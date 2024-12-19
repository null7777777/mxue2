package dao;

import java.util.List;
import model.User;
import model.PageBean;

/**
 * 用户数据访问接口
 * 定义了用户相关的所有数据库操作方法
 * 
 * Created time: 2024-12-18
 * @author null7777777
 * Last modified: 2024-12-18 09:01:11 UTC
 */
public interface UserDao {
    
    /**
     * 用户登录验证
     * 仅验证启用状态(enabled='y')的用户
     *
     * @param userName 用户名
     * @param userPassWord 密码
     * @return 验证成功返回用户对象，失败返回null
     */
    User userLogin(String userName, String userPassWord);
    
    /**
     * 用户注册
     * 默认状态为启用(enabled='y')
     *
     * @param user 用户对象，需包含用户名、密码、姓名、性别、年龄、电话和地址
     * @return 注册是否成功
     */
    boolean userRegister(User user);
    
    /**
     * 检查用户名是否已存在
     *
     * @param userName 待检查的用户名
     * @return true表示用户名已存在，false表示可用
     */
    boolean checkUserName(String userName);
    
    /**
     * 更新用户信息
     * 不包含用户名和状态的更新
     *
     * @param user 用户对象，需包含用户ID和待更新的信息
     * @return 更新是否成功
     */
    boolean userUpdate(User user);
    
    /**
     * 更新用户状态
     *
     * @param userId 用户ID
     * @param enabled 状态值('y'启用,'n'禁用)
     * @return 更新是否成功
     */
    boolean updateEnabled(int userId, String enabled);
    
    /**
     * 删除用户
     *
     * @param userId 用户ID
     * @return 删除是否成功
     */
    boolean userDel(int userId);
    
    /**
     * 批量删除用户
     *
     * @param userIds 用户ID字符串，格式如："1,2,3"
     * @return 删除是否成功
     */
    boolean userBatchDel(String userIds);
    
    /**
     * 分页获取用户列表
     * 按用户ID排序
     *
     * @param pb 分页参数对象
     * @return 用户列表
     */
    List<User> userList(PageBean pb);
    
    /**
     * 统计用户总数
     *
     * @return 用户总数
     */
    long userReadCount();
    
    /**
     * 根据用户ID查找用户
     *
     * @param userId 用户ID
     * @return 用户对象，不存在则返回null
     */
    User findUserById(int userId);
    
    /**
     * 搜索用户
     * 支持按用户名、姓名、电话号码模糊搜索，按用户ID排序
     *
     * @param keyword 搜索关键词
     * @param pb 分页参数对象
     * @return 符合条件的用户列表
     */
    List<User> searchUser(String keyword, PageBean pb);
    
    /**
     * 统计搜索结果总数
     *
     * @param keyword 搜索关键词
     * @return 符合搜索条件的用户总数
     */
    long searchUserCount(String keyword);
    
    /**
     * 修改用户密码
     *
     * @param userId 用户ID
     * @param newPassword 新密码
     * @return 修改是否成功
     */
    boolean updatePassword(int userId, String newPassword);
}