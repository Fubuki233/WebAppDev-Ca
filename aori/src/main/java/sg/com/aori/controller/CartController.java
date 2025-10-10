/**
 * v1.1: REST API applied
 * v1.2: Session applied
 * v1.3: Provide reference of how to get customerId from session, add validation
 * @author Jiang, Sun Rui
 * @date 2025-10-08
 * @version 1.3
 */

package sg.com.aori.controller;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import sg.com.aori.interfaces.ICart;
import sg.com.aori.model.ShoppingCart;
import sg.com.aori.utils.getSession;

@RestController
@RequestMapping("/api/cart")
public class CartController {

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

    @GetMapping
    public ResponseEntity<Map<String, Object>> getCart(HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        try {
            // ***** Syntax for tapping onto 'utils'
            String customerId = getSession.getCustomerId(session);
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
                   "[CartController] " + "customerId: " + customerId + ", Cart items: " + cartItems + ", Total amount: " + totalAmount);
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
            String customerId = getSession.getCustomerId(session);
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
            String customerId = getSession.getCustomerId(session);
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
            // ***** To reference session and get customerId
            String customerId = (String) session.getAttribute("customerId");
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
