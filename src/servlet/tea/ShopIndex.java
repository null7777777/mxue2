package servlet.tea;

import java.io.IOException;
import java.io.PrintWriter;
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

import net.sf.json.JSONObject;

/**
 * 商店首页Servlet
 * 获取推荐商品和最新商品列表
 * 
 * Created time: 2024-12-18
 * @author null7777777
 * Last modified: 2024-12-18 17:54:04 UTC
 */
@WebServlet("/ShopIndex")
public class ShopIndex extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final int PAGE_SIZE = 4;  // 每页显示数量

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        doPost(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        response.setContentType("application/json;charset=UTF-8");
        JSONObject json = new JSONObject();
        
        TeaDao teaDao = new TeaDaoImpl();
        
        try {
            // 获取推荐商品总数
            long recCount = teaDao.teaSearchCount("recommend=true");
            PageBean recPb = new PageBean(1, PAGE_SIZE, recCount);
            
            // 获取最新商品总数
            long newCount = teaDao.teaReadCount();
            PageBean newPb = new PageBean(1, PAGE_SIZE, newCount);
            
            // 获取推荐商品
            List<Tea> recTeas = teaDao.findRecommendTea(recPb);
            json.put("recTeas", recTeas);
            
            // 获取最新商品（按添加时间降序排序）
            List<Tea> newTeas = teaDao.teaList(newPb);
            json.put("newTeas", newTeas);
            
            // 添加分页信息
            json.put("recPages", recPb);
            json.put("newPages", newPb);
            json.put("success", true);
            
        } catch (Exception e) {
            json.put("success", false);
            json.put("message", "获取商品列表失败：" + e.getMessage());
            e.printStackTrace();
        }
        
        // 输出JSON响应
        PrintWriter pw = response.getWriter();
        pw.print(json);
        pw.flush();
        pw.close();
    }
}