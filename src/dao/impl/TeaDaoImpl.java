package dao.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import model.Tea;
import model.PageBean;
import dao.TeaDao;
import utils.C3P0Util;

/**
 * 商品数据访问实现类
 * Created time: 2024-12-18
 * @author null7777777
 */
public class TeaDaoImpl implements TeaDao {

    @Override
    public List<Tea> teaList(PageBean pb) {
        List<Tea> list = new ArrayList<>();
        String sql = "SELECT t.*, c.catalogName, i.imgName, i.imgSrc, i.imgType " +
                    "FROM s_tea t " +
                    "LEFT JOIN s_catalog c ON t.catalogId = c.catalogId " +
                    "LEFT JOIN s_uploadimg i ON t.imgId = i.imgId " +
                    "ORDER BY t.addTime DESC LIMIT ?,?";
        
        List<Map<String, Object>> lm = C3P0Util.executeQuery(sql,
            (pb.getCurPage() - 1) * pb.getMaxSize(),
            pb.getMaxSize()
        );
        
        if (lm.size() > 0) {
            for (Map<String, Object> map : lm) {
                Tea tea = new Tea(map);
                list.add(tea);
            }
        }
        return list;
    }
    
    @Override
    public List<Tea> searchTea(String keyword, PageBean pb) {
        List<Tea> list = new ArrayList<>();
        String sql = "SELECT t.*, c.catalogName, i.imgName, i.imgSrc, i.imgType " +
                    "FROM s_tea t " +
                    "LEFT JOIN s_catalog c ON t.catalogId = c.catalogId " +
                    "LEFT JOIN s_uploadimg i ON t.imgId = i.imgId " +
                    "WHERE t.teaName LIKE ? OR t.description LIKE ? " +
                    "ORDER BY t.addTime DESC LIMIT ?,?";
        
        String pattern = "%" + keyword + "%";
        List<Map<String, Object>> lm = C3P0Util.executeQuery(sql,
            pattern, pattern,
            (pb.getCurPage() - 1) * pb.getMaxSize(),
            pb.getMaxSize()
        );
        
        if (lm.size() > 0) {
            for (Map<String, Object> map : lm) {
                Tea tea = new Tea(map);
                list.add(tea);
            }
        }
        return list;
    }

    @Override
    public long teaReadCount() {
        long count = 0;
        String sql = "SELECT COUNT(*) as count FROM s_tea";
        
        List<Map<String, Object>> lm = C3P0Util.executeQuery(sql);
        
        if (lm.size() > 0) {
            count = ((Number) lm.get(0).get("count")).longValue();
        }
        return count;
    }
    
    @Override
    public long teaSearchCount(String keyword) {
        long count = 0;
        String sql = "SELECT COUNT(*) as count FROM s_tea " +
                    "WHERE teaName LIKE ? OR description LIKE ?";
        
        String pattern = "%" + keyword + "%";
        List<Map<String, Object>> lm = C3P0Util.executeQuery(sql, pattern, pattern);
        
        if (lm.size() > 0) {
            count = ((Number) lm.get(0).get("count")).longValue();
        }
        return count;
    }

    @Override
    public boolean teaAdd(Tea tea) {
        String sql = "INSERT INTO s_tea(teaName,price,description,catalogId,imgId,addTime,recommend) " +
                    "VALUES(?,?,?,?,?,?,?)";
        
        int result = C3P0Util.executeUpdate(sql,
            tea.getTeaName(),
            tea.getPrice(),
            tea.getDescription(),
            tea.getCatalogId(),
            tea.getImgId(),
            new Date(),
            tea.isRecommend()
        );
        
        return result > 0;
    }

    @Override
    public boolean teaUpdate(Tea tea) {
        StringBuilder sql = new StringBuilder();
        sql.append("UPDATE s_tea SET teaName=?,price=?,description=?,catalogId=?,recommend=? ");
        sql.append("WHERE teaId=?");
        
        int result = C3P0Util.executeUpdate(sql.toString(),
            tea.getTeaName(),
            tea.getPrice(),
            tea.getDescription(),
            tea.getCatalogId(),
            tea.isRecommend(),
            tea.getTeaId()
        );
        
        return result > 0;
    }

