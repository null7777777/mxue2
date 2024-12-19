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
import model.Tea; 
import dao.CatalogDao;
import dao.impl.TeaDaoImpl;
import dao.impl.CatalogDaoImpl;

import net.sf.json.JSONObject;

/**
 * 获取分类项及其商品数量的Servlet
 * 
 * Created time: 2024-12-18
 * @author null7777777
 * Last modified: 2024-12-19 05:07:57 UTC
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
            
            // 获取分类总数
            long totalCount = catalogDao.catalogReadCount();
            
            // 创建PageBean并设置参数
            PageBean pb = new PageBean();
            pb.setCurPage(1);
            pb.setMaxSize(Integer.MAX_VALUE); // 设置一个足够大的数以获取所有记录
            pb.setReadCount(totalCount);
            pb.updatePage();
            
            // 获取所有分类
            List<Catalog> catalogs = catalogDao.getCatalog(); // 使用getCatalog()方法替代catalogList(pb)
            
            // 获取每个分类的商品数量
            if (catalogs != null && !catalogs.isEmpty()) {
                for (Catalog catalog : catalogs) {
                    // 获取该分类下的茶品数量
                    List<Tea> teasInCatalog = teaDao.findTeaByCatalogId(catalog.getCatalogId());
                    catalog.setCatalogSize(teasInCatalog != null ? teasInCatalog.size() : 0);
                }
                
                // 将分类列表添加到JSON响应中
                json.put("catalog", catalogs);
                json.put("success", true);
            } else {
                json.put("success", false);
                json.put("message", "未找到任何分类信息");
            }
            
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