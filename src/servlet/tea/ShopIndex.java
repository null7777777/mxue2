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

@WebServlet("/shop/index")
public class ShopIndex extends HttpServlet {
    private TeaDao teaDao = new TeaDaoImpl();
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        doPost(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        try {
            // 获取推荐饮品列表（显示最新添加的6个）
            List<Tea> recommendList = teaDao.findRecommendTea(6);
            request.setAttribute("recommendList", recommendList);
            
            // 转发到首页
            request.getRequestDispatcher("/WEB-INF/jsp/shop/index.jsp").forward(request, response);
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/error.jsp");
        }
    }
}