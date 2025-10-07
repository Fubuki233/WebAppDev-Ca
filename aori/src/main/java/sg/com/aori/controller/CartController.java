/**
 * @author Jiang
 * @date 2025-10-07
 * @version 1.0
 */

package sg.com.aori.controller;

import sg.com.aori.interfaces.ICart;
import sg.com.aori.model.ShoppingCart;
// import sg.com.aori.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.List;

@Controller
@RequestMapping("/cart")
public class CartController {

    @Autowired
    private ICart cartService;

    // Display shopping cart page
    @GetMapping
    public String viewCart(Model model) {
        // For demo purposes, using a fixed customer ID
        // ***** Must be modified
        String customerId = "demo-customer-id";
        
        List<ShoppingCart> cartItems = cartService.findCartByCustomerId(customerId);
        BigDecimal totalAmount = cartService.calculateTotal(cartItems);
        
        model.addAttribute("cartItems", cartItems);
        model.addAttribute("totalAmount", totalAmount);
        
        return "Cart";
    }

    // Process checkout request
    @PostMapping("/checkout")
    public String checkout(RedirectAttributes redirectAttributes) {
        try {
            // ***** Must be modified
            String customerId = "demo-customer-id";
            
            // Check inventory for all items in cart
            boolean inventoryAvailable = cartService.checkInventory(customerId);
            if (!inventoryAvailable) {
                redirectAttributes.addFlashAttribute("error", "Insufficient inventory for some items");
                return "redirect:/cart";
            }
            
            // Create order
            String orderId = cartService.createOrder(customerId);
            
            redirectAttributes.addFlashAttribute("success", "Order created successfully");
            return "redirect:/order/" + orderId;
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Checkout failed: " + e.getMessage());
            return "redirect:/cart";
        }
    }

    // Add item to cart
    @PostMapping("/add")
    public String addToCart(@RequestParam String variantId, 
                           @RequestParam Integer quantity,
                           RedirectAttributes redirectAttributes) {
        try {
            String customerId = "demo-customer-id";
            cartService.addToCart(customerId, variantId, quantity);
            redirectAttributes.addFlashAttribute("success", "Product added to cart");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to add product: " + e.getMessage());
        }
        return "redirect:/products";
    }

    // Remove item from cart
    @PostMapping("/remove")
    public String removeFromCart(@RequestParam String cartId, RedirectAttributes redirectAttributes) {
        try {
            cartService.removeFromCart(cartId);
            redirectAttributes.addFlashAttribute("success", "Item removed from cart");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to remove item: " + e.getMessage());
        }
        return "redirect:/cart";
    }
}