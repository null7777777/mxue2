package servlet.admin;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import model.Admin;
import model.PageBean;
import dao.AdminDao;
import dao.impl.AdminDaoImpl;

import net.sf.json.JSONObject;

/**
 * 管理员管理Servlet
 * 处理管理员的增删改查等操作
 * 
 * Created time: 2024-12-18
 * @author null7777777
 * Last modified: 2024-12-18 17:08:23 UTC
 */
@WebServlet("/jsp/admin/AdminManageServlet")
public class AdminManageServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final String ADMINLIST_PATH = "adminManage/adminList.jsp";
    private static final String ADMINADD_PATH = "adminManage/adminAdd.jsp";
    private static final String ADMINEDIT_PATH = "adminManage/adminEdit.jsp";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        String action = request.getParameter("action");
        if(action == null) {
            action = "list";
        }
        
        try {
            switch(action) {
                case "list":
                    adminList(request, response);
                    break;
                case "add":
                    adminAdd(request, response);
                    break;
                case "update":
                    adminUpdate(request, response);
                    break;
                case "edit":
                    adminEdit(request, response);
                    break;
                case "del":
                    adminDel(request, response);
                    break;
                case "batDel":
                    adminBatDel(request, response);
                    break;
                case "find":
                    adminFind(request, response);
                    break;
                default:
                    adminList(request, response);
            }
        } catch (Exception e) {
            request.setAttribute("adminMessage", "操作失败：" + e.getMessage());
            request.getRequestDispatcher(ADMINLIST_PATH).forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        doGet(request, response);
    }
    
    /**
     * 查询管理员列表
     */
    private void adminList(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        AdminDao adminDao = new AdminDaoImpl();
        
        try {
            // 处理分页参数
            int curPage = 1;
            String pageStr = request.getParameter("page");
            if(pageStr != null) {
                curPage = Integer.parseInt(pageStr);
            }
            
            // 获取每页显示大小
            int maxSize = Integer.parseInt(
                request.getServletContext().getInitParameter("maxPageSize")
            );
            
            // 获取总记录数并创建分页对象
            long totalCount = adminDao.userReadCount();
            PageBean pageBean = new PageBean(curPage, maxSize, totalCount);
            
            // 获取管理员列表
            request.setAttribute("adminList", adminDao.userList(pageBean));
            request.setAttribute("pageBean", pageBean);
            
            // 转发到列表页面
            request.getRequestDispatcher(ADMINLIST_PATH).forward(request, response);
            
        } catch (NumberFormatException e) {
            request.setAttribute("adminMessage", "页码参数无效");
            request.getRequestDispatcher(ADMINLIST_PATH).forward(request, response);
        }
    }
    
    /**
     * 添加管理员
     */
    private void adminAdd(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        AdminDao adminDao = new AdminDaoImpl();
        
        try {
            // 创建管理员对象
            Admin admin = new Admin(
                request.getParameter("userName"),
                request.getParameter("passWord"),
                request.getParameter("name")
            );
            
            // 检查用户名是否存在
            if(adminDao.findUser(admin.getUserName())) {
                request.setAttribute("adminMessage", "添加失败：用户名已存在");
                request.getRequestDispatcher(ADMINADD_PATH).forward(request, response);
                return;
            }
            
            // 执行添加操作
            if(adminDao.userAdd(admin)) {
                request.setAttribute("adminMessage", "添加成功");
                adminList(request, response);
            } else {
                request.setAttribute("adminMessage", "添加失败");
                request.getRequestDispatcher(ADMINADD_PATH).forward(request, response);
            }
        } catch (Exception e) {
            request.setAttribute("adminMessage", "添加失败：" + e.getMessage());
            request.getRequestDispatcher(ADMINADD_PATH).forward(request, response);
        }
    }
    
    /**
     * 更新管理员信息
     */
    private void adminUpdate(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        AdminDao adminDao = new AdminDaoImpl();
        
        try {
            // 创建管理员对象
            Admin admin = new Admin(
                Integer.parseInt(request.getParameter("id")),
                request.getParameter("passWord"),
                request.getParameter("name")
            );
            
            // 执行更新操作
            if(adminDao.userUpdate(admin)) {
                request.setAttribute("adminMessage", "更新成功");
                adminList(request, response);
            } else {
                request.setAttribute("adminMessage", "更新失败");
                request.setAttribute("adminInfo", adminDao.findUser(admin.getId()));
                request.getRequestDispatcher(ADMINEDIT_PATH).forward(request, response);
            }
        } catch (NumberFormatException e) {
            request.setAttribute("adminMessage", "更新失败：ID参数无效");
            request.getRequestDispatcher(ADMINEDIT_PATH).forward(request, response);
        }
    }
    
    /**
     * 编辑管理员
     */
    private void adminEdit(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        try {
            int id = Integer.parseInt(request.getParameter("id"));
            AdminDao adminDao = new AdminDaoImpl();
            
            Admin admin = adminDao.findUser(id);
            if(admin != null) {
                request.setAttribute("adminInfo", admin);
                request.getRequestDispatcher(ADMINEDIT_PATH).forward(request, response);
            } else {
                request.setAttribute("adminMessage", "管理员不存在");
                adminList(request, response);
            }
        } catch (NumberFormatException e) {
            request.setAttribute("adminMessage", "ID参数无效");
            adminList(request, response);
        }
    }
    
    /**
     * 删除管理员
     */
    private void adminDel(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        try {
            int id = Integer.parseInt(request.getParameter("id"));
            AdminDao adminDao = new AdminDaoImpl();
            
            if(adminDao.delUser(id)) {
                request.setAttribute("adminMessage", "删除成功");
            } else {
                request.setAttribute("adminMessage", "删除失败");
            }
            adminList(request, response);
        } catch (NumberFormatException e) {
            request.setAttribute("adminMessage", "删除失败：ID参数无效");
            adminList(request, response);
        }
    }
    
    /**
     * 批量删除管理员
     */
    private void adminBatDel(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        String ids = request.getParameter("ids");
        if(ids == null || ids.trim().isEmpty()) {
            request.setAttribute("adminMessage", "请选择要删除的管理员");
            adminList(request, response);
            return;
        }
        
        AdminDao adminDao = new AdminDaoImpl();
        if(adminDao.batDelUser(ids)) {
            request.setAttribute("adminMessage", "批量删除成功");
        } else {
            request.setAttribute("adminMessage", "批量删除失败");
        }
        adminList(request, response);
    }
    
    /**
     * AJAX检查用户名是否存在
     */
    private void adminFind(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        String userName = request.getParameter("param");
        AdminDao adminDao = new AdminDaoImpl();
        
        response.setContentType("application/json;charset=UTF-8");
        JSONObject json = new JSONObject();
        
        if(adminDao.findUser(userName)) {
            json.put("info", "用户名已存在");
            json.put("status", "n");
        } else {
            json.put("info", "用户名可以使用");
            json.put("status", "y");
        }
        
        response.getWriter().write(json.toString());
    }
}