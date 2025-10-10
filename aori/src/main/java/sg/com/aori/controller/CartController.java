/**
 * v1.1: REST API applied
 * v1.2: Session applied
 * v1.3: Provide reference of how to get customerId from session
 * v1.4: Test completed
 * v 1.5: Add validation for addToCart
 * @author Jiang, Sun Rui
 * @date 2025-10-10
 * @version 1.4
 */

package sg.com.aori.controller;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpSession;
import sg.com.aori.interfaces.ICart;
import sg.com.aori.model.ShoppingCart;
import sg.com.aori.utils.getSession;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    private static final int MAX_QTY_PER_ITEM = 100; // 业务自行调整 在类里加一个上限常量************

    @Autowired
    private ICart cartService;

    // @Autowired
    // private CustomerService customerService;

    // private String getCustomerIdFromSession(HttpSession session) {
    // String email = (String) session.getAttribute("email");
    // if (email == null)
    // return null;
    // Customer customer = customerService.findCustomerByEmail(email).orElse(null);
    // return customer != null ? customer.getCustomerId() : null;
    // // ***** Use the statement below if customerId is stored in session
    // // return (String) session.getAttribute("customerId");
    // }
    /*
     * With 1 item in cart:
     * {
     * "totalAmount": 779.00,
     * "success": true,
     * "cartItems": [
     * {
     * "cartId": "7bb6e3ab-fba0-41d6-9623-d017703c9d4c",
     * "customerId": "5f2f7b1d-c3d1-4a3e-abca-6447215ea70a",
     * "productId": "8afc68df-80fe-479d-83d1-eb817bfeb597",
     * "quantity": 1,
     * "addedAt": "2025-10-10T03:38:03.619931",
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
     * },
     * "product": {
     * "productId": "8afc68df-80fe-479d-83d1-eb817bfeb597",
     * "productCode": "PROD-000003",
     * "productName": "Classic Polo Shirt-test",
     * "description": "High quality polo shirt",
     * "categoryId": "00c41711-68b0-4d03-a00b-67c6fba6ad87",
     * "collection": "Winter 2025",
     * "material": "Wool",
     * "season": "Winter",
     * "careInstructions": "Machine wash cold",
     * "createdAt": "2025-10-10T01:55:23.744753",
     * "updatedAt": "2025-10-10T03:29:38.847098",
     * "colors": "[\"#000000\", \"#FFFFFF\"]",
     * "image":
     * "https://images.unsplash.com/photo-1618354691373-d851c5c3a990?w=400&h=600&fit=crop",
     * "price": 779.00,
     * "stockQuantity": 98,
     * "size": "[\"XS\", \"S\", \"M\", \"L\", \"XL\"]",
     * "rating": 2.0,
     * "tags": "worst-seller",
     * "category": {
     * "categoryId": "00c41711-68b0-4d03-a00b-67c6fba6ad87",
     * "categoryCode": "CAT-M-001",
     * "categoryName": "Shirts-updated",
     * "broadCategoryId": "Men",
     * "slug": "mens-shirts",
     * "hibernateLazyInitializer": {}
     * },
     * "hibernateLazyInitializer": {}
     * }
     * }
     * ],
     * "itemCount": 1
     * }
     * When there is no any item in cart:
     * {
     * "totalAmount": 0,
     * "success": true,
     * "cartItems": [],
     * "itemCount": 0
     * }
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getCart(HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        try {
            // ***** Syntax for tapping onto 'utils'
            String customerId = getSession.getCustomerId(session);
            if (customerId == null) {
                // ***** Temporary use an existing id, use the annotated 3 lines in real app
                customerId = "5f2f7b1d-c3d1-4a3e-abca-6447215ea70a";
                // response.put("success", false);
                // response.put("message", "User not logged in");
                // return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }
            List<ShoppingCart> cartItems = cartService.findCartByCustomerId(customerId);
            BigDecimal totalAmount = cartService.calculateTotal(cartItems);

            response.put("success", true);
            response.put("cartItems", cartItems);
            response.put("totalAmount", totalAmount);
            response.put("itemCount", cartItems.size());
            System.out.println(
                    "customerId: " + customerId + ", Cart items: " + cartItems + ", Total amount: " + totalAmount);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    // Process checkout request
    /**
     * Normal situation:
     * {
     * "orderId": "e4d7afaa-f18e-4440-aa87-62a7994a9316",
     * "success": true,
     * "message": "Order created successfully"
     * }
     * When no items in cart:
     * {
     * "success": false,
     * "message": "Checkout failed: Cannot create order: Shopping cart is empty"
     * }
     */
    @PostMapping("/checkout")
    public ResponseEntity<Map<String, Object>> checkout(HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        try {
            String customerId = getSession.getCustomerId(session);
            if (customerId == null) {
                customerId = "5f2f7b1d-c3d1-4a3e-abca-6447215ea70a";
                // response.put("success", false);
                // response.put("message", "User not logged in");
                // return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }

            boolean inventoryAvailable = cartService.checkInventory(customerId);
            if (!inventoryAvailable) {
                response.put("success", false);
                response.put("message", "Insufficient inventory for some items");
                return ResponseEntity.badRequest().body(response);
            }

            String orderId = cartService.createOrder(customerId);

            response.put("success", true);
            response.put("message", "Order created successfully");
            response.put("orderId", orderId);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Checkout failed: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    // Add item to cart
    /*
     * JSON Input format:
     * *********
     * {
     * "productId": "8afc68df-80fe-479d-83d1-eb817bfeb597",
     * "quantity": 1
     * }
     * Output:
     * {
     * "success": true,
     * "message": "Product added to cart"
     * }
     */
    @PostMapping("/items")
    public ResponseEntity<Map<String, Object>> addToCart(@RequestBody Map<String, Object> request,
            HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        try {
            String customerId = getSession.getCustomerId(session);
            // if (customerId == null) {
            // customerId = "5f2f7b1d-c3d1-4a3e-abca-6447215ea70a";
            // // response.put("success", false);
            // // response.put("message", "User not logged in");
            // // return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            // }

            // String productId = (String) request.get("productId"); **********
            // Integer quantity = (Integer) request.get("quantity"); *********

            // —— productId：必须存在且非空 —— //
            Object pidObj = request.get("productId");
            if (!(pidObj instanceof String pid) || pid.isBlank()) {
                response.put("success", false);
                response.put("message", "productId is required");
                return ResponseEntity.badRequest().body(response);
            }
            String productId = (String) pidObj; // 或直接用上面的 pid

            // —— quantity：必须存在；兼容 Integer/Long/Double/String；范围校验 —— //
            Object qObj = request.get("quantity");
            Integer quantity = null;
            if (qObj instanceof Integer i) {
                quantity = i;
            } else if (qObj instanceof Long l) {
                try {
                    quantity = Math.toIntExact(l); // 防溢出
                } catch (ArithmeticException ex) {
                    response.put("success", false);
                    response.put("message", "quantity is too large");
                    return ResponseEntity.badRequest().body(response);
                }
            } else if (qObj instanceof Double d) {
                quantity = (int) Math.floor(d); // JSON 里若是小数，向下取整
            } else if (qObj instanceof String s) {
                try {
                    quantity = Integer.valueOf(s.trim());
                } catch (NumberFormatException ex) {
                    response.put("success", false);
                    response.put("message", "quantity must be an integer");
                    return ResponseEntity.badRequest().body(response);
                }
            }

            if (quantity == null) {
                response.put("success", false);
                response.put("message", "quantity is required");
                return ResponseEntity.badRequest().body(response);
            }
            if (quantity < 1) {
                response.put("success", false);
                response.put("message", "quantity must be >= 1");
                return ResponseEntity.badRequest().body(response);
            }
            if (quantity > MAX_QTY_PER_ITEM) {
                response.put("success", false);
                response.put("message", "quantity must be <= " + MAX_QTY_PER_ITEM);
                return ResponseEntity.badRequest().body(response);
            }

            cartService.addToCart(customerId, productId, quantity);

            response.put("success", true);
            response.put("message", "Product added to cart");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to add product: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    // Remove item from cart
    // Remove need the cart_id, so it almost must success
    /*
     * {
     * "success": true,
     * "message": "Item removed from cart"
     * }
     */
    @DeleteMapping("/items/{cartId}")
    public ResponseEntity<Map<String, Object>> removeFromCart(@PathVariable String cartId, HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        try {
            // ***** To reference session and get customerId
            String customerId = (String) session.getAttribute("customerId");
            if (customerId == null) {
                customerId = "5f2f7b1d-c3d1-4a3e-abca-6447215ea70a";
                // response.put("success", false);
                // response.put("message", "User not logged in");
                // return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }

            cartService.removeFromCart(cartId);

            response.put("success", true);
            response.put("message", "Item removed from cart");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to remove item: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
}
