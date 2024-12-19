package servlet.tea;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import model.Tea;
import model.PageBean;
import dao.TeaDao;
import dao.impl.TeaDaoImpl;

/**
 * 商品搜索列表Servlet
 * 处理商品搜索功能，支持按名称搜索
 * 
 * Created time: 2024-12-18
 * @author null7777777
 * Last modified: 2024-12-18 17:02:52 UTC
 */
@WebServlet("/TeaList2")
public class TeaList2 extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final int MAX_LIST_SIZE = 12;
    private static final String TEALIST_PATH = "jsp/tea/tealist.jsp";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        try {
            // 获取搜索关键词，处理中文编码
            String searchName = request.getParameter("seachname");
            if (searchName != null) {
                searchName = new String(searchName.getBytes("iso-8859-1"), "utf-8");
            }
            
            // 调用商品列表方法
            teaList(request, response, searchName);
            
        } catch (Exception e) {
            request.setAttribute("error", "搜索失败：" + e.getMessage());
            request.getRequestDispatcher(TEALIST_PATH).forward(request, response);
        }
    }

    /**
     * 显示商品列表
     * 支持搜索功能
     */
    private void teaList(HttpServletRequest request, HttpServletResponse response, String searchName) 
            throws ServletException, IOException {
        TeaDao teaDao = new TeaDaoImpl();
        
        try {
            // 处理分页参数
            int curPage = 1;
            String pageStr = request.getParameter("page");
            if (pageStr != null) {
                curPage = Integer.parseInt(pageStr);
            }
            
            // 创建分页对象和获取商品列表
            List<Tea> teaList;
            PageBean pb;
            
            if (searchName == null || searchName.trim().isEmpty()) {
                // 获取所有商品的总数
                long totalCount = teaDao.teaReadCount();
                
                // 创建分页对象
                pb = new PageBean(curPage, MAX_LIST_SIZE, totalCount);
                
                // 获取所有商品列表
                teaList = teaDao.teaList(pb);
                
                request.setAttribute("title", "所有商品");
                
            } else {
                // 使用搜索功能获取商品列表
                pb = new PageBean(curPage, MAX_LIST_SIZE, 0); // 先创建分页对象
                
                // 使用搜索方法获取商品列表
                teaList = teaDao.searchTea(searchName, pb);
                
                // 更新总记录数
                pb.setReadCount(teaList.size());
                pb.updatePage();
                
                request.setAttribute("title", "搜索结果：" + searchName);
                request.setAttribute("searchName", searchName);
            }

            // 设置请求属性
            request.setAttribute("pageBean", pb);
            request.setAttribute("teaList", teaList);
            
            // 转发到列表页面
            request.getRequestDispatcher(TEALIST_PATH).forward(request, response);
            
        } catch (NumberFormatException e) {
            request.setAttribute("error", "无效的页码");
            request.getRequestDispatcher(TEALIST_PATH).forward(request, response);
        } catch (Exception e) {
            request.setAttribute("error", "获取商品列表失败：" + e.getMessage());
            request.getRequestDispatcher(TEALIST_PATH).forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        doGet(request, response);
    }
}