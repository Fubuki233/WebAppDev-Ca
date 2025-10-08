/**
 * @author Jiang
 * @date 2025-10-07
 * @version 1.0
 */

package sg.com.aori.service;

import sg.com.aori.interfaces.ICart;
import sg.com.aori.model.*;
import sg.com.aori.repository.CartRepository;
import sg.com.aori.repository.InventoryRepository;
import sg.com.aori.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class CartService implements ICart {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private InventoryRepository inventoryRepository;

    @Autowired
    private OrderRepository orderRepository;

    // Find cart by customer ID
    public List<ShoppingCart> findCartByCustomerId(String customerId) {
        return cartRepository.findByCustomerId(customerId);
    }

    // Calculate total amount for cart items
    public BigDecimal calculateTotal(List<ShoppingCart> cartItems) {
        return cartItems.stream()
                .map(item -> item.getVariant().getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    // Check inventory for all items in cart
    public boolean checkInventory(String customerId) {
        List<ShoppingCart> cartItems = findCartByCustomerId(customerId);
        
        for (ShoppingCart item : cartItems) {
            ProductVariant variant = item.getVariant();
            if (variant.getStockQuantity() < item.getQuantity()) {
                return false;
            }
        }
        return true;
    }

    // Create order from cart
    public String createOrder(String customerId) {
        List<ShoppingCart> cartItems = findCartByCustomerId(customerId);
        
        // Create order
        Orders order = new Orders();
        order.setOrderId(java.util.UUID.randomUUID().toString());
        
        // Set customer (in real app, get from authenticated user)
        Customer customer = new Customer();
        customer.setCustomerId(customerId);
        order.setCustomer(customer);
        
        order.setOrderStatus(Orders.OrderStatus.Pending);
        order.setPaymentStatus(Orders.PaymentStatus.Pending);
        
        // Calculate total amount
        BigDecimal totalAmount = calculateTotal(cartItems);
        order.setTotalAmount(totalAmount);
        order.setCreatedAt(LocalDateTime.now());
        
        // Save order
        Orders savedOrder = orderRepository.save(order);
        
        // Create order items and update inventory
        for (ShoppingCart cartItem : cartItems) {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrderId(java.util.UUID.randomUUID().toString());
            orderItem.setOrder(savedOrder);
            orderItem.setVariant(cartItem.getVariant());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setPriceAtPurchase(cartItem.getVariant().getPrice());
            orderItem.setDiscountApplied(BigDecimal.ZERO);
            
            // Update inventory
            ProductVariant variant = cartItem.getVariant();
            variant.setStockQuantity(variant.getStockQuantity() - cartItem.getQuantity());
            inventoryRepository.save(variant);
        }
        
        // Clear cart
        cartRepository.deleteByCustomerId(customerId);
        
        return savedOrder.getOrderId();
    }

    // Add item to cart
    public void addToCart(String customerId, String variantId, Integer quantity) {
        Optional<ProductVariant> variantOpt = inventoryRepository.findById(variantId);
        if (variantOpt.isEmpty()) {
            throw new RuntimeException("Product variant not found");
        }
        
        ProductVariant variant = variantOpt.get();
        if (variant.getStockQuantity() < quantity) {
            throw new RuntimeException("Insufficient inventory");
        }
        
        // Check if item already in cart
        Optional<ShoppingCart> existingCartItem = cartRepository.findByCustomerIdAndVariantId(customerId, variantId);
        
        if (existingCartItem.isPresent()) {
            // Update quantity
            ShoppingCart cartItem = existingCartItem.get();
            cartItem.setQuantity(cartItem.getQuantity() + quantity);
            cartRepository.save(cartItem);
        } else {
            // Create new cart item
            ShoppingCart cartItem = new ShoppingCart();
            // ***** Check if we need this UUID
            // ***** It is more likely needed?
            cartItem.setCartId(java.util.UUID.randomUUID().toString());
            
            Customer customer = new Customer();
            customer.setCustomerId(customerId);
            cartItem.setCustomer(customer);
            
            cartItem.setVariant(variant);
            cartItem.setQuantity(quantity);
            cartItem.setAddedAt(LocalDateTime.now());
            
            cartRepository.save(cartItem);
        }
    }

    // Remove item from cart
    public void removeFromCart(String cartId) {
        cartRepository.deleteById(cartId);
    }

    // Verify if product is in cart
    public boolean verifyInCart(String customerId, String variantId) {
        return cartRepository.findByCustomerIdAndVariantId(customerId, variantId).isPresent();
    }
}