package dao.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import model.Catalog;
import model.PageBean;
import dao.CatalogDao;
import utils.C3P0Util;

public class CatalogDaoImpl implements CatalogDao {
    
    // 获取商品的分类信息（分页）
    @Override
    public List<Catalog> catalogList(PageBean pb) {
        List<Catalog> list = new ArrayList<Catalog>();
        String sql = "select * from s_catalog limit ?,?";
        
        // 查询的分页结果集
        List<Map<String, Object>> lm = C3P0Util.executeQuery(sql, 
            (pb.getCurPage() - 1) * pb.getMaxSize(),
            pb.getMaxSize()
        );
        
        if (lm.size() > 0) {
            for (Map<String, Object> map : lm) {
                Catalog catalog = new Catalog(map);
                list.add(catalog);
            }
        }
        return list;
    }

    // 统计商品的种类数
    @Override
    public long catalogReadCount() {
        long count = 0;
        String sql = "select count(*) as count from s_catalog";
        
        List<Map<String, Object>> lm = C3P0Util.executeQuery(sql);
        
        if (lm.size() > 0) {
            count = ((Number) lm.get(0).get("count")).longValue();
        }
        return count;
    }

    // 获取所有分类
    @Override
    public List<Catalog> getCatalog() {
        List<Catalog> list = new ArrayList<Catalog>();
        String sql = "select * from s_catalog";
        
        List<Map<String, Object>> lmso = C3P0Util.executeQuery(sql);
        
        if (lmso.size() > 0) {
            for (Map<String, Object> map : lmso) {
                Catalog catalog = new Catalog(map);
                list.add(catalog);
            }
        }
        return list;
    }

    // 根据分类id删除某一分类
    @Override
    public boolean catalogDel(int catalogId) {
        String sql = "delete from s_catalog where catalogId=?";
        
        int i = C3P0Util.executeUpdate(sql, catalogId);
        return i > 0;
    }

    // 根据分类id集批量删除分类
    @Override
    public boolean catalogBatDelById(String ids) {
        String sql = "delete from s_catalog where catalogId in (" + ids + ")";
        
        int i = C3P0Util.executeUpdate(sql);
        return i > 0;
    }

    // 根据分类名查询某一分类
    @Override
    public boolean findCatalogByCatalogName(String catalogName) {
        String sql = "select * from s_catalog where catalogName=?";
        
        List<Map<String, Object>> list = C3P0Util.executeQuery(sql, catalogName);
        return list.size() > 0;
    }

    // 添加分类
    @Override
    public boolean catalogAdd(String catalogName) {
        String sql = "insert into s_catalog(catalogName) values(?)";
        
        int i = C3P0Util.executeUpdate(sql, catalogName);
        return i > 0;
    }

    // 根据分类ID查找分类
    @Override
    public Catalog findCatalogById(int catalogId) {
        String sql = "select * from s_catalog where catalogId=?";
        Catalog catalog = null;
        
        List<Map<String, Object>> list = C3P0Util.executeQuery(sql, catalogId);
        
        if (list.size() > 0) {
            catalog = new Catalog(list.get(0));
        }
        return catalog;
    }

    // 更新分类信息
    @Override
    public boolean catalogUpdate(Catalog catalog) {
        String sql = "update s_catalog set catalogName=? where catalogId=?";
        
        int i = C3P0Util.executeUpdate(sql, 
            catalog.getCatalogName(),
            catalog.getCatalogId()
        );
        return i > 0;
    }
}