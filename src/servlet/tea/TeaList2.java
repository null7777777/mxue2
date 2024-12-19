package servlet.tea;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

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
 * Last modified: 2024-12-19 05:10:50 UTC
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
            // 获取所有商品列表
            List<Tea> allTeaList = teaDao.teaList();
            
            // 处理分页参数
            int curPage = 1;
            String pageStr = request.getParameter("page");
            if (pageStr != null) {
                curPage = Integer.parseInt(pageStr);
            }
            
            // 根据搜索条件筛选商品
            List<Tea> filteredList;
            if (searchName != null && !searchName.trim().isEmpty()) {
                filteredList = allTeaList.stream()
                    .filter(tea -> tea.getTeaName().toLowerCase().contains(searchName.toLowerCase().trim()))
                    .collect(Collectors.toList());
                request.setAttribute("title", "搜索结果：" + searchName);
                request.setAttribute("searchName", searchName);
            } else {
                filteredList = allTeaList;
                request.setAttribute("title", "所有商品");
            }
            
            // 创建分页对象
            long totalCount = filteredList.size();
            PageBean pb = new PageBean(curPage, MAX_LIST_SIZE, totalCount);
            
            // 手动进行分页处理
            int startIndex = (pb.getCurPage() - 1) * MAX_LIST_SIZE;
            int endIndex = Math.min(startIndex + MAX_LIST_SIZE, filteredList.size());
            
            // 获取当前页的数据
            List<Tea> pageTeaList = filteredList.subList(
                startIndex, 
                endIndex
            );
            
            // 设置请求属性
            request.setAttribute("pageBean", pb);
            request.setAttribute("teaList", pageTeaList);
            
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