package sg.com.aori.repository;

import java.util.List;

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
     * 根据订单 ID 查询订单项并加载商品详情
     * @param orderIds 订单 ID 列表
     * @return 订单项列表，包括关联的商品信息
     */
    @Query("SELECT oi FROM OrderItem oi " +
           "JOIN FETCH oi.product p " +
           "WHERE oi.orderId IN :orderIds")
    List<OrderItem> findOrderItemsWithProductDetails(@Param("orderIds") List<String> orderIds);

    /**
     * METHOD: Counts how many order items are associated with a specific product ID.
     * This is used in the CRUDProductService to prevent deletion of products that have orders.
     *
     * @param productId The ID of the product to check.
     * @return The number of order items linked to the product.
     */
    long countByProductId(String productId);

}
