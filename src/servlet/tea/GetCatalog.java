package servlet.tea;

import java.io.IOException;


import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import model.Catalog;
import model.PageBean;  
import dao.TeaDao;
import dao.CatalogDao;
import dao.impl.TeaDaoImpl;
import dao.impl.CatalogDaoImpl;

import net.sf.json.JSONObject;

/**
 * 获取分类项及其商品数量的Servlet
 * 
 * Created time: 2024-12-18
 * @author null7777777
 * Last modified: 2024-12-18 16:35:21 UTC
 */
@WebServlet("/GetCatalog")
public class GetCatalog extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        response.setContentType("application/json;charset=UTF-8");
        JSONObject json = new JSONObject();
        
        try {
            // 创建DAO实例
            CatalogDao catalogDao = new CatalogDaoImpl();
            TeaDao teaDao = new TeaDaoImpl();
            
            // 创建PageBean，设置为获取所有记录
            PageBean pb = new PageBean();
            pb.setCurPage(1);
            // 设置一个足够大的数字以获取所有分类
            pb.setMaxSize(1000);  
            
            // 获取所有分类
            List<Catalog> catalogs = catalogDao.catalogList(pb);
            
            // 获取每个分类的商品数量
            for (Catalog catalog : catalogs) {
                long count = teaDao.teaCountByCatalogId(catalog.getCatalogId());
                catalog.setCatalogSize(count);
            }
            
            // 将分类列表添加到JSON响应中
            json.put("catalog", catalogs);
            json.put("success", true);
            
        } catch (Exception e) {
            json.put("success", false);
            json.put("message", "获取分类信息失败：" + e.getMessage());
        }
        
        response.getWriter().write(json.toString());
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        doGet(request, response);
    }
}