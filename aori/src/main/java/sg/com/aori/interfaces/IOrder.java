/**
 * @author Jiang
 * @date 2025-10-07
 * @version 1.0
 */

package sg.com.aori.interfaces;

import sg.com.aori.model.OrderItem;
import sg.com.aori.model.Orders;
import java.util.List;

public interface IOrder {
    Orders findOrderById(String orderId);

    Orders findLatestOrderByCustomerId(String customerId);

    List<Orders> findOrdersByCustomerId(String customerId);

    List<OrderItem> findOrderItemsByOrderId(String orderId);

    boolean processPayment(String orderId);

    void cancelOrder(String orderId);

    void updateOrderStatus(String orderId, Orders.OrderStatus status);

    boolean validateOrderData(Orders order);
}