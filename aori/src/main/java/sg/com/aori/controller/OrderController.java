/**
 * v1.1: REST API applied
 * @author Jiang
 * @date 2025-10-07
 * @version 1.1
 */

package sg.com.aori.controller;

import sg.com.aori.interfaces.IOrder;
import sg.com.aori.model.OrderItem;
import sg.com.aori.model.Orders;
// import sg.com.aori.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
// import org.springframework.stereotype.Controller;
// import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

// @Controller
// @RequestMapping("/order")
@RestController
@RequestMapping("/api/order")
public class OrderController {

    @Autowired
    private IOrder orderService;

    // ----- Display order details page

    // @GetMapping("/{orderId}")
    // public String viewOrder(@PathVariable String orderId, Model model) {
    //     Orders order = orderService.findOrderById(orderId);
    //     List<OrderItem> orderItems = orderService.findOrderItemsByOrderId(orderId);
        
    //     model.addAttribute("order", order);
    //     model.addAttribute("orderItems", orderItems);
        
    //     return "Order";
    // }

    @GetMapping("/{orderId}")
    public ResponseEntity<Map<String, Object>> getOrder(@PathVariable String orderId) {
        Map<String, Object> response = new HashMap<>();
        try {
            Orders order = orderService.findOrderById(orderId);
            List<OrderItem> orderItems = orderService.findOrderItemsByOrderId(orderId);
            
            response.put("success", true);
            response.put("order", order);
            response.put("orderItems", orderItems);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    // ----- Process payment

    // @PostMapping("/pay")
    // @ResponseBody
    // public ResponseEntity<Map<String, Object>> processPayment() {
    //     Map<String, Object> response = new HashMap<>();
        
    //     try {
    //         // For demo, using the latest order
    //         // ***** Must be modified
    //         String customerId = "demo-customer-id";
    //         Orders latestOrder = orderService.findLatestOrderByCustomerId(customerId);
            
    //         if (latestOrder == null) {
    //             response.put("success", false);
    //             response.put("message", "No order found");
    //             return ResponseEntity.badRequest().body(response);
    //         }
            
    //         boolean paymentSuccess = orderService.processPayment(latestOrder.getOrderId());
            
    //         if (paymentSuccess) {
    //             response.put("success", true);
    //             response.put("message", "Payment processed successfully");
    //         } else {
    //             response.put("success", false);
    //             response.put("message", "Payment failed");
    //         }
            
    //         return ResponseEntity.ok(response);
            
    //     } catch (Exception e) {
    //         response.put("success", false);
    //         response.put("message", e.getMessage());
    //         return ResponseEntity.badRequest().body(response);
    //     }
    // }

    @PostMapping("/{orderId}/payment")
    public ResponseEntity<Map<String, Object>> processPayment(@PathVariable String orderId) {
        Map<String, Object> response = new HashMap<>();
        try {
            boolean paymentSuccess = orderService.processPayment(orderId);
            
            if (paymentSuccess) {
                response.put("success", true);
                response.put("message", "Payment processed successfully");
                response.put("orderStatus", "Paid");
                response.put("paymentStatus", "Paid");
            } else {
                response.put("success", false);
                response.put("message", "Payment failed");
                response.put("paymentStatus", "Failed");
            }
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    // ----- Cancel order

    // @PostMapping("/cancel")
    // @ResponseBody
    // public ResponseEntity<Map<String, Object>> cancelOrder() {
    //     Map<String, Object> response = new HashMap<>();
        
    //     try {
    //         String customerId = "demo-customer-id";
    //         Orders latestOrder = orderService.findLatestOrderByCustomerId(customerId);
            
    //         if (latestOrder == null) {
    //             response.put("success", false);
    //             response.put("message", "No order found");
    //             return ResponseEntity.badRequest().body(response);
    //         }
            
    //         orderService.cancelOrder(latestOrder.getOrderId());
            
    //         response.put("success", true);
    //         response.put("message", "Order cancelled successfully");
    //         return ResponseEntity.ok(response);
            
    //     } catch (Exception e) {
    //         response.put("success", false);
    //         response.put("message", e.getMessage());
    //         return ResponseEntity.badRequest().body(response);
    //     }
    // }

    @PostMapping("/{orderId}/cancellation")
    public ResponseEntity<Map<String, Object>> cancelOrder(@PathVariable String orderId) {
        Map<String, Object> response = new HashMap<>();
        try {
            orderService.cancelOrder(orderId);
            
            response.put("success", true);
            response.put("message", "Order cancelled successfully");
            response.put("orderStatus", "Cancelled");
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    // ----- Get order status

    // @GetMapping("/status/{orderId}")
    // @ResponseBody
    // public ResponseEntity<Orders> getOrderStatus(@PathVariable String orderId) {
    //     Orders order = orderService.findOrderById(orderId);
    //     return ResponseEntity.ok(order);
    // }

    @GetMapping("/{orderId}/status")
    public ResponseEntity<Orders> getOrderStatus(@PathVariable String orderId) {
        Orders order = orderService.findOrderById(orderId);
        return ResponseEntity.ok(order);
    }

}