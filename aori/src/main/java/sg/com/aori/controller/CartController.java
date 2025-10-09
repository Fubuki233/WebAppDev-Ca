/**
 * v1.1: REST API applied
 * v1.2: Session applied
 * @author Jiang
 * @date 2025-10-08
 * @version 1.2
 */

package sg.com.aori.controller;

import sg.com.aori.interfaces.ICart;
import sg.com.aori.model.Customer;
import sg.com.aori.model.ShoppingCart;
import sg.com.aori.service.CustomerService;
import sg.com.aori.utils.LoginValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    @Autowired
    private ICart cartService;

    @Autowired
    private CustomerService customerService;

    private String getCustomerIdFromSession(HttpSession session) {
        String id = (String) session.getAttribute("id");
        if (id == null)
            return null;
        Customer customer = customerService.findCustomerByEmail(id).orElse(null);
        return customer != null ? customer.getCustomerId() : null;
        // ***** Use the statement below if customerId is stored in session
        // return (String) session.getAttribute("customerId");
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> getCart(HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        try {
            String customerId = getCustomerIdFromSession(session);
            if (customerId == null) {
                response.put("success", false);
                response.put("message", "User not logged in");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }
            List<ShoppingCart> cartItems = cartService.findCartByCustomerId(customerId);
            BigDecimal totalAmount = cartService.calculateTotal(cartItems);

            response.put("success", true);
            response.put("cartItems", cartItems);
            response.put("totalAmount", totalAmount);
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
    @PostMapping("/checkout")
    public ResponseEntity<Map<String, Object>> checkout(HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        try {
            String customerId = getCustomerIdFromSession(session);
            if (customerId == null) {
                response.put("success", false);
                response.put("message", "User not logged in");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
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
    @PostMapping("/items")
    public ResponseEntity<Map<String, Object>> addToCart(@RequestBody Map<String, Object> request,
            HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        try {
            String customerId = getCustomerIdFromSession(session);
            if (customerId == null) {
                response.put("success", false);
                response.put("message", "User not logged in");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }

            String variantId = (String) request.get("variantId");
            Integer quantity = (Integer) request.get("quantity");

            cartService.addToCart(customerId, variantId, quantity);

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
    @DeleteMapping("/items/{cartId}")
    public ResponseEntity<Map<String, Object>> removeFromCart(@PathVariable String cartId, HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        try {
            String customerId = getCustomerIdFromSession(session);
            if (customerId == null) {
                response.put("success", false);
                response.put("message", "User not logged in");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
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
