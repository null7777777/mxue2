package servlet.admin;

import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import model.Order;
import model.OrderItem;
import model.PageBean;
import dao.TeaDao;
import dao.OrderDao;
import dao.OrderItemDao;
import dao.UserDao;
import dao.impl.TeaDaoImpl;
import dao.impl.OrderDaoImpl;
import dao.impl.OrderItemDaoImpl;
import dao.impl.UserDaoImpl;

/**
 * 订单管理Servlet
 */
@WebServlet("/jsp/admin/OrderManageServlet")
public class OrderManageServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final String ORDERLIST_PATH = "orderManage/orderlist.jsp";
    private static final String ORDERDETAIL_PATH = "orderManage/orderDetail.jsp";
    private static final String ORDEROP_PATH = "orderManage/orderOp.jsp";

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        switch(action) {
            case "list":
                orderList(request, response);
                break;
            case "detail":
                orderDetail(request, response);
                break;
            case "processing":
                orderProcessing(request, response);
                break;
            case "ship":
                orderShip(request, response);
                break;
            case "search":
                searchOrder(request, response);
                break;
            case "search1":
                searchOrder1(request, response);
                break;
            case "delete":
                deleteOrder(request, response);
                break;
            default:
                orderList(request, response);
        }
    }

    // 删除订单
    private void deleteOrder(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String orderIdStr = request.getParameter("id");
        if(orderIdStr != null && !orderIdStr.isEmpty()) {
            int orderId = Integer.parseInt(orderIdStr);
            OrderDao orderDao = new OrderDaoImpl();
            OrderItemDao orderItemDao = new OrderItemDaoImpl();
            
            // 先删除订单项,再删除订单
            orderItemDao.deleteByOrderId(orderId);
            orderDao.orderDel(orderId);
        }
        orderList(request, response);
    }

    // 根据订单号搜索订单
    private void searchOrder(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int curPage = 1;
        String page = request.getParameter("page");
        if (page != null) {
            curPage = Integer.parseInt(page);
        }
        int maxSize = Integer.parseInt(request.getServletContext().getInitParameter("maxPageSize"));
        String orderNum = request.getParameter("orderNum");
        OrderDao orderDao = new OrderDaoImpl();
        
        if(orderNum != null && !orderNum.isEmpty()) {
            Order order = orderDao.findOrderByNum(orderNum);
            if(order != null) {
                request.setAttribute("orderList", java.util.Arrays.asList(order));
                request.setAttribute("pageBean", new PageBean(1, maxSize, 1));
            } else {
                request.setAttribute("orderList", new java.util.ArrayList<>());
                request.setAttribute("pageBean", new PageBean(1, maxSize, 0));
            }
        } else {
            PageBean pb = new PageBean(curPage, maxSize, orderDao.orderReadCount());
            request.setAttribute("orderList", orderDao.orderList(pb));
            request.setAttribute("pageBean", pb);
        }
        
        request.getRequestDispatcher(ORDERLIST_PATH).forward(request, response);
    }

    // 根据订单状态搜索订单
    private void searchOrder1(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int curPage = 1;
        String page = request.getParameter("page");
        if (page != null) {
            curPage = Integer.parseInt(page);
        }
        int maxSize = Integer.parseInt(request.getServletContext().getInitParameter("maxPageSize"));
        OrderDao orderDao = new OrderDaoImpl();
        
        PageBean pb = new PageBean(curPage, maxSize, orderDao.orderCountByStatus(1));
        request.setAttribute("orderList", orderDao.orderListByStatus(pb, 1));
        request.setAttribute("pageBean", pb);
        
        request.getRequestDispatcher(ORDEROP_PATH).forward(request, response);
    }

    // 发货操作
    private void orderShip(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String orderIdStr = request.getParameter("id");
        if(orderIdStr != null && !orderIdStr.isEmpty()) {
            int orderId = Integer.parseInt(orderIdStr);
            OrderDao orderDao = new OrderDaoImpl();
            
            if(orderDao.updateOrderStatus(orderId, 2)) {
                request.setAttribute("orderMessage", "订单发货成功");
            } else {
                request.setAttribute("orderMessage", "订单发货失败");
            }
        }
        orderProcessing(request, response);
    }

    // 获取待处理订单列表
    private void orderProcessing(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int curPage = 1;
        String page = request.getParameter("page");
        if (page != null) {
            curPage = Integer.parseInt(page);
        }
        int maxSize = Integer.parseInt(request.getServletContext().getInitParameter("maxPageSize"));
        OrderDao orderDao = new OrderDaoImpl();
        
        PageBean pb = new PageBean(curPage, maxSize, orderDao.orderCountByStatus(1));
        request.setAttribute("orderList", orderDao.orderListByStatus(pb, 1));
        request.setAttribute("pageBean", pb);
        
        request.getRequestDispatcher(ORDEROP_PATH).forward(request, response);
    }

    // 查看订单详情
    private void orderDetail(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String orderIdStr = request.getParameter("id");
        if(orderIdStr != null && !orderIdStr.isEmpty()) {
            int orderId = Integer.parseInt(orderIdStr);
            OrderDao orderDao = new OrderDaoImpl();
            OrderItemDao orderItemDao = new OrderItemDaoImpl();
            UserDao userDao = new UserDaoImpl();
            TeaDao teaDao = new TeaDaoImpl();
            
            Order order = orderDao.findOrderById(orderId);
            if(order != null) {
                order.setUser(userDao.findUserById(order.getUserId()));
                List<OrderItem> items = orderItemDao.findItemsByOrderId(orderId);
                for(OrderItem item : items) {
                    item.setTea(teaDao.findTeaById(item.getTeaId()));
                }
                order.setoItem(items);
                request.setAttribute("order", order);
            }
        }
        request.getRequestDispatcher(ORDERDETAIL_PATH).forward(request, response);
    }

    // 获取订单列表
    private void orderList(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int curPage = 1;
        String page = request.getParameter("page");
        if (page != null) {
            curPage = Integer.parseInt(page);
        }
        int maxSize = Integer.parseInt(request.getServletContext().getInitParameter("maxPageSize"));
        OrderDao orderDao = new OrderDaoImpl();
        
        PageBean pb = new PageBean(curPage, maxSize, orderDao.orderReadCount());
        request.setAttribute("orderList", orderDao.orderList(pb));
        request.setAttribute("pageBean", pb);
        
        request.getRequestDispatcher(ORDERLIST_PATH).forward(request, response);
    }
}