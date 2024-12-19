package dao;

import model.Tea;
import java.util.List;

public interface TeaDao {
    // 查找推荐饮品
    List<Tea> findRecommendTea(int limit);
    
    // 根据分类ID查找饮品
    List<Tea> findTeaByCatalogId(int catalogId);
    
    // 获取所有饮品列表
    List<Tea> teaList();
    
    // 根据ID查找饮品
    Tea findTeaById(int teaId);
    
    // 添加饮品
    boolean addTea(Tea tea);
    
    // 更新饮品
    boolean updateTea(Tea tea);
    
    // 删除饮品
    boolean deleteTea(int teaId);
}