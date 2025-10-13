package sg.com.aori.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import sg.com.aori.model.OrderItem;

/**
 * purchasehistory
 *
 * @author Jiayi
 * @date 2025-10-08
 * @version 1.0
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
     * METHOD: Counts how many order items are associated with a specific product ID.
     * This is used in the CRUDProductService to prevent deletion of products that
     * have orders.
     * @author Ying Chun
     * @date 2025-10-10
     *
     * @param productId The ID of the product to check.
     * @return The number of order items linked to the product.
     */
    long countByProductId(String productId);

    /**
     * METHOD: For Product Review
     * @author Derek
     * @date 2025-10-08
     * @version 1.0
     */
    Optional<OrderItem> findByOrderItemIdAndOrderId(String orderItemId, String orderId);

    /**
     * @author Jiang
     * @date 2025-10-10
     */

    List<OrderItem> findByOrderId(String orderId);

    List<OrderItem> findByProductId(String productId);

    long countByOrderId(String orderId);

    @Query("SELECT oi FROM OrderItem oi JOIN FETCH oi.product WHERE oi.orderId = :orderId")
    List<OrderItem> findByOrderIdWithProduct(@Param("orderId") String orderId);

    boolean existsByOrderId(String orderId);
}
