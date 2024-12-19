package servlet.admin;

import java.io.IOException;
import java.util.List; 
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import model.Admin;
import model.PageBean;
import model.User;
import dao.AdminDao;
import dao.OrderDao;
import dao.UserDao;
import dao.impl.AdminDaoImpl;
import dao.impl.OrderDaoImpl;
import dao.impl.UserDaoImpl;

import net.sf.json.JSONObject;

/**
 * 用户管理Servlet
 * 创建时间: 2024-12-18
 * 最后修改: 2024-12-18 17:46:01 UTC
 * @author null7777777
 */
@WebServlet("/jsp/admin/UserManageServlet")
public class UserManageServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    
    // 页面路径常量
    private static final String USERLIST_PATH = "userManage/userList.jsp";        // 用户列表页面
    private static final String USERADD_PATH = "userManage/userAdd.jsp";         // 用户添加页面
    private static final String USEREDIT_PATH = "userManage/userEdit.jsp";       // 用户编辑页面
    private static final String USERDETAIL_PATH = "userManage/userDetail.jsp";   // 用户详情页面

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        doPost(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        String action = request.getParameter("action");
        if(action == null) {
            action = "list";
        }
        
        switch(action) {
            case "list":
                userList(request, response);
                break;
            case "add":
                userAdd(request, response);
                break;
            case "update":
                userUpdate(request, response);
                break;
            case "edit":
                userEdit(request, response);
                break;
            case "del":
                userDel(request, response);
                break;
            case "batDel":
                userBatchDel(request, response);
                break;
            case "find":
                checkUsername(request, response);
                break;
            case "detail":
                userDetail(request, response);
                break;
            case "search":
                searchUser(request, response);
                break;
        }
    }
    
    /**
     * 搜索用户
     * 支持按用户名、姓名或电话模糊搜索
     */
    private void searchUser(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        UserDao ud = new UserDaoImpl();
        int curPage = 1;
        String page = request.getParameter("page");
        if(page != null) {
            curPage = Integer.parseInt(page);
        }
        
        // 获取每页显示数量
        int maxSize = Integer.parseInt(request.getServletContext().getInitParameter("maxPageSize"));
        String keyword = request.getParameter("keyword");
        PageBean pageBean = null;
        
        if(keyword != null && !keyword.trim().isEmpty()) {
            // 使用搜索功能
            pageBean = new PageBean(curPage, maxSize, ud.searchUserCount(keyword));
            request.setAttribute("userList", ud.searchUser(keyword, pageBean));
            request.setAttribute("keyword", keyword); // 保存搜索关键词用于页面显示
        } else {
            // 显示所有用户
            pageBean = new PageBean(curPage, maxSize, ud.userReadCount());
            request.setAttribute("userList", ud.userList(pageBean));
        }
        
        request.setAttribute("pageBean", pageBean);
        request.getRequestDispatcher(USERLIST_PATH).forward(request, response);
    }

    /**
     * 显示用户详细信息
     */
    private void userDetail(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        String idStr = request.getParameter("id");
        if(idStr != null && !idStr.trim().isEmpty()) {
            try {
                int userId = Integer.parseInt(idStr);
                UserDao ud = new UserDaoImpl();
                User user = ud.findUserById(userId);
                
                if(user != null) {
                    request.setAttribute("userInfo", user);
                    request.getRequestDispatcher(USERDETAIL_PATH).forward(request, response);
                    return;
                }
            } catch(NumberFormatException e) {
                // ID格式错误处理
            }
        }
        // 如果获取用户信息失败，返回列表页面并显示错误信息
        request.setAttribute("userMessage", "获取用户信息失败");
        userList(request, response);
    }

    /**
     * 批量删除用户
     */
    private void userBatchDel(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        String ids = request.getParameter("ids");
        if(ids != null && !ids.trim().isEmpty()) {
            UserDao ud = new UserDaoImpl();
            if(ud.userBatchDel(ids)) {
                request.setAttribute("userMessage", "已成功删除选中用户");
            } else {
                request.setAttribute("userMessage", "批量删除用户失败");
            }
        } else {
            request.setAttribute("userMessage", "未选择要删除的用户");
        }
        userList(request, response);
    }
    
    /**
     * 删除单个用户
     */
    private void userDel(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        String idStr = request.getParameter("id");
        if(idStr != null && !idStr.trim().isEmpty()) {
            try {
                int userId = Integer.parseInt(idStr);
                UserDao ud = new UserDaoImpl();
                if(ud.userDel(userId)) {
                    request.setAttribute("userMessage", "用户删除成功");
                } else {
                    request.setAttribute("userMessage", "用户删除失败");
                }
            } catch(NumberFormatException e) {
                request.setAttribute("userMessage", "用户ID格式错误");
            }
        } else {
            request.setAttribute("userMessage", "未指定要删除的用户");
        }
        userList(request, response);
    }

    /**
     * 更新用户信息
     */
    private void userUpdate(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        try {
            User user = new User(
                Integer.parseInt(request.getParameter("userId")),
                request.getParameter("userPassWord"),
                request.getParameter("name"),
                request.getParameter("sex"),
                Integer.parseInt(request.getParameter("age")),
                request.getParameter("tell"),
                request.getParameter("address"),
                request.getParameter("enabled")
            );
            
            UserDao ud = new UserDaoImpl();
            if(ud.userUpdate(user)) {
                request.setAttribute("userMessage", "用户信息更新成功");
                userList(request, response);
            } else {
                request.setAttribute("userMessage", "用户信息更新失败");
                request.setAttribute("userInfo", ud.findUserById(user.getUserId()));
                request.getRequestDispatcher(USEREDIT_PATH).forward(request, response);
            }
        } catch(NumberFormatException e) {
            request.setAttribute("userMessage", "输入数据格式错误");
            request.getRequestDispatcher(USEREDIT_PATH).forward(request, response);
        }
    }

    /**
     * 加载用户编辑页面
     */
    private void userEdit(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        String idStr = request.getParameter("id");
        if(idStr != null && !idStr.trim().isEmpty()) {
            try {
                int userId = Integer.parseInt(idStr);
                UserDao ud = new UserDaoImpl();
                User user = ud.findUserById(userId);
                if(user != null) {
                    request.setAttribute("userInfo", user);
                    request.getRequestDispatcher(USEREDIT_PATH).forward(request, response);
                    return;
                }
            } catch(NumberFormatException e) {
                // ID格式错误处理
            }
        }
        request.setAttribute("userMessage", "获取用户信息失败");
        userList(request, response);
    }

    /**
     * 检查用户名是否已存在
     * 返回JSON格式响应
     */
    private void checkUsername(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        String userName = request.getParameter("param");
        UserDao ud = new UserDaoImpl();
        
        JSONObject json = new JSONObject();
        if(userName != null && !userName.trim().isEmpty()) {
            if(ud.checkUserName(userName)) {
                json.put("info", "用户名已存在");
                json.put("status", "n");
            } else {
                json.put("info", "用户名可以使用");
                json.put("status", "y");
            }
        } else {
            json.put("info", "用户名不能为空");
            json.put("status", "n");
        }
        
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(json.toString());
    }
    
    /**
     * 添加新用户
     * 创建时间: 2024-12-18
     * @author null7777777
     */
    private void userAdd(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        try {
            // 创建用户对象并设置基本信息
            User user = new User(
                request.getParameter("userName"),
                request.getParameter("userPassWord"),
                request.getParameter("name"),
                request.getParameter("sex"),
                Integer.parseInt(request.getParameter("age")),
                request.getParameter("tell"),
                request.getParameter("address")
            );
            // 设置用户状态为启用
            user.setEnabled("y");
            
            UserDao ud = new UserDaoImpl();
            // 检查用户名是否已存在
            if(ud.checkUserName(user.getUserName())) {
                request.setAttribute("userMessage", "用户添加失败！用户名已存在");
                request.getRequestDispatcher(USERADD_PATH).forward(request, response);
                return;
            }
            
            // 执行用户注册
            if(ud.userRegister(user)) {
                request.setAttribute("userMessage", "用户添加成功！");
                userList(request, response);
            } else {
                request.setAttribute("userMessage", "用户添加失败！");
                request.getRequestDispatcher(USERADD_PATH).forward(request, response);
            }
        } catch(NumberFormatException e) {
            request.setAttribute("userMessage", "输入数据格式错误");
            request.getRequestDispatcher(USERADD_PATH).forward(request, response);
        }
    }

    /**
     * 分页显示用户列表
     * 创建时间: 2024-12-18
     * @author null7777777
     */
    private void userList(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        UserDao ud = new UserDaoImpl();
        
        // 获取当前页码
        int curPage = 1;
        String pageStr = request.getParameter("page");
        if(pageStr != null && !pageStr.trim().isEmpty()) {
            try {
                curPage = Integer.parseInt(pageStr);
                if(curPage < 1) {
                    curPage = 1;
                }
            } catch(NumberFormatException e) {
                // 页码格式错误，使用默认值1
            }
        }
        
        // 获取系统配置的每页显示数量
        int maxSize = Integer.parseInt(
            request.getServletContext().getInitParameter("maxPageSize")
        );
        
        // 创建分页对象
        long totalCount = ud.userReadCount();
        PageBean pageBean = new PageBean(curPage, maxSize, totalCount);
        
        // 获取用户列表数据
        List<User> userList = ud.userList(pageBean);
        
        // 设置请求属性
        request.setAttribute("userList", userList);
        request.setAttribute("pageBean", pageBean);
        
        // 转发到用户列表页面
        request.getRequestDispatcher(USERLIST_PATH).forward(request, response);
    }
}