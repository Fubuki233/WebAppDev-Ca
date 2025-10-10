/**
 * v1.1: Small adjustments, including a simple validation before creating order
 * ATTETION: orderItem can't be added correctly, further modification needed
 * @author Jiang
 * @date 2025-10-10
 * @version 1.1
 * 
 * Debug for addToCart 
 * @author Yunhe
 * @date 2025-10-10
 * @version 1.2
 */

package sg.com.aori.service;

import sg.com.aori.interfaces.ICart;
import sg.com.aori.model.*;
import sg.com.aori.repository.CartRepository;
import sg.com.aori.repository.CustomerRepository;
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
    private CustomerRepository customerRepository;

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

        if (cartItems == null || cartItems.isEmpty()) {
            throw new RuntimeException("Cannot create order: Shopping cart is empty");
        }

        // Create order
        Orders order = new Orders();
        order.setOrderId(java.util.UUID.randomUUID().toString());

        // Set customer (in real app, get from authenticated user)
        // ***** Check this part again though it can work
        Customer customer = new Customer();
        customer.setCustomerId(customerId);
        order.setCustomer(customer);
        order.setCustomerId(customerId);

        order.setOrderStatus(Orders.OrderStatus.Pending);
        order.setPaymentStatus(Orders.PaymentStatus.Pending);

        // Calculate total amount
        BigDecimal totalAmount = calculateTotal(cartItems);
        order.setTotalAmount(totalAmount);
        order.setCreatedAt(LocalDateTime.now());

        // ***** Check if we should set a order_number
        // order.setOrderNumber(order.getOrderId());

        // Save order
        System.out.println("-----------------------------------" + order.getOrderId());
        Orders savedOrder = orderRepository.save(order);

        // Create order items and update inventory
        for (ShoppingCart cartItem : cartItems) {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrderItemId(java.util.UUID.randomUUID().toString());
            orderItem.setOrder(savedOrder);
            orderItem.setOrderId(savedOrder.getOrderId());
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
        System.out.println("-----------------------------------" + savedOrder.getCustomerId());
        System.out.println("-----------------------------------" + order.getCustomerId());

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

            // Fetch existing customer from database
            Customer customer = customerRepository.findById(customerId)
                    .orElseThrow(() -> new RuntimeException("Customer not found with ID: " + customerId));
            cartItem.setCustomer(customer);

            // Set customerid and productId directly
            cartItem.setCustomerId(customerId);
            cartItem.setProductId(productId);

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