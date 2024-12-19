package dao;

import java.util.List;
import model.Admin;
import model.PageBean;

/**
 * 管理员数据访问接口
 * 提供管理员账户相关的数据库操作方法
 * 
 */
public interface AdminDao {
    
    /**
     * 管理员登录验证
     * 验证成功后会更新最后登录时间
     *
     * @param admin 包含登录信息的管理员对象（需要userName和passWord）
     * @return 登录是否成功
     */
    boolean userLogin(Admin admin);
    
    /**
     * 获取管理员列表（分页）
     *
     * @param pageBean 分页参数对象，包含当前页码和每页显示数量
     * @return 管理员列表
     */
    List<Admin> userList(PageBean pageBean);
    
    /**
     * 添加新管理员
     *
     * @param user 要添加的管理员信息对象
     * @return 添加是否成功
     */
    boolean userAdd(Admin user);
    
    /**
     * 根据ID查找管理员
     *
     * @param id 管理员ID
     * @return 管理员对象，如果不存在则返回null
     */
    Admin findUser(Integer id);
    
    /**
     * 根据用户名查找管理员
     * 用于检查用户名是否已存在
     *
     * @param userName 用户名
     * @return true表示用户名已存在，false表示不存在
     */
    boolean findUser(String userName);
    
    /**
     * 更新管理员信息
     *
     * @param admin 包含更新信息的管理员对象
     * @return 更新是否成功
     */
    boolean userUpdate(Admin admin);
    
    /**
     * 删除指定管理员
     *
     * @param id 要删除的管理员ID
     * @return 删除是否成功
     */
    boolean delUser(int id);
    
    /**
     * 批量删除管理员
     *
     * @param ids 要删除的管理员ID字符串，格式如："1,2,3"
     * @return 删除是否成功
     */
    boolean batDelUser(String ids);
    
    /**
     * 统计管理员总数
     *
     * @return 管理员总数
     */
    long userReadCount();
}