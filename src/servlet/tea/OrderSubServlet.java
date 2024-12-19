package servlet.tea;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import model.Cart;
import model.CartItem;
import model.Order;
import model.OrderItem;
import model.PageBean;
import model.User;
import model.Tea;
import dao.TeaDao;
import dao.OrderDao;
import dao.OrderItemDao;
import dao.impl.TeaDaoImpl;
import dao.impl.OrderDaoImpl;
import dao.impl.OrderItemDaoImpl;
import utils.DateUtil;
import utils.RanUtil;

/**
 * 订单前台处理Servlet
 * 
 * Created time: 2024-12-18
 * @author null7777777
 * Last modified: 2024-12-18 16:50:39 UTC
 */
@WebServlet(name = "OrderServlet", urlPatterns = { "/OrderServlet" })
public class OrderSubServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final int MAX_LIST_SIZE = 5;
    private static final String CART_PATH = "jsp/tea/cart.jsp";
    private static final String ORDER_PAY_PATH = "jsp/tea/ordersuccess.jsp";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        doPost(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        String action = request.getParameter("action");
        switch(action) {
            case "subOrder":
                submitOrder(request, response);
                break;
            case "list":
                listMyOrders(request, response);
                break;
            case "ship":
                shipOrder(request, response);
                break;
        }
    }

    /**
     * 订单发货/状态更新
     */
    private void shipOrder(HttpServletRequest request, HttpServletResponse response) 
            throws IOException, ServletException {
        int orderId = Integer.parseInt(request.getParameter("id"));
        OrderDao orderDao = new OrderDaoImpl();
        
        if(orderDao.updateOrderStatus(orderId, 3)) {
            request.setAttribute("orderMessage", "订单状态更新成功");
        } else {
            request.setAttribute("orderMessage", "订单状态更新失败");
        }
        listMyOrders(request, response);
    }

    /**
     * 查看我的订单列表
     */
    private void listMyOrders(HttpServletRequest request, HttpServletResponse response) 
            throws IOException, ServletException {
        User user = (User)request.getSession().getAttribute("landing");
        if(user == null) {
            response.sendRedirect("jsp/tea/reg.jsp?type=login");
            return;
        }

        // 创建DAO实例
        OrderDao orderDao = new OrderDaoImpl();
        OrderItemDao orderItemDao = new OrderItemDaoImpl();
        TeaDao teaDao = new TeaDaoImpl();

        try {
            // 处理分页
            int curPage = 1;
            String pageStr = request.getParameter("page");
            if (pageStr != null) {
                curPage = Integer.parseInt(pageStr);
            }
            
            // 获取该用户的所有订单
            List<Order> allOrders = orderDao.findOrderByUserId(user.getUserId());
            
            // 创建分页对象，传入当前页码、每页显示数量和总记录数
            PageBean pb = new PageBean(curPage, MAX_LIST_SIZE, (long)allOrders.size());
            
            // 计算当前页的数据范围
            int fromIndex = (curPage - 1) * MAX_LIST_SIZE;
            int toIndex = Math.min(fromIndex + MAX_LIST_SIZE, allOrders.size());
            
            // 获取当前页的订单
            List<Order> pageOrders;
            if (fromIndex < allOrders.size()) {
                pageOrders = allOrders.subList(fromIndex, toIndex);
                
                // 填充订单详情
                for(Order order : pageOrders) {
                    // 获取订单项
                    List<OrderItem> items = orderItemDao.findItemsByOrderId(order.getOrderId());
                    for(OrderItem item : items) {
                        Tea tea = teaDao.findTeaById(item.getTeaId());
                        item.setTea(tea);
                    }
                    order.setoItem(items);
                }
            } else {
                pageOrders = new ArrayList<>();
            }
            
            request.setAttribute("pageBean", pb);
            request.setAttribute("orderList", pageOrders);
            request.getRequestDispatcher("jsp/tea/myorderlist.jsp").forward(request, response);
            
        } catch (Exception e) {
            request.setAttribute("error", "获取订单列表失败：" + e.getMessage());
            request.getRequestDispatcher(CART_PATH).forward(request, response);
        }
    }

    /**
     * 提交订单
     */
    private void submitOrder(HttpServletRequest request, HttpServletResponse response) 
            throws IOException, ServletException {
        HttpSession session = request.getSession();
        Cart cart = (Cart) session.getAttribute("shopCart");
        User user = (User) session.getAttribute("landing");
        
        if (cart == null || user == null) {
            request.setAttribute("suberr", "提交订单失败：购物车为空或用户未登录");
            request.getRequestDispatcher(CART_PATH).forward(request, response);
            return;
        }

        // 创建订单对象
        Order order = new Order();
        order.setOrderNum(RanUtil.getOrderNum());
        order.setOrderDate(DateUtil.show());
        order.setMoney(cart.getTotPrice());
        order.setOrderStatus(1); // 未付款状态
        order.setUserId(user.getUserId());

        // 保存订单
        OrderDao orderDao = new OrderDaoImpl();
        if(orderDao.orderAdd(order)) {
            // 获取新生成的订单ID
            Order newOrder = orderDao.findOrderByNum(order.getOrderNum());
            if (newOrder != null) {
                // 创建订单项列表
                List<OrderItem> orderItems = new ArrayList<>();
                for(Map.Entry<Integer, CartItem> entry : cart.getMap().entrySet()) {
                    OrderItem item = new OrderItem();
                    item.setTeaId(entry.getKey());
                    item.setQuantity(entry.getValue().getQuantity());
                    item.setOrderId(newOrder.getOrderId());
                    orderItems.add(item);
                }
                
                // 批量添加订单项
                OrderItemDao orderItemDao = new OrderItemDaoImpl();
                if(orderItemDao.orderItemBatchAdd(orderItems)) {
                    // 清空购物车并跳转到支付页面
                    session.removeAttribute("shopCart");
                    request.setAttribute("orderNum", newOrder.getOrderNum());
                    request.setAttribute("money", newOrder.getMoney());
                    request.getRequestDispatcher(ORDER_PAY_PATH).forward(request, response);
                } else {
                    request.setAttribute("suberr", "订单项添加失败");
                    request.getRequestDispatcher(CART_PATH).forward(request, response);
                }
            } else {
                request.setAttribute("suberr", "订单提交失败：无法获取订单信息");
                request.getRequestDispatcher(CART_PATH).forward(request, response);
            }
        } else {
            request.setAttribute("suberr", "订单提交失败，请重新提交");
            request.getRequestDispatcher(CART_PATH).forward(request, response);
        }
    }
}