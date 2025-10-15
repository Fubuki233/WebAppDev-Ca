package sg.com.aori.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import sg.com.aori.interfaces.IOrder;
import sg.com.aori.model.*;
import sg.com.aori.repository.InventoryRepository;
import sg.com.aori.repository.OrderRepository;

/**
 * @author Yibai
 * @date 2025-10-10
 * @version 1.0
 * @version 1.1 - Removed variant, modified it into product
 * @version 1.2 - Timeout limit 60s -> 10s
 */

@Service
@Transactional
public class OrderService implements IOrder {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private InventoryRepository inventoryRepository;

    @Autowired
    private FinanceService financeService;

    public Orders findOrderById(String orderId) {
        return orderRepository.findById(orderId).orElse(null);
    }

    public Orders findLatestOrderByCustomerId(String customerId) {
        return orderRepository.findTopByCustomerIdOrderByCreatedAtDesc(customerId);
    }

    @Override
    public List<Orders> findOrdersByCustomerId(String customerId) {
        return orderRepository.findByCustomerIdOrderByCreatedAtDesc(customerId);
    }

    public List<OrderItem> findOrderItemsByOrderId(String orderId) {
        return orderRepository.findOrderItemsByOrderId(orderId);
    }

    public boolean processPayment(String orderId) {
        Orders order = findOrderById(orderId);
        if (order == null) {
            throw new RuntimeException("Order not found");
        }

        if (!order.getOrderStatus().equals(Orders.OrderStatus.Pending)) {
            throw new RuntimeException("Order cannot be paid");
        }

        CompletableFuture<Boolean> paymentFuture = CompletableFuture.supplyAsync(() -> {
            try {
                // Timeout limit can be modified here
                for (int i = 0; i < 10; i++) {
                    Boolean paymentResult = financeService.verifyPayment(orderId);
                    System.out.println(i + "s, paymentResult=" + paymentResult);
                    if (paymentResult != null && paymentResult) {
                        return true;
                    }
                    TimeUnit.SECONDS.sleep(1);
                }
                return false;
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return false;
            }
        });

        try {
            Boolean paymentSuccess = paymentFuture.get(65, TimeUnit.SECONDS);

            if (paymentSuccess) {
                order.setOrderStatus(Orders.OrderStatus.Shipped);
                order.setPaymentStatus(Orders.PaymentStatus.Paid);
                order.setUpdatedAt(LocalDateTime.now());
                orderRepository.save(order);
                return true;
            } else {
                order.setPaymentStatus(Orders.PaymentStatus.Failed);
                order.setUpdatedAt(LocalDateTime.now());
                orderRepository.save(order);
                restoreInventory(orderId);
                return false;
            }

        } catch (Exception e) {
            order.setPaymentStatus(Orders.PaymentStatus.Failed);
            order.setUpdatedAt(LocalDateTime.now());
            orderRepository.save(order);
            restoreInventory(orderId);
            return false;
        }
    }

    @Override
    public void returnOrder(String orderId) {
        Orders order = findOrderById(orderId);
        if (order == null) {
            throw new RuntimeException("Order not found");
        }

        if (!order.getOrderStatus().equals(Orders.OrderStatus.Shipped)
                && !order.getOrderStatus().equals(Orders.OrderStatus.Delivered)) {
            throw new RuntimeException("Only shipped or delivered orders can be returned");
        }

        order.setOrderStatus(Orders.OrderStatus.Returned);
        order.setPaymentStatus(Orders.PaymentStatus.Refunded);
        order.setUpdatedAt(LocalDateTime.now());
        orderRepository.save(order);
    }

    public void cancelOrder(String orderId) {
        Orders order = findOrderById(orderId);
        if (order == null) {
            throw new RuntimeException("Order not found");
        }

        if (!order.getOrderStatus().equals(Orders.OrderStatus.Pending)
                && !order.getOrderStatus().equals(Orders.OrderStatus.Paid)) {
            throw new RuntimeException("Only pending or paid orders can be cancelled");
        }

        order.setOrderStatus(Orders.OrderStatus.Cancelled);
        if (order.getPaymentStatus().equals(Orders.PaymentStatus.Paid)) {
            order.setPaymentStatus(Orders.PaymentStatus.Refunded);
        } else {
            order.setPaymentStatus(Orders.PaymentStatus.Failed);
        }
        order.setUpdatedAt(LocalDateTime.now());
        orderRepository.save(order);

        restoreInventory(orderId);
    }

    @Override
    public void confirmDelivery(String orderId) {
        Orders order = findOrderById(orderId);
        if (order == null) {
            throw new RuntimeException("Order not found");
        }

        if (!order.getOrderStatus().equals(Orders.OrderStatus.Shipped)) {
            throw new RuntimeException("Only shipped orders can be confirmed for delivery");
        }

        order.setOrderStatus(Orders.OrderStatus.Delivered);
        order.setUpdatedAt(LocalDateTime.now());
        orderRepository.save(order);
    }

    private void restoreInventory(String orderId) {
        List<OrderItem> orderItems = findOrderItemsByOrderId(orderId);

        for (OrderItem item : orderItems) {
            Product product = item.getProduct();
            product.setStockQuantity(product.getStockQuantity() + item.getQuantity());
            inventoryRepository.save(product);
        }
    }

    public void updateOrderStatus(String orderId, Orders.OrderStatus status) {
        Orders order = findOrderById(orderId);
        if (order != null) {
            order.setOrderStatus(status);
            order.setUpdatedAt(LocalDateTime.now());
            orderRepository.save(order);
        }
    }

    public boolean validateOrderData(Orders order) {
        return order != null &&
                order.getTotalAmount() != null &&
                order.getTotalAmount().compareTo(BigDecimal.ZERO) > 0 &&
                order.getCustomer() != null;
    }
}