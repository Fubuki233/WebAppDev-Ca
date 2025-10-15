package sg.com.aori.controller;

import sg.com.aori.interfaces.IOrder;
import sg.com.aori.model.OrderItem;
import sg.com.aori.model.Orders;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import jakarta.validation.Valid;

/**
 * @author Jiang
 * @date 2025-10-10
 * @version 2.0 - REST API applied
 * @version 2.1 - Test completed
 */

@RestController
@RequestMapping("/api/order")
public class OrderController {

    @Autowired
    private IOrder orderService;

    /**
     * Get all orders for a customer
     * ATTENTION: Test result attached at the end of the file,including 6 orders
     */
    @GetMapping("")
    public ResponseEntity<List<Orders>> getUserOrders(jakarta.servlet.http.HttpSession session) {
        try {
            String customerId = (String) session.getAttribute("id");
            List<Orders> orders = orderService.findOrdersByCustomerId(customerId);
            return ResponseEntity.ok(orders);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Display order details page
     * Example response:
     * 
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

    /**
     * Process payment
     * ATTENTION : Payment timeout counter starts here
     * Example response:
     * 
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

    /**
     * Cancel order
     * 
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

    /**
     * Get order status
     * Example output:
     * 
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

/**
 * Example response of getUserOrders
 * 
 * [
 * {
 * "orderId": "7e5bd44e-c12a-4ee7-a150-c87aba96c483",
 * "orderNumber": "ORD-202510111807-7e5b",
 * "customerId": "07532ea4-8954-5e60-86da-c1b7844e0a7f",
 * "orderStatus": "Pending",
 * "totalAmount": 33.09,
 * "paymentStatus": "Pending",
 * "createdAt": "2025-10-11T18:07:16",
 * "updatedAt": "2025-10-11T18:07:16",
 * "customer": {
 * "customerId": "07532ea4-8954-5e60-86da-c1b7844e0a7f",
 * "firstName": "John",
 * "lastName": "Tang",
 * "email": "john@example.com",
 * "password": "SecurePass123!",
 * "phoneNumber": "+6588112233",
 * "gender": "Undisclosed",
 * "dateOfBirth": "1991-02-24",
 * "createdAt": "2025-05-21T17:22:33",
 * "updatedAt": "2025-10-11T07:56:05",
 * "hibernateLazyInitializer": {}
 * }
 * },
 * {
 * "orderId": "1d5eb9a2-b9e4-512f-bdbc-a55889be8ed2",
 * "orderNumber": "ORD-2025-000009",
 * "customerId": "07532ea4-8954-5e60-86da-c1b7844e0a7f",
 * "orderStatus": "Returned",
 * "totalAmount": 108.14,
 * "paymentStatus": "Paid",
 * "createdAt": "2025-10-08T21:21:14",
 * "updatedAt": "2025-10-11T07:00:16",
 * "customer": {
 * "customerId": "07532ea4-8954-5e60-86da-c1b7844e0a7f",
 * "firstName": "John",
 * "lastName": "Tang",
 * "email": "john@example.com",
 * "password": "SecurePass123!",
 * "phoneNumber": "+6588112233",
 * "gender": "Undisclosed",
 * "dateOfBirth": "1991-02-24",
 * "createdAt": "2025-05-21T17:22:33",
 * "updatedAt": "2025-10-11T07:56:05",
 * "hibernateLazyInitializer": {}
 * }
 * },
 * {
 * "orderId": "3df61d5b-f7bc-5177-af59-4db40a17dc1e",
 * "orderNumber": "ORD-2025-000010",
 * "customerId": "07532ea4-8954-5e60-86da-c1b7844e0a7f",
 * "orderStatus": "Delivered",
 * "totalAmount": 260.41,
 * "paymentStatus": "Paid",
 * "createdAt": "2025-10-02T00:21:20",
 * "updatedAt": "2025-10-11T07:00:16",
 * "customer": {
 * "customerId": "07532ea4-8954-5e60-86da-c1b7844e0a7f",
 * "firstName": "John",
 * "lastName": "Tang",
 * "email": "john@example.com",
 * "password": "SecurePass123!",
 * "phoneNumber": "+6588112233",
 * "gender": "Undisclosed",
 * "dateOfBirth": "1991-02-24",
 * "createdAt": "2025-05-21T17:22:33",
 * "updatedAt": "2025-10-11T07:56:05",
 * "hibernateLazyInitializer": {}
 * }
 * },
 * {
 * "orderId": "0fce792f-23da-5b92-82c0-1bd62736a109",
 * "orderNumber": "ORD-2025-000005",
 * "customerId": "07532ea4-8954-5e60-86da-c1b7844e0a7f",
 * "orderStatus": "Delivered",
 * "totalAmount": 138.49,
 * "paymentStatus": "Paid",
 * "createdAt": "2025-09-25T21:50:33",
 * "updatedAt": "2025-10-11T07:00:16",
 * "customer": {
 * "customerId": "07532ea4-8954-5e60-86da-c1b7844e0a7f",
 * "firstName": "John",
 * "lastName": "Tang",
 * "email": "john@example.com",
 * "password": "SecurePass123!",
 * "phoneNumber": "+6588112233",
 * "gender": "Undisclosed",
 * "dateOfBirth": "1991-02-24",
 * "createdAt": "2025-05-21T17:22:33",
 * "updatedAt": "2025-10-11T07:56:05",
 * "hibernateLazyInitializer": {}
 * }
 * },
 * {
 * "orderId": "2b632d36-d3b7-5af1-8b84-68640fb424e4",
 * "orderNumber": "ORD-2025-000004",
 * "customerId": "07532ea4-8954-5e60-86da-c1b7844e0a7f",
 * "orderStatus": "Returned",
 * "totalAmount": 924.21,
 * "paymentStatus": "Paid",
 * "createdAt": "2025-09-25T14:02:36",
 * "updatedAt": "2025-10-11T07:00:16",
 * "customer": {
 * "customerId": "07532ea4-8954-5e60-86da-c1b7844e0a7f",
 * "firstName": "John",
 * "lastName": "Tang",
 * "email": "john@example.com",
 * "password": "SecurePass123!",
 * "phoneNumber": "+6588112233",
 * "gender": "Undisclosed",
 * "dateOfBirth": "1991-02-24",
 * "createdAt": "2025-05-21T17:22:33",
 * "updatedAt": "2025-10-11T07:56:05",
 * "hibernateLazyInitializer": {}
 * }
 * },
 * {
 * "orderId": "3b4b9dac-e9c3-575c-b27a-bb87b39f2550",
 * "orderNumber": "ORD-2025-000007",
 * "customerId": "07532ea4-8954-5e60-86da-c1b7844e0a7f",
 * "orderStatus": "Returned",
 * "totalAmount": 161.25,
 * "paymentStatus": "Paid",
 * "createdAt": "2025-09-15T07:34:26",
 * "updatedAt": "2025-10-11T07:00:16",
 * "customer": {
 * "customerId": "07532ea4-8954-5e60-86da-c1b7844e0a7f",
 * "firstName": "John",
 * "lastName": "Tang",
 * "email": "john@example.com",
 * "password": "SecurePass123!",
 * "phoneNumber": "+6588112233",
 * "gender": "Undisclosed",
 * "dateOfBirth": "1991-02-24",
 * "createdAt": "2025-05-21T17:22:33",
 * "updatedAt": "2025-10-11T07:56:05",
 * "hibernateLazyInitializer": {}
 * }
 * }
 * ]
 */