/**
 * v1.1: REST API applied
 * @author Jiang
 * @date 2025-10-07
 * @version 1.1
 */

package sg.com.aori.controller;

import sg.com.aori.interfaces.ICart;
import sg.com.aori.model.ShoppingCart;
// import sg.com.aori.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
// import org.springframework.stereotype.Controller;
// import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
// import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// @Controller
// @RequestMapping("/cart")
@RestController
@RequestMapping("/api/cart")
public class CartController {

    @Autowired
    private ICart cartService;

    // ----- Display shopping cart page

    // @GetMapping
    // public String viewCart(Model model) {
    //     // For demo purposes, using a fixed customer ID
    //     // ***** Must be modified
    //     String customerId = "demo-customer-id";
        
    //     List<ShoppingCart> cartItems = cartService.findCartByCustomerId(customerId);
    //     BigDecimal totalAmount = cartService.calculateTotal(cartItems);
        
    //     model.addAttribute("cartItems", cartItems);
    //     model.addAttribute("totalAmount", totalAmount);
        
    //     return "Cart";
    // }

    @GetMapping
    public ResponseEntity<Map<String, Object>> getCart() {
        Map<String, Object> response = new HashMap<>();
        try {
            // For demo purposes, using a fixed customer ID
            // ***** Must be modified
            String customerId = "demo-customer-id";
            List<ShoppingCart> cartItems = cartService.findCartByCustomerId(customerId);
            BigDecimal totalAmount = cartService.calculateTotal(cartItems);
            
            response.put("success", true);
            response.put("cartItems", cartItems);
            response.put("totalAmount", totalAmount);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    // ----- Process checkout request

    // @PostMapping("/checkout")
    // public String checkout(RedirectAttributes redirectAttributes) {
    //     try {
    //         // ***** Must be modified
    //         String customerId = "demo-customer-id";
            
    //         // Check inventory for all items in cart
    //         boolean inventoryAvailable = cartService.checkInventory(customerId);
    //         if (!inventoryAvailable) {
    //             redirectAttributes.addFlashAttribute("error", "Insufficient inventory for some items");
    //             return "redirect:/cart";
    //         }
            
    //         // Create order
    //         String orderId = cartService.createOrder(customerId);
            
    //         redirectAttributes.addFlashAttribute("success", "Order created successfully");
    //         return "redirect:/order/" + orderId;
            
    //     } catch (Exception e) {
    //         redirectAttributes.addFlashAttribute("error", "Checkout failed: " + e.getMessage());
    //         return "redirect:/cart";
    //     }
    // }

    @PostMapping("/checkout")
    public ResponseEntity<Map<String, Object>> checkout() {
        Map<String, Object> response = new HashMap<>();
        try {
            String customerId = "demo-customer-id";
            
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

    // ----- Add item to cart

    // @PostMapping("/add")
    // public String addToCart(@RequestParam String variantId, 
    //                        @RequestParam Integer quantity,
    //                        RedirectAttributes redirectAttributes) {
    //     try {
    //         String customerId = "demo-customer-id";
    //         cartService.addToCart(customerId, variantId, quantity);
    //         redirectAttributes.addFlashAttribute("success", "Product added to cart");
    //     } catch (Exception e) {
    //         redirectAttributes.addFlashAttribute("error", "Failed to add product: " + e.getMessage());
    //     }
    //     return "redirect:/products";
    // }

    @PostMapping("/items")
    public ResponseEntity<Map<String, Object>> addToCart(@RequestBody Map<String, Object> request) {
        Map<String, Object> response = new HashMap<>();
        try {
            String customerId = "demo-customer-id";
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

    // ----- Remove item from cart

    // @PostMapping("/remove")
    // public String removeFromCart(@RequestParam String cartId, RedirectAttributes redirectAttributes) {
    //     try {
    //         cartService.removeFromCart(cartId);
    //         redirectAttributes.addFlashAttribute("success", "Item removed from cart");
    //     } catch (Exception e) {
    //         redirectAttributes.addFlashAttribute("error", "Failed to remove item: " + e.getMessage());
    //     }
    //     return "redirect:/cart";
    // }

    @DeleteMapping("/items/{cartId}")
    public ResponseEntity<Map<String, Object>> removeFromCart(@PathVariable String cartId) {
        Map<String, Object> response = new HashMap<>();
        try {
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