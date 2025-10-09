/**
 * v1.1: Provide check before processing payment / refund
 * @author Jiang
 * @date 2025-10-10
 * @version 1.1
 */

// ***** AI generated a method with random result of validation
// ***** But maybe we just need a very simple one?
// ***** Most statements are annotated for demo purpose

package sg.com.aori.service;

import sg.com.aori.interfaces.IFinance;
import sg.com.aori.model.Orders;
import sg.com.aori.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

// import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
// import java.util.concurrent.TimeUnit;

@Service
public class FinanceService implements IFinance {

    // private final Random random = new Random();
    private final ConcurrentHashMap<String, Boolean> paymentResults = new ConcurrentHashMap<>();

    @Autowired
    private OrderRepository orderRepository;

    @Override
    public Boolean verifyPayment(String orderId) {
        // Check order first
        if (!isOrderValidForPayment(orderId)) {
            System.out.println("Order validation failed for order: " + orderId);
            return false;
        }
        return true;
        
        // // Simulate payment
        // try {
        //     // Simulate network delay
        //     TimeUnit.MILLISECONDS.sleep(random.nextInt(2000));
            
        //     // 80% success rate for demo
        //     boolean success = random.nextDouble() < 0.8;
            
        //     // Store result
        //     paymentResults.put(orderId, success);
            
        //     System.out.println("Payment verification result for order " + orderId + ": " + success);
        //     return true;
            
        // } catch (InterruptedException e) {
        //     Thread.currentThread().interrupt();
        //     System.out.println("Payment verification interrupted for order: " + orderId);
        //     return false;
        // }
    }

    /**
     * Check if the order can be paid
     * Condition:
     * 1. Order exists
     * 2. order_status = Pending
     * 3. payment_status = Pending
     */
    private boolean isOrderValidForPayment(String orderId) {
        try {
            // Get order info from DB
            Orders order = orderRepository.findById(orderId).orElse(null);
            
            if (order == null) {
                System.out.println("Order not found: " + orderId);
                return false;
            }
            
            // Check order status
            if (!Orders.OrderStatus.Pending.equals(order.getOrderStatus())) {
                System.out.println("Invalid order status for payment. Order: " + orderId + 
                                 ", Status: " + order.getOrderStatus());
                return false;
            }
            
            // Check payment status
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

    // @Override
    // public boolean processRefund(String orderId, Double amount) {
    //     // Check order first
    //     if (!isOrderValidForRefund(orderId)) {
    //         System.out.println("Order not valid for refund: " + orderId);
    //         return false;
    //     }
        
    //     try {
    //         // Simulate refund
    //         TimeUnit.MILLISECONDS.sleep(1000);
            
    //         // 95% refund rate
    //         boolean success = random.nextDouble() < 0.95;
    //         System.out.println("Refund processing result for order " + orderId + ": " + success);
    //         return success;
            
    //     } catch (InterruptedException e) {
    //         Thread.currentThread().interrupt();
    //         return false;
    //     }
    // }

    // /**
    //  * Check if the order can get refund
    //  */
    // private boolean isOrderValidForRefund(String orderId) {
    //     try {
    //         Orders order = orderRepository.findById(orderId).orElse(null);
            
    //         if (order == null) {
    //             return false;
    //         }
            
    //         // Only paid order can get refund
    //         return Orders.PaymentStatus.Paid.equals(order.getPaymentStatus());
            
    //     } catch (Exception e) {
    //         System.out.println("Error validating order for refund: " + orderId);
    //         return false;
    //     }
    // }

    @Override
    public Boolean getPaymentStatus(String orderId) {
        return paymentResults.get(orderId);
    }

    @Override
    public Boolean isPaymentGatewayAvailable() {
        return true;
    }

    public PaymentStatusDetail getPaymentStatusDetail(String orderId) {
        PaymentStatusDetail detail = new PaymentStatusDetail();
        detail.setOrderId(orderId);
        
        try {
            Orders order = orderRepository.findById(orderId).orElse(null);
            if (order != null) {
                detail.setOrderStatus(order.getOrderStatus());
                detail.setPaymentStatus(order.getPaymentStatus());
                detail.setOrderExists(true);
            } else {
                detail.setOrderExists(false);
            }
            
            Boolean paymentResult = paymentResults.get(orderId);
            detail.setPaymentVerified(paymentResult);
            detail.setGatewayAvailable(isPaymentGatewayAvailable());
            
        } catch (Exception e) {
            detail.setError(e.getMessage());
        }
        
        return detail;
    }

    public static class PaymentStatusDetail {
        private String orderId;
        private Orders.OrderStatus orderStatus;
        private Orders.PaymentStatus paymentStatus;
        private Boolean paymentVerified;
        private Boolean orderExists;
        private Boolean gatewayAvailable;
        private String error;

        // Getters and Setters
        public String getOrderId() { return orderId; }
        public void setOrderId(String orderId) { this.orderId = orderId; }
        
        public Orders.OrderStatus getOrderStatus() { return orderStatus; }
        public void setOrderStatus(Orders.OrderStatus orderStatus) { this.orderStatus = orderStatus; }
        
        public Orders.PaymentStatus getPaymentStatus() { return paymentStatus; }
        public void setPaymentStatus(Orders.PaymentStatus paymentStatus) { this.paymentStatus = paymentStatus; }
        
        public Boolean getPaymentVerified() { return paymentVerified; }
        public void setPaymentVerified(Boolean paymentVerified) { this.paymentVerified = paymentVerified; }
        
        public Boolean getOrderExists() { return orderExists; }
        public void setOrderExists(Boolean orderExists) { this.orderExists = orderExists; }
        
        public Boolean getGatewayAvailable() { return gatewayAvailable; }
        public void setGatewayAvailable(Boolean gatewayAvailable) { this.gatewayAvailable = gatewayAvailable; }
        
        public String getError() { return error; }
        public void setError(String error) { this.error = error; }
    }
}