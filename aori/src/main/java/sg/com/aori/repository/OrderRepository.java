package sg.com.aori.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import sg.com.aori.model.OrderItem;
import sg.com.aori.model.Orders;

/**
 * @author Yibai
 * @date 2025-10-07
 * @version 1.0
 * @version 1.1 - Added findByOrderId
 * 
 * @author Derek
 * @version 1.2 - Added findByOrderIdAndCustomerId
 * 
 * @author Xiaobo
 * @version 1.3 - Added findOrderItemForReturn
 * 
 * @author Ying Chun
 * @version 1.4 - Added findByIdWithCustomer
 */

@Repository
public interface OrderRepository extends JpaRepository<Orders, String> {

        List<Orders> findByCustomerId(String customerId);

        List<Orders> findByCustomerIdOrderByCreatedAtDesc(String customerId);

        List<Orders> findByOrderId(String orderId);

        Orders findTopByCustomerIdOrderByCreatedAtDesc(String customerId);

        @Query("SELECT oi FROM OrderItem oi LEFT JOIN FETCH oi.product WHERE oi.order.id = :orderId")
        List<OrderItem> findOrderItemsByOrderId(@Param("orderId") String orderId);

        List<Orders> findByOrderStatus(Orders.OrderStatus orderStatus);

        List<Orders> findByPaymentStatus(Orders.PaymentStatus paymentStatus);

        @Query("SELECT COUNT(o) > 0 FROM Orders o WHERE o.id = :orderId AND o.customer.id = :customerId")
        boolean existsByIdAndCustomerId(@Param("orderId") String orderId, @Param("customerId") String customerId);

        @Query("SELECT oi FROM OrderItem oi " +
                        "WHERE oi.order.orderId = :orderId " +
                        "AND oi.productId = :productId " +
                        "AND oi.order.customerId = :customerId")
        Optional<OrderItem> findOrderItemForReturn(
                        @Param("orderId") String orderId,
                        @Param("productId") String productId,
                        @Param("customerId") String customerId);

        @Query("SELECT oi FROM OrderItem oi JOIN oi.order o WHERE oi.orderItemId = :itemId AND o.customerId = :userId")
        Optional<OrderItem> findOrderItemByItemIdAndUserId(
                        @Param("itemId") String orderItemId,
                        @Param("userId") String userId);

        Optional<Orders> findByOrderIdAndCustomerId(String orderId, String customerId);

        @Query("SELECT o FROM Orders o LEFT JOIN FETCH o.customer WHERE o.orderId = :orderId")
        Optional<Orders> findByIdWithCustomer(@Param("orderId") String orderId);

}