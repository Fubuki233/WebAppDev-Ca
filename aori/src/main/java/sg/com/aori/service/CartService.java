/**
 * @author Jiang
 * @date 2025-10-07
 * @version 1.0
 */

package sg.com.aori.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import sg.com.aori.interfaces.ICart;
import sg.com.aori.model.Customer;
import sg.com.aori.model.OrderItem;
import sg.com.aori.model.Orders;
import sg.com.aori.model.Product;
import sg.com.aori.model.ShoppingCart;
import sg.com.aori.repository.CartRepository;
import sg.com.aori.repository.InventoryRepository;
import sg.com.aori.repository.OrderRepository;

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
                .map(item -> item.getProduct().getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    // Check inventory for all items in cart
    public boolean checkInventory(String customerId) {
        List<ShoppingCart> cartItems = findCartByCustomerId(customerId);
        
        for (ShoppingCart item : cartItems) {
            Product product = item.getProduct();
            if (product.getStockQuantity() < item.getQuantity()) {
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
            orderItem.setProduct(cartItem.getProduct());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setPriceAtPurchase(cartItem.getProduct().getPrice());
            orderItem.setDiscountApplied(BigDecimal.ZERO);
            
            // Update inventory
            Product product = cartItem.getProduct();
            product.setStockQuantity(product.getStockQuantity() - cartItem.getQuantity());
            inventoryRepository.save(product);
        }
        
        // Clear cart
        cartRepository.deleteByCustomerId(customerId);
        
        return savedOrder.getOrderId();
    }

    // Add item to cart
    public void addToCart(String customerId, String productId, Integer quantity) {
        Optional<Product> productOpt = inventoryRepository.findById(productId);
        if (productOpt.isEmpty()) {
            throw new RuntimeException("Product not found");
        }
        
        Product product = productOpt.get();
        if (product.getStockQuantity() < quantity) {
            throw new RuntimeException("Insufficient inventory");
        }
        
        // Check if item already in cart
        Optional<ShoppingCart> existingCartItem = cartRepository.findByCustomerIdAndProductId(customerId, productId);
        
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
            
            cartItem.setProduct(product);
            cartItem.setQuantity(quantity);
            cartItem.setAddedAt(LocalDateTime.now());
            
            cartRepository.save(cartItem);
        }
       
        // Basic validation, add defensive checks to prevent bypassing the controller
        if (productId == null || productId.isBlank()) {
        throw new IllegalArgumentException("productId is required");
        }
        if (quantity == null || quantity <= 0) {
        throw new IllegalArgumentException("Quantity must be greater than zero");
        }
    }

    // Remove item from cart
    public void removeFromCart(String cartId) {
        cartRepository.deleteById(cartId);
    }

    // Verify if product is in cart
    public boolean verifyInCart(String customerId, String productId) {
        return cartRepository.findByCustomerIdAndProductId(customerId, productId).isPresent();
    }
}