/**
 * v1.1: Added method findByOrderId
 * @author Jiang
 * @date 2025-10-07
 * @version 1.1
 */

package sg.com.aori.repository;

import sg.com.aori.model.OrderItem;
import sg.com.aori.model.Orders;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Orders, String> {

    // Find all orders by customer ID
    List<Orders> findByCustomerId(String customerId);

    List<Orders> findByOrderId(String orderId);

    // Find latest order by customer ID
    Orders findTopByCustomerIdOrderByCreatedAtDesc(String customerId);

    // Find order items by order ID
    @Query("SELECT oi FROM OrderItem oi WHERE oi.order.id = :orderId")
    List<OrderItem> findOrderItemsByOrderId(@Param("orderId") String orderId);

    // Find orders by status
    List<Orders> findByOrderStatus(Orders.OrderStatus orderStatus);

    // Find orders by payment status
    List<Orders> findByPaymentStatus(Orders.PaymentStatus paymentStatus);

    // Check if order exists and is accessible by customer
    @Query("SELECT COUNT(o) > 0 FROM Orders o WHERE o.id = :orderId AND o.customer.id = :customerId")
    boolean existsByIdAndCustomerId(@Param("orderId") String orderId, @Param("customerId") String customerId);

    /**
     * xiaobo Date
     * 2025-10-09
     * Securely finds the OrderItem associated with a specific order, product, and
     * customer.
     * This is Step 5 (Find Order) in the return process and includes a crucial
     * security check.
     */
    @Query("SELECT oi FROM OrderItem oi " +
            "WHERE oi.order.orderId = :orderId " +
            "AND oi.productId = :productId " +
            "AND oi.order.customerId = :customerId")
    Optional<OrderItem> findOrderItemForReturn(
            @Param("orderId") String orderId,
            @Param("productId") String productId,
            @Param("customerId") String customerId);
}