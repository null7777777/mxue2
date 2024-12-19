package servlet.tea;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import model.User;
import dao.UserDao;
import dao.impl.UserDaoImpl;

import net.sf.json.JSONObject;

/**
 * 用户前台操作Servlet
 * 处理用户登录、注册等操作
 * 
 * Created time: 2024-12-18
 * @author null7777777
 * Last modified: 2024-12-18 17:05:41 UTC
 */
@WebServlet("/UserServlet")
public class UserServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final String LOGIN_PATH = "jsp/tea/reg.jsp?type=login";
    private static final String REG_PATH = "jsp/tea/reg.jsp?type=reg";
    private static final String INDEX_PATH = "jsp/tea/index.jsp";
    private static final String LANDING = "landing"; // 前台用户session标识

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        doPost(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        String action = request.getParameter("action");
        switch(action) {
            case "login":
                login(request, response);
                break;
            case "off":
                offlogin(request, response);
                break;
            case "ajlogin":
                ajlogin(request, response);
                break;
            case "reg":
                reg(request, response);
                break;
            case "landstatus":
                landstatus(request, response);
                break;
        }
    }

    /**
     * 判断用户登录状态
     */
    private void landstatus(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        User user = (User) request.getSession().getAttribute(LANDING);
        
        response.setContentType("application/json;charset=UTF-8");
        PrintWriter pw = response.getWriter();
        JSONObject json = new JSONObject();
        
        json.put("status", user != null ? "y" : "n");
        pw.print(json.toString());
        pw.flush();
        pw.close();
    }

    /**
     * AJAX登录处理
     */
    private void ajlogin(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        String userName = request.getParameter("userName");
        String passWord = request.getParameter("passWord");
        
        response.setContentType("application/json;charset=UTF-8");
        PrintWriter pw = response.getWriter();
        JSONObject json = new JSONObject();
        
        UserDao userDao = new UserDaoImpl();
        User user = userDao.userLogin(userName, passWord);
        
        if(user != null) {
            // 登录成功，设置session
            request.getSession().setAttribute(LANDING, user);
            json.put("status", "y");
        } else {
            json.put("info", "用户名或密码错误，请重新登录！");
        }
        
        pw.print(json.toString());
        pw.flush();
        pw.close();
    }

    /**
     * 用户注册
     */
    private void reg(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        UserDao userDao = new UserDaoImpl();
        
        try {
            // 检查用户名是否存在
            String userName = request.getParameter("userName");
            if(userDao.checkUserName(userName)) {
                request.setAttribute("infoList", "注册失败：用户名已存在");
                request.getRequestDispatcher(REG_PATH).forward(request, response);
                return;
            }
            
            // 创建用户对象
            User user = new User();
            user.setUserName(userName);
            user.setUserPassWord(request.getParameter("passWord"));
            user.setName(request.getParameter("name"));
            user.setSex(request.getParameter("sex"));
            user.setAge(Integer.parseInt(request.getParameter("age")));
            user.setTell(request.getParameter("tell"));
            user.setAddress(request.getParameter("address"));
            
            // 执行注册
            if(userDao.userRegister(user)) {
                request.setAttribute("infoList", "注册成功！请登录！");
                request.getRequestDispatcher(LOGIN_PATH).forward(request, response);
            } else {
                request.setAttribute("infoList", "注册失败：系统错误");
                request.getRequestDispatcher(REG_PATH).forward(request, response);
            }
            
        } catch(NumberFormatException e) {
            request.setAttribute("infoList", "注册失败：年龄必须是数字");
            request.getRequestDispatcher(REG_PATH).forward(request, response);
        } catch(Exception e) {
            request.setAttribute("infoList", "注册失败：" + e.getMessage());
            request.getRequestDispatcher(REG_PATH).forward(request, response);
        }
    }

    /**
     * 退出登录
     */
    private void offlogin(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        HttpSession session = request.getSession();
        session.removeAttribute(LANDING);
        response.sendRedirect(INDEX_PATH);
    }

    /**
     * 用户登录
     */
    private void login(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        String userName = request.getParameter("userName");
        String passWord = request.getParameter("passWord");
        
        UserDao userDao = new UserDaoImpl();
        User user = userDao.userLogin(userName, passWord);
        
        if(user != null) {
            // 登录成功，设置session并跳转到首页
            request.getSession().setAttribute(LANDING, user);
            response.sendRedirect(INDEX_PATH);
        } else {
            // 登录失败，返回登录页面并显示错误信息
            request.setAttribute("infoList", "用户名或密码错误，请重新登录！");
            request.getRequestDispatcher(LOGIN_PATH).forward(request, response);
        }
    }
}