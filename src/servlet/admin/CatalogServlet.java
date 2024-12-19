package servlet.admin;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import model.Catalog;
import model.PageBean;
import dao.CatalogDao;
import dao.impl.CatalogDaoImpl;

import net.sf.json.JSONObject;

/**
 * 商品分类管理Servlet
 * 提供分类的增删改查等功能
 * 
 * Created time: 2024-12-18
 * @author null7777777
 * Last modified: 2024-12-18 17:11:47 UTC
 */
@WebServlet("/jsp/admin/CatalogServlet")
public class CatalogServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final String CATALOGLIST_PATH = "teaManage/catalogList.jsp";
    private static final String CATALOGADD_PATH = "teaManage/catalogAdd.jsp";
    private static final String CATALOGEDIT_PATH = "teaManage/catalogEdit.jsp";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        doPost(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String action = request.getParameter("action");
        if(action == null) {
            action = "list";
        }
        
        try {
            switch(action) {
                case "list":
                    catalogList(request, response);
                    break;
                case "add":
                    catalogAdd(request, response);
                    break;
                case "edit":
                    catalogEdit(request, response);
                    break;
                case "update":
                    catalogUpdate(request, response);
                    break;
                case "del":
                    catalogDel(request, response);
                    break;
                case "batDel":
                    catalogBatDel(request, response);
                    break;
                case "find":
                    catalogFind(request, response);
                    break;
                default:
                    catalogList(request, response);
            }
        } catch (Exception e) {
            request.setAttribute("catalogMessage", "操作失败：" + e.getMessage());
            request.getRequestDispatcher(CATALOGLIST_PATH).forward(request, response);
        }
    }

    /**
     * 获取分类列表
     */
    private void catalogList(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        CatalogDao catalogDao = new CatalogDaoImpl();
        
        try {
            // 处理分页参数
            int curPage = 1;
            String pageStr = request.getParameter("page");
            if(pageStr != null) {
                curPage = Integer.parseInt(pageStr);
            }
            
            // 获取每页显示数量
            int maxSize = Integer.parseInt(
                request.getServletContext().getInitParameter("maxPageSize")
            );
            
            // 创建分页对象
            long totalCount = catalogDao.catalogReadCount();
            PageBean pb = new PageBean(curPage, maxSize, totalCount);
            
            // 获取分类列表
            request.setAttribute("catalogList", catalogDao.catalogList(pb));
            request.setAttribute("pageBean", pb);
            
            // 转发到列表页面
            request.getRequestDispatcher(CATALOGLIST_PATH).forward(request, response);
            
        } catch (NumberFormatException e) {
            request.setAttribute("catalogMessage", "页码参数无效");
            request.getRequestDispatcher(CATALOGLIST_PATH).forward(request, response);
        }
    }

    /**
     * 添加分类
     */
    private void catalogAdd(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        String catalogName = request.getParameter("catalogName");
        if(catalogName == null || catalogName.trim().isEmpty()) {
            request.setAttribute("catalogMessage", "分类名称不能为空");
            request.getRequestDispatcher(CATALOGADD_PATH).forward(request, response);
            return;
        }
        
        CatalogDao catalogDao = new CatalogDaoImpl();
        
        // 检查分类名是否已存在
        if(catalogDao.findCatalogByCatalogName(catalogName)) {
            request.setAttribute("catalogMessage", "该分类名已存在");
            request.getRequestDispatcher(CATALOGADD_PATH).forward(request, response);
            return;
        }
        
        // 执行添加操作
        if(catalogDao.catalogAdd(catalogName)) {
            request.setAttribute("catalogMessage", "添加成功");
            catalogList(request, response);
        } else {
            request.setAttribute("catalogMessage", "添加失败");
            request.getRequestDispatcher(CATALOGADD_PATH).forward(request, response);
        }
    }

    /**
     * 编辑分类页面
     */
    private void catalogEdit(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        try {
            int catalogId = Integer.parseInt(request.getParameter("id"));
            CatalogDao catalogDao = new CatalogDaoImpl();
            
            Catalog catalog = catalogDao.findCatalogById(catalogId);
            if(catalog != null) {
                request.setAttribute("catalog", catalog);
                request.getRequestDispatcher(CATALOGEDIT_PATH).forward(request, response);
            } else {
                request.setAttribute("catalogMessage", "分类不存在");
                catalogList(request, response);
            }
        } catch (NumberFormatException e) {
            request.setAttribute("catalogMessage", "无效的分类ID");
            catalogList(request, response);
        }
    }

    /**
     * 更新分类信息
     */
    private void catalogUpdate(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        try {
            int catalogId = Integer.parseInt(request.getParameter("catalogId"));
            String catalogName = request.getParameter("catalogName");
            
            if(catalogName == null || catalogName.trim().isEmpty()) {
                request.setAttribute("catalogMessage", "分类名称不能为空");
                catalogEdit(request, response);
                return;
            }
            
            Catalog catalog = new Catalog();
            catalog.setCatalogId(catalogId);
            catalog.setCatalogName(catalogName);
            
            CatalogDao catalogDao = new CatalogDaoImpl();
            
            // 检查新名称是否与其他分类重复
            if(!catalog.getCatalogName().equals(
                    catalogDao.findCatalogById(catalogId).getCatalogName()) 
               && catalogDao.findCatalogByCatalogName(catalogName)) {
                request.setAttribute("catalogMessage", "该分类名已存在");
                catalogEdit(request, response);
                return;
            }
            
            if(catalogDao.catalogUpdate(catalog)) {
                request.setAttribute("catalogMessage", "更新成功");
                catalogList(request, response);
            } else {
                request.setAttribute("catalogMessage", "更新失败");
                catalogEdit(request, response);
            }
        } catch (NumberFormatException e) {
            request.setAttribute("catalogMessage", "无效的分类ID");
            catalogList(request, response);
        }
    }

    /**
     * 删除分类
     */
    private void catalogDel(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        try {
            int catalogId = Integer.parseInt(request.getParameter("id"));
            CatalogDao catalogDao = new CatalogDaoImpl();
            
            if(catalogDao.catalogDel(catalogId)) {
                request.setAttribute("catalogMessage", "删除成功");
            } else {
                request.setAttribute("catalogMessage", "删除失败");
            }
            catalogList(request, response);
        } catch (NumberFormatException e) {
            request.setAttribute("catalogMessage", "无效的分类ID");
            catalogList(request, response);
        }
    }

    /**
     * 批量删除分类
     */
    private void catalogBatDel(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        String ids = request.getParameter("ids");
        if(ids == null || ids.trim().isEmpty()) {
            request.setAttribute("catalogMessage", "请选择要删除的分类");
            catalogList(request, response);
            return;
        }
        
        CatalogDao catalogDao = new CatalogDaoImpl();
        if(catalogDao.catalogBatDelById(ids)) {
            request.setAttribute("catalogMessage", "批量删除成功");
        } else {
            request.setAttribute("catalogMessage", "批量删除失败");
        }
        catalogList(request, response);
    }

    /**
     * AJAX检查分类名是否存在
     */
    private void catalogFind(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        String catalogName = request.getParameter("param");
        
        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();
        
        try {
            CatalogDao catalogDao = new CatalogDaoImpl();
            JSONObject json = new JSONObject();
            
            if(catalogDao.findCatalogByCatalogName(catalogName)) {
                json.put("info", "该分类已存在");
                json.put("status", "n");
            } else {
                json.put("info", "分类名可用");
                json.put("status", "y");
            }
            
            out.write(json.toString());
            
        } finally {
            if(out != null) {
                out.flush();
                out.close();
            }
        }
    }
}