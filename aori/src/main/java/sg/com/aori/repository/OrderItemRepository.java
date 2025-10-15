package sg.com.aori.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import sg.com.aori.model.OrderItem;

/**
 * @author Jiayi, Derek, Jiang
 * @date 2025-10-8
 * @version 1.0
 * 
 * @author Derek
 * @date 2025-10-8
 * @version 1.1 - Added findByOrderItemIdAndOrderId
 * 
 * @author Jiang
 * @date 2025-10-10
 * @version 1.2 - Aded findByOrderId
 */

public interface OrderItemRepository extends JpaRepository<OrderItem, String> {

    /**
     * Fetches order items along with their associated product details for a list of
     * order IDs.
     * 
     * @param orderIds List of order IDs to fetch items for
     * @return List of order items including associated product information
     */
    @Query("SELECT oi FROM OrderItem oi " +
            "JOIN FETCH oi.product p " +
            "WHERE oi.orderId IN :orderIds")
    List<OrderItem> findOrderItemsWithProductDetails(@Param("orderIds") List<String> orderIds);

    /**
     * METHOD: Counts how many order items are associated with a specific product
     * ID.
     * This is used in the CRUDProductService to prevent deletion of products that
     * have orders.
     *
     * @param productId The ID of the product to check.
     * @return The number of order items linked to the product.
     */
    long countByProductId(String productId);

    // for product review
    Optional<OrderItem> findByOrderItemIdAndOrderId(String orderItemId, String orderId);

    List<OrderItem> findByOrderId(String orderId);

    List<OrderItem> findByProductId(String productId);

    long countByOrderId(String orderId);

    @Query("SELECT oi FROM OrderItem oi JOIN FETCH oi.product WHERE oi.orderId = :orderId")
    List<OrderItem> findByOrderIdWithProduct(@Param("orderId") String orderId);

    boolean existsByOrderId(String orderId);
}
