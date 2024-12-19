package servlet.admin;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import model.Admin;
import dao.AdminDao;
import dao.impl.AdminDaoImpl;

/**
 * 管理员登录Servlet
 * 处理管理员的登录验证
 * 
 * Created time: 2024-12-18
 * @author null7777777
 * Last modified: 2024-12-18 17:15:12 UTC
 */
@WebServlet("/jsp/admin/LoginServlet")
public class LoginServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final String MAIN_PATH = "index.jsp";
    private static final String LOGIN_PATH = "login.jsp";
    private static final String ADMIN_SESSION_KEY = "adminUser";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        doPost(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        
        String userName = request.getParameter("userName");
        String passWord = request.getParameter("passWord");
        
        // 验证参数
        List<String> errorList = validateLoginParams(userName, passWord);
        
        if(errorList.isEmpty()) {
            // 执行登录
            try {
                if(processLogin(request, userName, passWord)) {
                    // 登录成功，重定向到主页
                    response.sendRedirect(MAIN_PATH);
                    return;
                } else {
                    errorList.add("用户名或密码错误，请重新输入");
                }
            } catch (Exception e) {
                errorList.add("登录失败：" + e.getMessage());
            }
        }
        
        // 登录失败，返回登录页面并显示错误信息
        request.setAttribute("infoList", errorList);
        request.getRequestDispatcher(LOGIN_PATH).forward(request, response);
    }
    
    /**
     * 验证登录参数
     * @param userName 用户名
     * @param passWord 密码
     * @return 错误信息列表
     */
    private List<String> validateLoginParams(String userName, String passWord) {
        List<String> errorList = new ArrayList<>();
        
        if(userName == null || userName.trim().isEmpty()) {
            errorList.add("用户名不能为空");
        }
        if(passWord == null || passWord.trim().isEmpty()) {
            errorList.add("密码不能为空");
        }
        
        return errorList;
    }
    
    /**
     * 处理登录逻辑
     * @param request HttpServletRequest对象
     * @param userName 用户名
     * @param passWord 密码
     * @return 登录是否成功
     */
    private boolean processLogin(HttpServletRequest request, String userName, String passWord) {
        AdminDao adminDao = new AdminDaoImpl();
        Admin admin = new Admin(userName, passWord);
        
        if(adminDao.userLogin(admin)) {
            // 登录成功，设置session
            HttpSession session = request.getSession();
            session.setAttribute(ADMIN_SESSION_KEY, admin);
            
            // 记录最后登录时间
            admin.setLastLoginTime(new java.util.Date());
            
            return true;
        }
        
        return false;
    }
}