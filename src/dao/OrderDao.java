package dao;

import java.util.List;

import model.Order;
import model.PageBean;

/**
 * 订单数据访问接口
 * 定义所有订单相关的数据库操作方法
 * 
 */
public interface OrderDao {
    
    /**
     * 添加新订单
     * 创建时会自动设置当前时间为订单日期
     *
     * @param order 订单对象，需包含userId、orderNum、money和orderStatus
     * @return 添加是否成功
     */
    boolean orderAdd(Order order);
    
    /**
     * 查找最新创建的订单
     * 按orderId降序排序获取第一条记录
     *
     * @return 最新的订单对象，不存在则返回null
     */
    Order findLastOrder();
    
    /**
     * 分页获取订单列表
     * 按订单日期（orderDate）降序排序
     *
     * @param pb 分页参数对象，包含当前页码和每页显示数量
     * @return 订单列表
     */
    List<Order> orderList(PageBean pb);
    
    /**
     * 统计订单总数
     *
     * @return 订单总数
     */
    long orderReadCount();
    
    /**
     * 获取指定用户的所有订单
     * 按订单日期（orderDate）降序排序
     *
     * @param userId 用户ID
     * @return 该用户的订单列表
     */
    List<Order> findOrderByUserId(int userId);
    
    /**
     * 根据订单ID查找订单
     *
     * @param orderId 订单ID
     * @return 订单对象，不存在则返回null
     */
    Order findOrderById(int orderId);
    
    /**
     * 更新订单状态
     *
     * @param orderId 订单ID
     * @param orderStatus 新的订单状态
     * @return 更新是否成功
     */
    boolean updateOrderStatus(int orderId, int orderStatus);
    
    /**
     * 删除指定订单
     *
     * @param orderId 订单ID
     * @return 删除是否成功
     */
    boolean orderDel(int orderId);
    
    /**
     * 批量删除订单
     *
     * @param orderIds 订单ID字符串，格式如："1,2,3"
     * @return 删除是否成功
     */
    boolean orderBatDel(String orderIds);
    
    /**
     * 统计指定状态的订单数量
     *
     * @param orderStatus 订单状态
     * @return 该状态的订单数量
     */
    long orderCountByStatus(int orderStatus);
    
    /**
     * 获取指定状态的订单列表（分页）
     * 按订单日期（orderDate）降序排序
     *
     * @param pb 分页参数对象
     * @param orderStatus 订单状态
     * @return 指定状态的订单列表
     */
    List<Order> orderListByStatus(PageBean pb, int orderStatus);
    
    /**
     * 根据订单号查找订单
     *
     * @param orderNum 订单号
     * @return 订单对象，不存在则返回null
     */
    Order findOrderByNum(String orderNum);
    
    /**
     * 更新订单金额
     *
     * @param orderId 订单ID
     * @param money 新的订单金额
     * @return 更新是否成功
     */
    boolean updateMoney(int orderId, double money);
    
    /**
     * 获取用户的订单总金额
     *
     * @param userId 用户ID
     * @return 用户的所有订单总金额
     */
    double getUserTotalMoney(int userId);
}