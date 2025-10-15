/**
 * v1.1: Modified variant related code
 * v1.2: Optimized import
 * v1.3: Added findByCustomerIdAndProductIdAndSku query method
 * @author Jiang
 * @date 2025-10-15
 * @version 1.3
 */

package sg.com.aori.repository;

import sg.com.aori.model.ShoppingCart;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public interface CartRepository extends JpaRepository<ShoppingCart, String> {

    // Find all cart items by customer ID
    List<ShoppingCart> findByCustomerId(String customerId);

    // Find specific cart item by customer ID and product ID
    Optional<ShoppingCart> findByCustomerIdAndProductId(String customerId, String productId);

    // Find specific cart item by customer ID, product ID and SKU
    Optional<ShoppingCart> findByCustomerIdAndProductIdAndSku(String customerId, String productId, String sku);

    // Delete all cart items for a customer
    @Modifying
    @Query("DELETE FROM ShoppingCart c WHERE c.customer.id = :customerId")
    void deleteByCustomerId(@Param("customerId") String customerId);

    // Verify if product is in cart
    // @Query("SELECT COUNT(c) > 0 FROM ShoppingCart c WHERE c.customer.id =
    // :customerId AND c.variant.id = :variantId")
    // boolean existsByCustomerIdAndVariantId(@Param("customerId") String
    // customerId, @Param("variantId") String variantId);

    @Query("SELECT COUNT(c) > 0 FROM ShoppingCart c WHERE c.customer.id = :customerId AND c.product.id = :productId")
    boolean existsByCustomerIdAndProductId(@Param("customerId") String customerId,
            @Param("productId") String productId);
}