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
 * 商品列表Servlet
 * 处理商品列表的显示，支持分类查询
 * 
 * Created time: 2024-12-18
 * @author null7777777
 * Last modified: 2024-12-18 17:56:27 UTC
 */
@WebServlet("/TeaList")
public class TeaList extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final int MAX_LIST_SIZE = 12;
    private static final String TEALIST_PATH = "jsp/tea/tealist.jsp";
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        String action = request.getParameter("action");
        if(action == null) {
            action = "list";
        }
        switch(action) {
            case "d":
                break;
            case "list":
                teaList(request, response);
                break;
            default:
                teaList(request, response);
                break;
        }
    }

    /**
     * 显示商品列表
     * 支持按分类显示或显示所有商品
     */
    private void teaList(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        TeaDao teaDao = new TeaDaoImpl();
        
        try {
            // 处理分页参数
            int curPage = 1;
            String pageStr = request.getParameter("page");
            if (pageStr != null && !pageStr.trim().isEmpty()) {
                curPage = Integer.parseInt(pageStr);
            }
            
            // 获取分类ID参数
            String catalogIdStr = request.getParameter("catalogId");
            
            // 创建分页对象和获取商品列表
            PageBean pb;
            List<Tea> teaList;
            
            if(catalogIdStr != null && !catalogIdStr.trim().isEmpty()) {
                // 按分类查询
                int catalogId = Integer.parseInt(catalogIdStr);
                
                // 获取分类下的商品总数
                long totalCount = teaDao.teaCountByCatalogId(catalogId);
                
                // 创建分页对象
                pb = new PageBean(curPage, MAX_LIST_SIZE, totalCount);
                
                // 获取分类下的商品列表
                teaList = teaDao.findTeaByCatalogId(catalogId, pb);
                
                // 设置页面标题
                if(!teaList.isEmpty()) {
                    request.setAttribute("title", teaList.get(0).getCatalog().getCatalogName());
                    request.setAttribute("catalogId", catalogId);
                } else {
                    request.setAttribute("title", "分类商品");
                }
                
            } else {
                // 查询所有商品
                long totalCount = teaDao.teaReadCount();
                pb = new PageBean(curPage, MAX_LIST_SIZE, totalCount);
                teaList = teaDao.teaList(pb);
                request.setAttribute("title", "所有商品");
            }
            
            // 设置请求属性
            request.setAttribute("pageBean", pb);
            request.setAttribute("teaList", teaList);
            
        } catch (NumberFormatException e) {
            request.setAttribute("error", "无效的页码或分类ID");
        } catch (Exception e) {
            request.setAttribute("error", "获取商品列表失败：" + e.getMessage());
            e.printStackTrace();
        }
        
        // 转发到列表页面
        request.getRequestDispatcher(TEALIST_PATH).forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        doGet(request, response);
    }
}