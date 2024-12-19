package dao.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import model.Order;
import model.PageBean;
import dao.OrderDao;
import utils.C3P0Util;
import utils.DateUtil;

/**
 * 订单数据访问实现类
 * 与Order.java字段保持一致
 * 
 */
public class OrderDaoImpl implements OrderDao {

    // 添加订单
    @Override
    public boolean orderAdd(Order order) {
        String sql = "insert into s_order(userId, orderNum, orderDate, money, orderStatus) values(?,?,?,?,?)";
        
        int i = C3P0Util.executeUpdate(sql,
            order.getUserId(),
            order.getOrderNum(),
            DateUtil.getTimestamp(),  // 使用当前时间作为订单日期
            order.getMoney(),
            order.getOrderStatus()
        );
        
        return i > 0;
    }

    // 查找最新生成的订单
    @Override
    public Order findLastOrder() {
        Order order = null;
        String sql = "select * from s_order order by orderId desc limit 1";
        
        List<Map<String, Object>> list = C3P0Util.executeQuery(sql);
        
        if (list.size() > 0) {
            order = new Order(list.get(0));
        }
        return order;
    }

    // 分页获取订单列表
    @Override
    public List<Order> orderList(PageBean pb) {
        List<Order> orderList = new ArrayList<>();
        String sql = "select * from s_order order by orderDate desc limit ?,?";
        
        List<Map<String, Object>> list = C3P0Util.executeQuery(sql,
            (pb.getCurPage() - 1) * pb.getMaxSize(),
            pb.getMaxSize()
        );
        
        if (list.size() > 0) {
            for (Map<String, Object> map : list) {
                Order order = new Order(map);
                orderList.add(order);
            }
        }
        return orderList;
    }

    // 统计订单总数
    @Override
    public long orderReadCount() {
        long count = 0;
        String sql = "select count(*) as count from s_order";
        
        List<Map<String, Object>> list = C3P0Util.executeQuery(sql);
        
        if (list.size() > 0) {
            count = ((Number) list.get(0).get("count")).longValue();
        }
        return count;
    }

    // 根据用户ID获取订单列表
    @Override
    public List<Order> findOrderByUserId(int userId) {
        List<Order> orderList = new ArrayList<>();
        String sql = "select * from s_order where userId=? order by orderDate desc";
        
        List<Map<String, Object>> list = C3P0Util.executeQuery(sql, userId);
        
        if (list.size() > 0) {
            for (Map<String, Object> map : list) {
                Order order = new Order(map);
                orderList.add(order);
            }
        }
        return orderList;
    }

    // 根据订单ID查找订单
    @Override
    public Order findOrderById(int orderId) {
        Order order = null;
        String sql = "select * from s_order where orderId=?";
        
        List<Map<String, Object>> list = C3P0Util.executeQuery(sql, orderId);
        
        if (list.size() > 0) {
            order = new Order(list.get(0));
        }
        return order;
    }

    // 更新订单状态
    @Override
    public boolean updateOrderStatus(int orderId, int orderStatus) {
        String sql = "update s_order set orderStatus=? where orderId=?";
        
        int i = C3P0Util.executeUpdate(sql, orderStatus, orderId);
        return i > 0;
    }

    // 删除订单
    @Override
    public boolean orderDel(int orderId) {
        String sql = "delete from s_order where orderId=?";
        
        int i = C3P0Util.executeUpdate(sql, orderId);
        return i > 0;
    }

    // 批量删除订单
    @Override
    public boolean orderBatDel(String orderIds) {
        String sql = "delete from s_order where orderId in (" + orderIds + ")";
        
        int i = C3P0Util.executeUpdate(sql);
        return i > 0;
    }

    // 按状态统计订单数量
    @Override
    public long orderCountByStatus(int orderStatus) {
        long count = 0;
        String sql = "select count(*) as count from s_order where orderStatus=?";
        
        List<Map<String, Object>> list = C3P0Util.executeQuery(sql, orderStatus);
        
        if (list.size() > 0) {
            count = ((Number) list.get(0).get("count")).longValue();
        }
        return count;
    }

    // 获取指定状态的订单列表（分页）
    @Override
    public List<Order> orderListByStatus(PageBean pb, int orderStatus) {
        List<Order> orderList = new ArrayList<>();
        String sql = "select * from s_order where orderStatus=? order by orderDate desc limit ?,?";
        
        List<Map<String, Object>> list = C3P0Util.executeQuery(sql,
            orderStatus,
            (pb.getCurPage() - 1) * pb.getMaxSize(),
            pb.getMaxSize()
        );
        
        if (list.size() > 0) {
            for (Map<String, Object> map : list) {
                Order order = new Order(map);
                orderList.add(order);
            }
        }
        return orderList;
    }

    // 根据订单号查找订单
    @Override
    public Order findOrderByNum(String orderNum) {
        Order order = null;
        String sql = "select * from s_order where orderNum=?";
        
        List<Map<String, Object>> list = C3P0Util.executeQuery(sql, orderNum);
        
        if (list.size() > 0) {
            order = new Order(list.get(0));
        }
        return order;
    }

    // 更新订单金额
    @Override
    public boolean updateMoney(int orderId, double money) {
        String sql = "update s_order set money=? where orderId=?";
        
        int i = C3P0Util.executeUpdate(sql, money, orderId);
        return i > 0;
    }

    // 获取用户的订单总金额
    @Override
    public double getUserTotalMoney(int userId) {
        double total = 0.0;
        String sql = "select sum(money) as total from s_order where userId=?";
        
        List<Map<String, Object>> list = C3P0Util.executeQuery(sql, userId);
        
        if (list.size() > 0 && list.get(0).get("total") != null) {
            total = ((Number) list.get(0).get("total")).doubleValue();
        }
        return total;
    }
}