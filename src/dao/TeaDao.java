package dao;

import java.util.List;
import model.Tea;
import model.PageBean;

/**
 * 商品数据访问接口
 * Created time: 2024-12-18
 * @author null7777777
 */
public interface TeaDao {
    
    /**
     * 分页获取商品列表
     * @param pb 分页对象
     * @return 商品列表
     */
    List<Tea> teaList(PageBean pb);
    
    /**
     * 按关键字搜索商品(分页)
     * @param keyword 搜索关键字
     * @param pb 分页对象
     * @return 商品列表
     */
    List<Tea> searchTea(String keyword, PageBean pb);
    
    /**
     * 统计商品总数
     * @return 商品总数
     */
    long teaReadCount();
    
    /**
     * 按关键字统计商品数量
     * @param keyword 搜索关键字
     * @return 匹配的商品数量
     */
    long teaSearchCount(String keyword);
    
    /**
     * 添加商品
     * @param tea 商品对象
     * @return 是否添加成功
     */
    boolean teaAdd(Tea tea);
    
    /**
     * 更新商品信息
     * @param tea 商品对象
     * @return 是否更新成功
     */
    boolean teaUpdate(Tea tea);
    
    /**
     * 删除商品
     * @param id 商品ID
     * @return 是否删除成功
     */
    boolean teaDel(int id);
    
    /**
     * 按ID查找商品
     * @param id 商品ID
     * @return 商品对象
     */
    Tea findTeaById(int id);
    
    /**
     * 按名称查找商品
     * @param teaName 商品名称
     * @return 是否存在
     */
    boolean findTeaByName(String teaName);
    
    /**
     * 通过多个商品id查询对应的图片id
     * @param ids 商品ID字符串(逗号分隔)
     * @return 图片ID字符串(逗号分隔)
     */
    String findimgIdByIds(String ids);
    
    /**
     * 统计指定分类下的商品数量
     * @param catalogId 分类ID
     * @return 该分类下的商品数量
     */
    long teaCountByCatalogId(int catalogId);
    
    /**
     * 获取推荐商品列表(分页)
     * @param pb 分页对象
     * @return 推荐商品列表
     */
    List<Tea> findRecommendTea(PageBean pb);
    
    /**
     * 按分类ID获取商品列表(分页)
     * @param catalogId 分类ID
     * @param pb 分页对象
     * @return 商品列表
     */
    List<Tea> findTeaByCatalogId(int catalogId, PageBean pb);
}