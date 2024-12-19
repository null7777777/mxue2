package dao.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import model.OrderItem;
import dao.OrderItemDao;
import utils.C3P0Util;

/**
 * 订单项数据访问实现类
 * 
 * Created time: 2024-12-18
 */
public class OrderItemDaoImpl implements OrderItemDao {

    /**
     * 添加订单项
     */
    @Override
    public boolean orderItemAdd(OrderItem item) {
        String sql = "insert into s_orderitem(teaId, orderId, quantity) values(?,?,?)";
        
        int i = C3P0Util.executeUpdate(sql,
            item.getTeaId(),
            item.getOrderId(),
            item.getQuantity()
        );
        
        return i > 0;
    }

    /**
     * 批量添加订单项
     */
    @Override
    public boolean orderItemBatchAdd(List<OrderItem> items) {
        if (items == null || items.isEmpty()) {
            return false;
        }

        StringBuilder sql = new StringBuilder(
            "insert into s_orderitem(teaId, orderId, quantity) values");
        
        // 构建批量插入的SQL
        for (int i = 0; i < items.size(); i++) {
            sql.append("(?,?,?)");
            if (i < items.size() - 1) {
                sql.append(",");
            }
        }

        // 准备参数
        Object[] params = new Object[items.size() * 3];
        int index = 0;
        for (OrderItem item : items) {
            params[index++] = item.getTeaId();
            params[index++] = item.getOrderId();
            params[index++] = item.getQuantity();
        }

        int i = C3P0Util.executeUpdate(sql.toString(), params);
        return i == items.size();
    }

    /**
     * 根据订单ID查找订单项
     */
    @Override
    public List<OrderItem> findItemsByOrderId(int orderId) {
        List<OrderItem> items = new ArrayList<>();
        String sql = "select * from s_orderitem where orderId=?";
        
        List<Map<String, Object>> list = C3P0Util.executeQuery(sql, orderId);
        
        if (list.size() > 0) {
            for (Map<String, Object> map : list) {
                OrderItem item = new OrderItem(map);
                items.add(item);
            }
        }
        return items;
    }

    /**
     * 根据订单项ID查找订单项
     */
    @Override
    public OrderItem findItemById(int itemId) {
        OrderItem item = null;
        String sql = "select * from s_orderitem where itemId=?";
        
        List<Map<String, Object>> list = C3P0Util.executeQuery(sql, itemId);
        
        if (list.size() > 0) {
            item = new OrderItem(list.get(0));
        }
        return item;
    }

    /**
     * 更新订单项数量
     */
    @Override
    public boolean updateQuantity(int itemId, int quantity) {
        String sql = "update s_orderitem set quantity=? where itemId=?";
        
        int i = C3P0Util.executeUpdate(sql, quantity, itemId);
        return i > 0;
    }

    /**
     * 删除订单项
     */
    @Override
    public boolean deleteItem(int itemId) {
        String sql = "delete from s_orderitem where itemId=?";
        
        int i = C3P0Util.executeUpdate(sql, itemId);
        return i > 0;
    }

    /**
     * 删除订单的所有订单项
     */
    @Override
    public boolean deleteByOrderId(int orderId) {
        String sql = "delete from s_orderitem where orderId=?";
        
        int i = C3P0Util.executeUpdate(sql, orderId);
        return i > 0;
    }

    /**
     * 获取商品在所有订单中的总销售数量
     */
    @Override
    public int getTeaTotalSales(int teaId) {
        int total = 0;
        String sql = "select sum(quantity) as total from s_orderitem where teaId=?";
        
        List<Map<String, Object>> list = C3P0Util.executeQuery(sql, teaId);
        
        if (list.size() > 0 && list.get(0).get("total") != null) {
            total = ((Number) list.get(0).get("total")).intValue();
        }
        return total;
    }

    /**
     * 检查商品是否已经在订单中使用
     */
    @Override
    public boolean isTeaInUse(int teaId) {
        String sql = "select count(*) as count from s_orderitem where teaId=?";
        
        List<Map<String, Object>> list = C3P0Util.executeQuery(sql, teaId);
        
        if (list.size() > 0) {
            int count = ((Number) list.get(0).get("count")).intValue();
            return count > 0;
        }
        return false;
    }

    /**
     * 获取订单中的商品种类数量
     */
    @Override
    public int getOrderTeaCount(int orderId) {
        int count = 0;
        String sql = "select count(distinct teaId) as count from s_orderitem where orderId=?";
        
        List<Map<String, Object>> list = C3P0Util.executeQuery(sql, orderId);
        
        if (list.size() > 0) {
            count = ((Number) list.get(0).get("count")).intValue();
        }
        return count;
    }
}