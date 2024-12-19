package dao.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import model.User;
import model.PageBean;
import dao.UserDao;
import utils.C3P0Util;

/**
 * 用户数据访问实现类
 * 
 * Created time: 2024-12-18
 */
public class UserDaoImpl implements UserDao {

    /**
     * 用户登录验证
     */
    @Override
    public User userLogin(String userName, String userPassWord) {
        User user = null;
        String sql = "select * from s_user where userName=? and userPassWord=? and enabled='y'";
        
        List<Map<String, Object>> list = C3P0Util.executeQuery(sql, userName, userPassWord);
        
        if (list.size() > 0) {
            user = new User(list.get(0));
        }
        return user;
    }

    /**
     * 用户注册
     */
    @Override
    public boolean userRegister(User user) {
        String sql = "insert into s_user(userName,userPassWord,name,sex,age,tell,address,enabled) values(?,?,?,?,?,?,?,'y')";
        
        int i = C3P0Util.executeUpdate(sql,
            user.getUserName(),
            user.getUserPassWord(),
            user.getName(),
            user.getSex(),
            user.getAge(),
            user.getTell(),
            user.getAddress()
        );
        
        return i > 0;
    }

    /**
     * 检查用户名是否存在
     */
    @Override
    public boolean checkUserName(String userName) {
        String sql = "select count(*) as count from s_user where userName=?";
        
        List<Map<String, Object>> list = C3P0Util.executeQuery(sql, userName);
        
        if (list.size() > 0) {
            int count = ((Number) list.get(0).get("count")).intValue();
            return count > 0;
        }
        return false;
    }

    /**
     * 更新用户信息
     */
    @Override
    public boolean userUpdate(User user) {
        String sql = "update s_user set userPassWord=?,name=?,sex=?,age=?,tell=?,address=? where userId=?";
        
        int i = C3P0Util.executeUpdate(sql,
            user.getUserPassWord(),
            user.getName(),
            user.getSex(),
            user.getAge(),
            user.getTell(),
            user.getAddress(),
            user.getUserId()
        );
        
        return i > 0;
    }

    /**
     * 更新用户状态
     */
    @Override
    public boolean updateEnabled(int userId, String enabled) {
        String sql = "update s_user set enabled=? where userId=?";
        
        int i = C3P0Util.executeUpdate(sql, enabled, userId);
        return i > 0;
    }

    /**
     * 删除用户
     */
    @Override
    public boolean userDel(int userId) {
        String sql = "delete from s_user where userId=?";
        
        int i = C3P0Util.executeUpdate(sql, userId);
        return i > 0;
    }

    /**
     * 批量删除用户
     */
    @Override
    public boolean userBatchDel(String userIds) {
        String sql = "delete from s_user where userId in (" + userIds + ")";
        
        int i = C3P0Util.executeUpdate(sql);
        return i > 0;
    }

    /**
     * 分页获取用户列表
     */
    @Override
    public List<User> userList(PageBean pb) {
        List<User> userList = new ArrayList<>();
        String sql = "select * from s_user order by userId limit ?,?";
        
        List<Map<String, Object>> list = C3P0Util.executeQuery(sql,
            (pb.getCurPage() - 1) * pb.getMaxSize(),
            pb.getMaxSize()
        );
        
        if (list.size() > 0) {
            for (Map<String, Object> map : list) {
                User user = new User(map);
                userList.add(user);
            }
        }
        return userList;
    }

    /**
     * 统计用户总数
     */
    @Override
    public long userReadCount() {
        long count = 0;
        String sql = "select count(*) as count from s_user";
        
        List<Map<String, Object>> list = C3P0Util.executeQuery(sql);
        
        if (list.size() > 0) {
            count = ((Number) list.get(0).get("count")).longValue();
        }
        return count;
    }

    /**
     * 根据用户ID查找用户
     */
    @Override
    public User findUserById(int userId) {
        User user = null;
        String sql = "select * from s_user where userId=?";
        
        List<Map<String, Object>> list = C3P0Util.executeQuery(sql, userId);
        
        if (list.size() > 0) {
            user = new User(list.get(0));
        }
        return user;
    }

    /**
     * 搜索用户
     */
    @Override
    public List<User> searchUser(String keyword, PageBean pb) {
        List<User> userList = new ArrayList<>();
        String sql = "select * from s_user where userName like ? or name like ? or tell like ? " +
                    "order by userId limit ?,?";
        
        String pattern = "%" + keyword + "%";
        List<Map<String, Object>> list = C3P0Util.executeQuery(sql,
            pattern,
            pattern,
            pattern,
            (pb.getCurPage() - 1) * pb.getMaxSize(),
            pb.getMaxSize()
        );
        
        if (list.size() > 0) {
            for (Map<String, Object> map : list) {
                User user = new User(map);
                userList.add(user);
            }
        }
        return userList;
    }

    /**
     * 统计搜索结果总数
     */
    @Override
    public long searchUserCount(String keyword) {
        long count = 0;
        String sql = "select count(*) as count from s_user where userName like ? or name like ? or tell like ?";
        
        String pattern = "%" + keyword + "%";
        List<Map<String, Object>> list = C3P0Util.executeQuery(sql, pattern, pattern, pattern);
        
        if (list.size() > 0) {
            count = ((Number) list.get(0).get("count")).longValue();
        }
        return count;
    }
    
    /**
     * 修改密码
     */
    @Override
    public boolean updatePassword(int userId, String newPassword) {
        String sql = "update s_user set userPassWord=? where userId=?";
        
        int i = C3P0Util.executeUpdate(sql, newPassword, userId);
        return i > 0;
    }
}