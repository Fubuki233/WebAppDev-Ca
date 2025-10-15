
package sg.com.aori.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import sg.com.aori.interfaces.ICart;
import sg.com.aori.model.*;
import sg.com.aori.repository.*;

/**
 * @author Jiang
 * @version 1.0
 * @version 1.1 - Small adjustments, including a simple validation before
 *          creating order
 * @version 1.2 - Repaired the problem that orderItem cannot be added correctly
 * @version 1.3 - Added the function of auto generating order_number
 * @version 1.4 - Added sku
 * 
 * @author Yunhe
 * @date 2025-10-15
 * @version 1.5 - Fixed duplicate cart item issue - now checks SKU in addition
 *          to productId
 * @version 1.6 - Changed default order status to Shipped and payment status to
 *          Paid
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

    @Autowired
    private SkuService skuService;

    public List<ShoppingCart> findCartByCustomerId(String customerId) {
        return cartRepository.findByCustomerId(customerId);
    }

    public BigDecimal calculateTotal(List<ShoppingCart> cartItems) {
        return cartItems.stream()
                .map(item -> item.getProduct().getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public boolean checkInventory(String customerId) {
        List<ShoppingCart> cartItems = findCartByCustomerId(customerId);

        for (ShoppingCart item : cartItems) {
            String sku = item.getSku();
            int skuStock = skuService.getQuantity(sku);
            if (skuStock < item.getQuantity()) {
                return false;
            }
        }
        return true;
    }

    public String createOrder(String customerId) {

        try {
            System.out.println("Starting createOrder for customer: " + customerId);

            List<ShoppingCart> cartItems = findCartByCustomerId(customerId);
            System.out.println("Found " + cartItems.size() + " cart items");

            if (cartItems == null || cartItems.isEmpty()) {
                throw new RuntimeException("Cannot create order: Shopping cart is empty");
            }

            boolean inventoryAvailable = checkInventory(customerId);
            if (!inventoryAvailable) {
                throw new RuntimeException("Insufficient inventory for some items");
            }

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

                String sku = cartItem.getSku();
                int quantity = cartItem.getQuantity();
                System.out.println("Decreasing SKU " + sku + " inventory by " + quantity);
                int newSkuStock = skuService.decreaseQuantity(sku, quantity);

                if (newSkuStock < 0) {
                    throw new RuntimeException("Failed to update inventory for SKU: " + sku);
                }

                System.out.println("SKU " + sku + " new stock: " + newSkuStock);
            }

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

    public void addToCart(String customerId, String productId, Integer quantity, String sku) {
        Optional<Product> productOpt = inventoryRepository.findById(productId);
        if (productOpt.isEmpty()) {
            throw new RuntimeException("Product not found");
        }

        Product product = productOpt.get();

        int skuStock = skuService.getQuantity(sku);
        if (skuStock < quantity) {
            throw new RuntimeException("Insufficient inventory");
        }

        Optional<ShoppingCart> existingCartItem = cartRepository.findByCustomerIdAndProductIdAndSku(customerId,
                productId, sku);

        if (existingCartItem.isPresent()) {
            ShoppingCart cartItem = existingCartItem.get();
            int newQuantity = cartItem.getQuantity() + quantity;

            if (skuStock < newQuantity) {
                throw new RuntimeException("Insufficient inventory for requested quantity");
            }

            cartItem.setQuantity(newQuantity);
            cartRepository.save(cartItem);
        } else {
            ShoppingCart cartItem = new ShoppingCart();
            cartItem.setCartId(java.util.UUID.randomUUID().toString());

            Customer customer = new Customer();
            customer.setCustomerId(customerId);
            cartItem.setCustomer(customer);

            cartItem.setCustomerId(customerId);
            cartItem.setProductId(productId);
            cartItem.setSku(sku);
            cartItem.setProduct(product);
            cartItem.setQuantity(quantity);
            cartItem.setAddedAt(LocalDateTime.now());

            cartRepository.save(cartItem);
        }
    }

    public void removeFromCart(String cartId) {
        cartRepository.deleteById(cartId);
    }

    public boolean verifyInCart(String customerId, String productId) {
        return cartRepository.findByCustomerIdAndProductId(customerId, productId).isPresent();
    }
}