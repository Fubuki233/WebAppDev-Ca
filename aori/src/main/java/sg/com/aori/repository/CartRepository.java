package sg.com.aori.repository;

import java.util.*;

import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import sg.com.aori.model.ShoppingCart;

/**
 * @author Jiang
 * @version 1.0
 * @version 1.1 - Modified variant related code
 * @version 1.2 - Optimized import
 * 
 * @date 2025-10-15
 * @version 1.3 - Added findByCustomerIdAndProductIdAndSku query method
 */

@Repository
public interface CartRepository extends JpaRepository<ShoppingCart, String> {

    List<ShoppingCart> findByCustomerId(String customerId);

    Optional<ShoppingCart> findByCustomerIdAndProductId(String customerId, String productId);

    Optional<ShoppingCart> findByCustomerIdAndProductIdAndSku(String customerId, String productId, String sku);

    @Modifying
    @Query("DELETE FROM ShoppingCart c WHERE c.customer.id = :customerId")
    void deleteByCustomerId(@Param("customerId") String customerId);

    @Query("SELECT COUNT(c) > 0 FROM ShoppingCart c WHERE c.customer.id = :customerId AND c.product.id = :productId")
    boolean existsByCustomerIdAndProductId(@Param("customerId") String customerId,
            @Param("productId") String productId);
}