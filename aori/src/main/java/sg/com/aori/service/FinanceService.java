package sg.com.aori.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import sg.com.aori.interfaces.IFinance;
import sg.com.aori.model.Orders;
import sg.com.aori.repository.OrderRepository;

/**
 * @author Yibai
 * @date 2025-10-10
 * @version 1.0
 * @version 1.1 - Added check before processing payment
 */

@Service
public class FinanceService implements IFinance {

    @Autowired
    private OrderRepository orderRepository;

    /**
     * Verfy if the payment is success
     * 
     * @param orderId
     * @return true / false (only if order is not applicable to pay)
     */
    @Override
    public Boolean verifyPayment(String orderId) {
        if (!isOrderValidForPayment(orderId)) {
            System.out.println("Order validation failed for order: " + orderId);
            return false;
        }
        return true;
    }

    /**
     * Check if the order can be paid
     * Condition:
     * 1. Order exists;
     * 2. order_status = Pending;
     * 3. payment_status = Pending.
     * 
     * @param orderId
     * @return true / false
     */
    private boolean isOrderValidForPayment(String orderId) {
        try {
            Orders order = orderRepository.findById(orderId).orElse(null);

            if (order == null) {
                System.out.println("Order not found: " + orderId);
                return false;
            }

            if (!Orders.OrderStatus.Pending.equals(order.getOrderStatus())) {
                System.out.println("Invalid order status for payment. Order: " + orderId +
                        ", Status: " + order.getOrderStatus());
                return false;
            }

            if (!Orders.PaymentStatus.Pending.equals(order.getPaymentStatus())) {
                System.out.println("Invalid payment status for payment. Order: " + orderId +
                        ", Payment Status: " + order.getPaymentStatus());
                return false;
            }

            System.out.println("Order validation passed for payment: " + orderId);
            return true;

        } catch (Exception e) {
            System.out.println("Error validating order for payment: " + orderId + ", Error: " + e.getMessage());
            return false;
        }
    }
}