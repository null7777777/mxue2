package dao;

import java.util.List;
import model.UpLoadImg;
import model.PageBean;

/**
 * 图片上传数据访问接口
 * 定义了图片文件相关的所有数据库操作方法
 * 
 * Created time: 2024-12-18
 */
public interface UpLoadImgDao {
    
    /**
     * 添加新的图片记录
     *
     * @param img 图片对象，需包含文件名称、路径和类型
     * @return 添加是否成功
     */
    boolean imgAdd(UpLoadImg img);
    
    /**
     * 查找最新添加的图片记录
     *
     * @return 最新的图片对象，不存在则返回null
     */
    UpLoadImg findLastImg();
    
    /**
     * 更新图片信息
     *
     * @param img 图片对象，需包含完整的更新信息
     * @return 更新是否成功
     */
    boolean imgUpdate(UpLoadImg img);
    
    /**
     * 删除图片记录
     *
     * @param imgId 图片ID
     * @return 删除是否成功
     */
    boolean imgDel(int imgId);
    
    /**
     * 批量删除图片记录
     *
     * @param imgIds 图片ID字符串，格式如："1,2,3"
     * @return 删除是否成功
     */
    boolean imgBatchDel(String imgIds);
    
    /**
     * 分页获取图片列表
     * 按图片ID降序排序
     *
     * @param pb 分页参数对象
     * @return 图片列表
     */
    List<UpLoadImg> imgList(PageBean pb);
    
    /**
     * 统计图片总数
     *
     * @return 图片总数
     */
    long imgReadCount();
    
    /**
     * 根据图片ID查找图片
     *
     * @param imgId 图片ID
     * @return 图片对象，不存在则返回null
     */
    UpLoadImg findImgById(int imgId);
    
    /**
     * 根据文件类型查找图片
     * 按图片ID降序排序
     *
     * @param imgType 文件类型
     * @param pb 分页参数对象
     * @return 指定类型的图片列表
     */
    List<UpLoadImg> findImgByType(String imgType, PageBean pb);
    
    /**
     * 统计指定类型的图片数量
     *
     * @param imgType 文件类型
     * @return 该类型的图片数量
     */
    long imgCountByType(String imgType);
    
    /**
     * 检查图片是否被商品使用
     *
     * @param imgId 图片ID
     * @return true表示图片正在使用，false表示未使用
     */
    boolean isImgInUse(int imgId);
}