    @Override
    public boolean teaDel(int id) {
        String sql = "DELETE FROM s_tea WHERE teaId=?";
        int result = C3P0Util.executeUpdate(sql, id);
        return result > 0;
    }

    @Override
    public Tea findTeaById(int id) {
        Tea tea = null;
        String sql = "SELECT t.*, c.catalogName, i.imgName, i.imgSrc, i.imgType " +
                    "FROM s_tea t " +
                    "LEFT JOIN s_catalog c ON t.catalogId = c.catalogId " +
                    "LEFT JOIN s_uploadimg i ON t.imgId = i.imgId " +
                    "WHERE t.teaId=?";
        
        List<Map<String, Object>> lm = C3P0Util.executeQuery(sql, id);
        
        if (lm.size() > 0) {
            tea = new Tea(lm.get(0));
        }
        return tea;
    }

    @Override
    public boolean findTeaByName(String teaName) {
        String sql = "SELECT COUNT(*) as count FROM s_tea WHERE teaName=?";
        
        List<Map<String, Object>> lm = C3P0Util.executeQuery(sql, teaName);
        
        if (lm.size() > 0) {
            int count = ((Number) lm.get(0).get("count")).intValue();
            return count > 0;
        }
        return false;
    }

    @Override
    public String findimgIdByIds(String ids) {
        StringBuilder imgIds = new StringBuilder();
        String sql = "SELECT imgId FROM s_tea WHERE teaId IN (" + ids + ")";
        
        List<Map<String, Object>> lm = C3P0Util.executeQuery(sql);
        
        if (lm.size() > 0) {
            for (Map<String, Object> map : lm) {
                imgIds.append(map.get("imgId")).append(",");
            }
            if (imgIds.length() > 0) {
                imgIds.deleteCharAt(imgIds.length() - 1);
            }
        }
        return imgIds.toString();
    }
    
    @Override
    public long teaCountByCatalogId(int catalogId) {
        long count = 0;
        String sql = "SELECT COUNT(*) as count FROM s_tea WHERE catalogId = ?";
        
        List<Map<String, Object>> lm = C3P0Util.executeQuery(sql, catalogId);
        
        if (lm.size() > 0) {
            count = ((Number) lm.get(0).get("count")).longValue();
        }
        return count;
    }
    @Override
    public List<Tea> findRecommendTea(PageBean pb) {
        List<Tea> list = new ArrayList<>();
        String sql = "SELECT t.*, c.catalogName, i.imgName, i.imgSrc, i.imgType " +
                    "FROM s_tea t " +
                    "LEFT JOIN s_catalog c ON t.catalogId = c.catalogId " +
                    "LEFT JOIN s_uploadimg i ON t.imgId = i.imgId " +
                    "WHERE t.recommend = true " +
                    "ORDER BY t.addTime DESC LIMIT ?,?";
        
        List<Map<String, Object>> lm = C3P0Util.executeQuery(sql,
            (pb.getCurPage() - 1) * pb.getMaxSize(),
            pb.getMaxSize()
        );
        
        if (lm.size() > 0) {
            for (Map<String, Object> map : lm) {
                Tea tea = new Tea(map);
                list.add(tea);
            }
        }
        return list;
    }
    
    @Override
    public List<Tea> findTeaByCatalogId(int catalogId, PageBean pb) {
        List<Tea> list = new ArrayList<>();
        String sql = "SELECT t.*, c.catalogName, i.imgName, i.imgSrc, i.imgType " +
                    "FROM s_tea t " +
                    "LEFT JOIN s_catalog c ON t.catalogId = c.catalogId " +
                    "LEFT JOIN s_uploadimg i ON t.imgId = i.imgId " +
                    "WHERE t.catalogId = ? " +
                    "ORDER BY t.addTime DESC LIMIT ?,?";
        
        List<Map<String, Object>> lm = C3P0Util.executeQuery(sql,
            catalogId,
            (pb.getCurPage() - 1) * pb.getMaxSize(),
            pb.getMaxSize()
        );
        
        if (lm.size() > 0) {
            for (Map<String, Object> map : lm) {
                Tea tea = new Tea(map);
                list.add(tea);
            }
        }
        return list;
    }
}