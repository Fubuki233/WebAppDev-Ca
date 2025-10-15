package sg.com.aori.service;

import sg.com.aori.interfaces.ICart;
import sg.com.aori.model.*;
import sg.com.aori.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * @author Jiang
 * @date 2025-10-14
 * @version 1.0
 * @version 1.1 - Small adjustments, including a simple validation before creating order
 * @version 1.2 - Repaired the problem that orderItem cannot be added correctly.
 * @version 1.3 - Added the function of auto generating order_number
 * @version 1.4 - Added sku
 */

@Service
@Transactional
public class CartService implements ICart {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private InventoryRepository inventoryRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

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

        try {
            System.out.println("Starting createOrder for customer: " + customerId);
            
            List<ShoppingCart> cartItems = findCartByCustomerId(customerId);
            System.out.println("Found " + cartItems.size() + " cart items");
            
            if (cartItems == null || cartItems.isEmpty()) {
                throw new RuntimeException("Cannot create order: Shopping cart is empty");
            }
            
            // Check Inventory
            boolean inventoryAvailable = checkInventory(customerId);
            if (!inventoryAvailable) {
                throw new RuntimeException("Insufficient inventory for some items");
            }
            
            // Create order
            Orders order = new Orders();
            String orderId = java.util.UUID.randomUUID().toString();
            order.setOrderId(orderId);
            System.out.println("Creating order with ID: " + orderId);
            
            Customer customer = new Customer();
            customer.setCustomerId(customerId);
            order.setCustomer(customer);
            order.setCustomerId(customerId);
            
            order.setOrderStatus(Orders.OrderStatus.Pending);
            order.setPaymentStatus(Orders.PaymentStatus.Pending);
            
            BigDecimal totalAmount = calculateTotal(cartItems);
            order.setTotalAmount(totalAmount);
            order.setCreatedAt(LocalDateTime.now());

            String orderNumber = generateOrderNumber(orderId, LocalDateTime.now());
            order.setOrderNumber(orderNumber);
            
            System.out.println("Saving order to database...");
            Orders savedOrder = orderRepository.save(order);
            System.out.println("Order saved successfully");
            
            // Create order items
            System.out.println("Creating order items...");
            for (ShoppingCart cartItem : cartItems) {
                OrderItem orderItem = new OrderItem();
                orderItem.setOrderId(savedOrder.getOrderId());
                orderItem.setProductId(cartItem.getProductId());
                orderItem.setQuantity(cartItem.getQuantity());
                orderItem.setPriceAtPurchase(cartItem.getProduct().getPrice());
                orderItem.setDiscountApplied(BigDecimal.ZERO);
                orderItem.setSku(cartItem.getSku());
                
                System.out.println("Saving order item: " + orderItem);
                orderItemRepository.save(orderItem);
                
                // Update inventory
                Product product = cartItem.getProduct();
                int newStock = product.getStockQuantity() - cartItem.getQuantity();
                System.out.println("Updating product " + product.getProductId() + " stock from " + 
                                product.getStockQuantity() + " to " + newStock);
                product.setStockQuantity(newStock);
                inventoryRepository.save(product);
            }
            
            // Delete cart
            System.out.println("Clearing cart for customer: " + customerId);
            cartRepository.deleteByCustomerId(customerId);
            
            System.out.println("Order creation completed successfully");
            return savedOrder.getOrderId();
            
        } catch (Exception e) {
            System.err.println("Error in createOrder: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    private String generateOrderNumber(String orderId, LocalDateTime createdAt) {
        String timePart = createdAt.format(DateTimeFormatter.ofPattern("yyyyMMddHHmm"));
        String idPart = orderId.substring(0, 4);
        return "ORD-" + timePart + "-" + idPart;
    }

    // Add item to cart
    public void addToCart(String customerId, String productId, Integer quantity, String sku) {
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

            cartItem.setCartId(java.util.UUID.randomUUID().toString());

            Customer customer = new Customer();
            customer.setCustomerId(customerId);
            cartItem.setCustomer(customer);

            // Set customerid and productId directly
            cartItem.setCustomerId(customerId);
            cartItem.setProductId(productId);
            cartItem.setSku(sku);
            cartItem.setProduct(product);
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
    public boolean verifyInCart(String customerId, String productId) {
        return cartRepository.findByCustomerIdAndProductId(customerId, productId).isPresent();
    }
}