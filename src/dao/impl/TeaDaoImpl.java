/**
 * 饮品数据访问实现类
 * Created time: 2024-12-19 03:35:06
 * @author null7777777
 */
package dao.impl;

import dao.TeaDao;
import model.Tea;
import utils.C3P0Util;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TeaDaoImpl implements TeaDao {
    
    /**
     * 查找推荐饮品列表
     * @param limit 限制返回数量
     * @return 返回指定数量的饮品列表，按添加时间降序排序
     */
    @Override
    public List<Tea> findRecommendTea(int limit) {
        String sql = "SELECT * FROM view_tea ORDER BY addTime DESC LIMIT ?";
        List<Map<String, Object>> mapList = C3P0Util.executeQuery(sql, limit);
        List<Tea> list = new ArrayList<>();
        if (mapList != null) {
            for (Map<String, Object> map : mapList) {
                if (map != null) {
                    list.add(new Tea(map));
                }
            }
        }
        return list;
    }

    /**
     * 根据分类ID查找饮品列表
     * @param catalogId 分类ID
     * @return 该分类下的所有饮品列表
     */
    @Override
    public List<Tea> findTeaByCatalogId(int catalogId) {
        String sql = "SELECT * FROM view_tea WHERE catalogId = ?";
        List<Map<String, Object>> mapList = C3P0Util.executeQuery(sql, catalogId);
        List<Tea> list = new ArrayList<>();
        if (mapList != null) {
            for (Map<String, Object> map : mapList) {
                if (map != null) {
                    list.add(new Tea(map));
                }
            }
        }
        return list;
    }

    /**
     * 获取所有饮品列表
     * @return 所有饮品列表，按ID升序排序
     */
    @Override
    public List<Tea> teaList() {
        String sql = "SELECT * FROM view_tea ORDER BY teaId ASC";
        List<Map<String, Object>> mapList = C3P0Util.executeQuery(sql);
        List<Tea> list = new ArrayList<>();
        if (mapList != null) {
            for (Map<String, Object> map : mapList) {
                if (map != null) {
                    list.add(new Tea(map));
                }
            }
        }
        return list;
    }

    /**
     * 根据ID查找饮品
     * @param teaId 饮品ID
     * @return 找到返回饮品对象，否则返回null
     */
    @Override
    public Tea findTeaById(int teaId) {
        String sql = "SELECT * FROM view_tea WHERE teaId = ?";
        List<Map<String, Object>> mapList = C3P0Util.executeQuery(sql, teaId);
        if (mapList != null && !mapList.isEmpty() && mapList.get(0) != null) {
            return new Tea(mapList.get(0));
        }
        return null;
    }

    /**
     * 添加新饮品
     * @param tea 要添加的饮品对象
     * @return 添加成功返回true，失败返回false
     */
    @Override
    public boolean addTea(Tea tea) {
        String sql = "INSERT INTO s_tea (catalogId, teaName, price, description, imgId, addTime) VALUES (?, ?, ?, ?, ?, NOW())";
        return C3P0Util.executeUpdate(sql, 
            tea.getCatalogId(), 
            tea.getTeaName(), 
            tea.getPrice(), 
            tea.getDescription(), 
            tea.getImgId()) > 0;
    }

    /**
     * 更新饮品信息
     * @param tea 要更新的饮品对象
     * @return 更新成功返回true，失败返回false
     */
    @Override
    public boolean updateTea(Tea tea) {
        String sql = "UPDATE s_tea SET catalogId=?, teaName=?, price=?, description=?, imgId=? WHERE teaId=?";
        return C3P0Util.executeUpdate(sql, 
            tea.getCatalogId(), 
            tea.getTeaName(), 
            tea.getPrice(), 
            tea.getDescription(), 
            tea.getImgId(), 
            tea.getTeaId()) > 0;
    }

    /**
     * 删除饮品
     * @param teaId 要删除的饮品ID
     * @return 删除成功返回true，失败返回false
     */
    @Override
    public boolean deleteTea(int teaId) {
        String sql = "DELETE FROM s_tea WHERE teaId=?";
        return C3P0Util.executeUpdate(sql, teaId) > 0;
    }
}