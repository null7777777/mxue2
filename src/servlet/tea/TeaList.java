package servlet.tea;

import dao.TeaDao;
import dao.impl.TeaDaoImpl;
import model.Tea;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet("/shop/tea/list")
public class TeaList extends HttpServlet {
    private TeaDao teaDao = new TeaDaoImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        String catalogId = request.getParameter("catalogId");
        try {
            if (catalogId != null && !catalogId.trim().isEmpty()) {
                // 按分类查询
                List<Tea> teaList = teaDao.findTeaByCatalogId(Integer.parseInt(catalogId));
                request.setAttribute("teaList", teaList);
            } else {
                // 查询所有
                List<Tea> teaList = teaDao.teaList();
                request.setAttribute("teaList", teaList);
            }
            
            // 转发到列表页面
            request.getRequestDispatcher("/WEB-INF/jsp/shop/tea/list.jsp").forward(request, response);
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/error.jsp");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        doGet(request, response);
    }
}