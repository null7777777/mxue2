package dao.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import model.UpLoadImg;
import model.PageBean;
import dao.UpLoadImgDao;
import utils.C3P0Util;

/**
 * 图片上传数据访问实现类
 * 
 * Created time: 2024-12-18
 */
public class UpLoadImgDaoImpl implements UpLoadImgDao {
    
    /**
     * 添加图片记录
     */
    @Override
    public boolean imgAdd(UpLoadImg img) {
        String sql = "insert into s_uploadimg(imgName,imgSrc,imgType) values(?,?,?)";
        
        int i = C3P0Util.executeUpdate(sql,
            img.getImgName(),
            img.getImgSrc(),
            img.getImgType()
        );
        
        return i > 0;
    }
    
    /**
     * 获取最新添加的图片
     */
    @Override
    public UpLoadImg findLastImg() {
        UpLoadImg img = null;
        String sql = "select * from s_uploadimg order by imgId desc limit 1";
        
        List<Map<String, Object>> list = C3P0Util.executeQuery(sql);
        
        if (list.size() > 0) {
            img = new UpLoadImg(list.get(0));
        }
        return img;
    }
    
    /**
     * 更新图片信息
     */
    @Override
    public boolean imgUpdate(UpLoadImg img) {
        String sql = "update s_uploadimg set imgName=?,imgSrc=?,imgType=? where imgId=?";
        
        int i = C3P0Util.executeUpdate(sql,
            img.getImgName(),
            img.getImgSrc(),
            img.getImgType(),
            img.getImgId()
        );
        
        return i > 0;
    }
    
    /**
     * 删除图片记录
     */
    @Override
    public boolean imgDel(int imgId) {
        String sql = "delete from s_uploadimg where imgId=?";
        
        int i = C3P0Util.executeUpdate(sql, imgId);
        return i > 0;
    }
    
    /**
     * 批量删除图片记录
     */
    @Override
    public boolean imgBatchDel(String imgIds) {
        String sql = "delete from s_uploadimg where imgId in (" + imgIds + ")";
        
        int i = C3P0Util.executeUpdate(sql);
        return i > 0;
    }
    
    /**
     * 分页获取图片列表
     */
    @Override
    public List<UpLoadImg> imgList(PageBean pb) {
        List<UpLoadImg> imgList = new ArrayList<>();
        String sql = "select * from s_uploadimg order by imgId desc limit ?,?";
        
        List<Map<String, Object>> list = C3P0Util.executeQuery(sql,
            (pb.getCurPage() - 1) * pb.getMaxSize(),
            pb.getMaxSize()
        );
        
        if (list.size() > 0) {
            for (Map<String, Object> map : list) {
                UpLoadImg img = new UpLoadImg(map);
                imgList.add(img);
            }
        }
        return imgList;
    }
    
    /**
     * 统计图片总数
     */
    @Override
    public long imgReadCount() {
        long count = 0;
        String sql = "select count(*) as count from s_uploadimg";
        
        List<Map<String, Object>> list = C3P0Util.executeQuery(sql);
        
        if (list.size() > 0) {
            count = ((Number) list.get(0).get("count")).longValue();
        }
        return count;
    }
    
    /**
     * 根据图片ID查找图片
     */
    @Override
    public UpLoadImg findImgById(int imgId) {
        UpLoadImg img = null;
        String sql = "select * from s_uploadimg where imgId=?";
        
        List<Map<String, Object>> list = C3P0Util.executeQuery(sql, imgId);
        
        if (list.size() > 0) {
            img = new UpLoadImg(list.get(0));
        }
        return img;
    }
    
    /**
     * 根据文件类型查找图片
     */
    @Override
    public List<UpLoadImg> findImgByType(String imgType, PageBean pb) {
        List<UpLoadImg> imgList = new ArrayList<>();
        String sql = "select * from s_uploadimg where imgType=? order by imgId desc limit ?,?";
        
        List<Map<String, Object>> list = C3P0Util.executeQuery(sql,
            imgType,
            (pb.getCurPage() - 1) * pb.getMaxSize(),
            pb.getMaxSize()
        );
        
        if (list.size() > 0) {
            for (Map<String, Object> map : list) {
                UpLoadImg img = new UpLoadImg(map);
                imgList.add(img);
            }
        }
        return imgList;
    }
    
    /**
     * 统计指定类型的图片数量
     */
    @Override
    public long imgCountByType(String imgType) {
        long count = 0;
        String sql = "select count(*) as count from s_uploadimg where imgType=?";
        
        List<Map<String, Object>> list = C3P0Util.executeQuery(sql, imgType);
        
        if (list.size() > 0) {
            count = ((Number) list.get(0).get("count")).longValue();
        }
        return count;
    }
    
    /**
     * 检查图片是否被使用
     */
    @Override
    public boolean isImgInUse(int imgId) {
        String sql = "select count(*) as count from s_tea where imgId=?";
        
        List<Map<String, Object>> list = C3P0Util.executeQuery(sql, imgId);
        
        if (list.size() > 0) {
            int count = ((Number) list.get(0).get("count")).intValue();
            return count > 0;
        }
        return false;
    }
}