/**
 * v1.1: REST API applied
 * v1.2: Test completed
 * @author Jiang
 * @date 2025-10-10
 * @version 1.2
 */

package sg.com.aori.controller;

import sg.com.aori.interfaces.IOrder;
import sg.com.aori.model.OrderItem;
import sg.com.aori.model.Orders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/order")
public class OrderController {

    @Autowired
    private IOrder orderService;

    /**
     * Get all orders for a customer
     * GET /api/orders
     * Returns: List of orders for the logged-in customer
     */
    @GetMapping("")
    public ResponseEntity<List<Orders>> getUserOrders(jakarta.servlet.http.HttpSession session) {
        try {
            String customerId = (String) session.getAttribute("id");
            if (customerId == null) {
                return ResponseEntity.status(401).build();
            }

            List<Orders> orders = orderService.findOrdersByCustomerId(customerId);
            return ResponseEntity.ok(orders);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Display order details page
    /*
     * {
     * "success": true,
     * "orderItems": [],
     * "order":
     * {
     * "orderId": "be5e8714-d0bc-4ef4-ad7a-a9b3cc5c2b64",
     * "orderNumber": null,
     * "customerId": "5f2f7b1d-c3d1-4a3e-abca-6447215ea70a",
     * "orderStatus": "Pending",
     * "totalAmount": 249.00,
     * "paymentStatus": "Pending",
     * "createdAt": "2025-10-10T00:42:15.918278",
     * "updatedAt": "2025-10-10T00:42:15.918278",
     * "customer":
     * {
     * "customerId": "5f2f7b1d-c3d1-4a3e-abca-6447215ea70a",
     * "firstName": "John",
     * "lastName": "Doe1",
     * "email": "john@example.com",
     * "password": "SecurePass123!",
     * "phoneNumber": null,
     * "gender": "Female",
     * "dateOfBirth": "1995-03-16",
     * "createdAt": "2025-10-09T00:20:58.212149",
     * "updatedAt": "2025-10-09T18:14:49.590697",
     * "hibernateLazyInitializer": {}
     * }
     * }
     * }
     */
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

    // Process payment
    // Be aware: timeout counter starts here
    /**
     * When payment success:
     * {
     * "success": true,
     * "orderStatus": "Paid",
     * "message": "Payment processed successfully",
     * "paymentStatus": "Paid"
     * }
     * When payment fail:
     * {
     * "success": false,
     * "message": "Payment failed",
     * "paymentStatus": "Failed"
     * }
     * When order or payment status is not 'Pending':
     * {
     * "success": false,
     * "message": "Order cannot be paid"
     * }
     */
    @PostMapping("/{orderId}/payment")
    public ResponseEntity<Map<String, Object>> processPayment(@Valid @PathVariable String orderId) {
        Map<String, Object> response = new HashMap<>();
        try {
            boolean paymentSuccess = orderService.processPayment(orderId);

            if (paymentSuccess) {
                response.put("success", true);
                response.put("message", "Payment processed successfully");
                response.put("orderStatus", "Paid");
                response.put("paymentStatus", "Paid");
                return ResponseEntity.ok(response);
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

    // Cancel order
    // Be aware: only changes order_status, doesn't change payment_status
    /*
     * {
     * "success": true,
     * "orderStatus": "Cancelled",
     * "message": "Order cancelled successfully"
     * }
     */
    @PostMapping("/{orderId}/cancellation")
    public ResponseEntity<Map<String, Object>> cancelOrder(@Valid @PathVariable String orderId) {
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

    // Get order status
    /*
     * {
     * "orderId": "be5e8714-d0bc-4ef4-ad7a-a9b3cc5c2b64",
     * "orderNumber": null,
     * "customerId": "5f2f7b1d-c3d1-4a3e-abca-6447215ea70a",
     * "orderStatus": "Pending",
     * "totalAmount": 249.00,
     * "paymentStatus": "Pending",
     * "createdAt": "2025-10-10T00:42:15.918278",
     * "updatedAt": "2025-10-10T00:42:15.918278",
     * "customer": {
     * "customerId": "5f2f7b1d-c3d1-4a3e-abca-6447215ea70a",
     * "firstName": "John",
     * "lastName": "Doe1",
     * "email": "john@example.com",
     * "password": "SecurePass123!",
     * "phoneNumber": null,
     * "gender": "Female",
     * "dateOfBirth": "1995-03-16",
     * "createdAt": "2025-10-09T00:20:58.212149",
     * "updatedAt": "2025-10-09T18:14:49.590697",
     * "hibernateLazyInitializer": {}
     * }
     * }
     */
    @GetMapping("/{orderId}/status")
    public ResponseEntity<Orders> getOrderStatus(@PathVariable String orderId) {
        Orders order = orderService.findOrderById(orderId);
        return ResponseEntity.ok(order);
    }

}