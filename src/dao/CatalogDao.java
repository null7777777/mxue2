package dao;

import java.util.List;
import model.Catalog;
import model.PageBean;

/**
 * 商品分类数据访问接口
 * Created time: 2024-12-18
 * @author null7777777
 */
public interface CatalogDao {
    
    /**
     * 获取分页的分类列表
     * @param pb 分页Bean，包含当前页码和每页显示数量
     * @return 分类列表
     */
    List<Catalog> catalogList(PageBean pb);
    
    /**
     * 统计分类总数
     * @return 分类总数
     */
    long catalogReadCount();
    
    /**
     * 获取所有分类
     * @return 所有分类列表
     */
    List<Catalog> getCatalog();
    
    /**
     * 根据分类ID删除分类
     * @param catalogId 分类ID
     * @return 删除是否成功
     */
    boolean catalogDel(int catalogId);
    
    /**
     * 批量删除分类
     * @param ids 分类ID字符串，格式如："1,2,3"
     * @return 删除是否成功
     */
    boolean catalogBatDelById(String ids);
    
    /**
     * 根据分类名称查询分类
     * @param catalogName 分类名称
     * @return 是否存在该分类
     */
    boolean findCatalogByCatalogName(String catalogName);
    
    /**
     * 添加新分类
     * @param catalogName 分类名称
     * @return 添加是否成功
     */
    boolean catalogAdd(String catalogName);
    
    /**
     * 根据ID查找分类
     * @param catalogId 分类ID
     * @return 分类对象，如果不存在返回null
     */
    Catalog findCatalogById(int catalogId);
    
    /**
     * 更新分类信息
     * @param catalog 包含更新信息的分类对象
     * @return 更新是否成功
     */
    boolean catalogUpdate(Catalog catalog);
}