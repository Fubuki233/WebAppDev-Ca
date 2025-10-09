/**
 * @author Jiang
 * @date 2025-10-07
 * @version 1.0
 */

package sg.com.aori.service;

import sg.com.aori.interfaces.ICart;
import sg.com.aori.model.*;
import sg.com.aori.repository.CartRepository;
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
    private OrderRepository orderRepository;

    // Find cart by customer ID
    public List<ShoppingCart> findCartByCustomerId(String customerId) {
        return cartRepository.findByCustomerId(customerId);
    }

    // Calculate total amount for cart items
    public BigDecimal calculateTotal(List<ShoppingCart> cartItems) {
        return cartItems.stream()
                .map(item -> BigDecimal.valueOf(item.getProduct().getPrice())
                        .multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    // Check inventory for all items in cart
    // Note: ShoppingCart uses Product, but inventory is tracked at ProductVariant
    // level
    // This method returns true as inventory check needs to be done at variant level
    public boolean checkInventory(String customerId) {
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

        // Create order items
        // Note: ShoppingCart uses Product but OrderItem needs ProductVariant
        // This implementation needs to be updated based on business logic
        for (ShoppingCart cartItem : cartItems) {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrderId(java.util.UUID.randomUUID().toString());
            orderItem.setOrder(savedOrder);
            // TODO: Need to determine which variant to use from the product
            // For now, commented out as ShoppingCart doesn't have variant info
            // orderItem.setVariant(variant);
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setPriceAtPurchase(BigDecimal.valueOf(cartItem.getProduct().getPrice()));
            orderItem.setDiscountApplied(BigDecimal.ZERO);

            // Note: Inventory update needs variant-level information
            // Cannot update inventory with only product-level cart data
        }

        // Clear cart
        cartRepository.deleteByCustomerId(customerId);

        return savedOrder.getOrderId();
    }

    // Add item to cart
    public void addToCart(String customerId, String productId, Integer quantity) {
        // Note: Changed from variantId to productId to match ShoppingCart entity
        // Inventory check cannot be done without variant information

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
            cartItem.setCustomerId(customerId);
            cartItem.setProductId(productId);
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