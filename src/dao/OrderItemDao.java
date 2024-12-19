package dao;

import java.util.List;
import model.OrderItem;

/**
 * 订单项数据访问接口
 * 定义了订单项相关的所有数据库操作方法
 * 
 */
public interface OrderItemDao {
    
    /**
     * 添加新订单项
     *
     * @param item 订单项对象，需包含teaId、orderId和quantity
     * @return 添加是否成功
     */
    boolean orderItemAdd(OrderItem item);
    
    /**
     * 批量添加订单项
     * 用于同时添加多个订单项，提高性能
     *
     * @param items 订单项列表
     * @return 添加是否成功
     */
    boolean orderItemBatchAdd(List<OrderItem> items);
    
    /**
     * 根据订单ID查找该订单的所有订单项
     *
     * @param orderId 订单ID
     * @return 订单项列表
     */
    List<OrderItem> findItemsByOrderId(int orderId);
    
    /**
     * 根据订单项ID查找订单项
     *
     * @param itemId 订单项ID
     * @return 订单项对象，如果不存在则返回null
     */
    OrderItem findItemById(int itemId);
    
    /**
     * 更新订单项数量
     *
     * @param itemId 订单项ID
     * @param quantity 新的数量
     * @return 更新是否成功
     */
    boolean updateQuantity(int itemId, int quantity);
    
    /**
     * 删除指定订单项
     *
     * @param itemId 订单项ID
     * @return 删除是否成功
     */
    boolean deleteItem(int itemId);
    
    /**
     * 删除指定订单的所有订单项
     *
     * @param orderId 订单ID
     * @return 删除是否成功
     */
    boolean deleteByOrderId(int orderId);
    
    /**
     * 获取指定商品的总销售数量
     *
     * @param teaId 商品ID
     * @return 商品的总销售数量
     */
    int getTeaTotalSales(int teaId);
    
    /**
     * 检查商品是否在任何订单项中使用
     *
     * @param teaId 商品ID
     * @return true表示商品已被使用，false表示未使用
     */
    boolean isTeaInUse(int teaId);
    
    /**
     * 获取指定订单中的商品种类数量
     *
     * @param orderId 订单ID
     * @return 订单中不同商品的数量
     */
    int getOrderTeaCount(int orderId);
}