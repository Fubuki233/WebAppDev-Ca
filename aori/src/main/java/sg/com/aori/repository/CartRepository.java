/**
 * @author Jiang
 * @date 2025-10-07
 * @version 1.0
 */

package sg.com.aori.repository;

import sg.com.aori.model.ShoppingCart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<ShoppingCart, String> {

    // Find all cart items by customer ID
    List<ShoppingCart> findByCustomerId(String customerId);

    // Find specific cart item by customer ID and product ID
    Optional<ShoppingCart> findByCustomerIdAndProductId(String customerId, String productId);

    // Delete all cart items for a customer
    @Modifying
    @Query("DELETE FROM ShoppingCart c WHERE c.customer.id = :customerId")
    void deleteByCustomerId(@Param("customerId") String customerId);

    // Verify if product is in cart
    @Query("SELECT COUNT(c) > 0 FROM ShoppingCart c WHERE c.customer.id = :customerId AND c.product.id = :productId")
    boolean existsByCustomerIdAndProductId(@Param("customerId") String customerId,
            @Param("productId") String productId);
}