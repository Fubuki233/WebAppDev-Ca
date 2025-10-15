/**
 * @author Jiang
 * @date 2025-10-07
 * @version 1.0
 */

package sg.com.aori.repository;

import sg.com.aori.model.Product;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InventoryRepository extends JpaRepository<Product, String> {

    List<Product> findByProductId(String productId);

    // Find products with sufficient stock
    List<Product> findByStockQuantityGreaterThan(Integer quantity);

    // Update stock quantity
    @Modifying
    @Query("UPDATE Product p SET p.stockQuantity = p.stockQuantity - :quantity WHERE p.id = :productId AND p.stockQuantity >= :quantity")
    int decreaseStockQuantity(@Param("productId") String productId, @Param("quantity") Integer quantity);

    // Restore stock quantity
    @Modifying
    @Query("UPDATE Product p SET p.stockQuantity = p.stockQuantity + :quantity WHERE p.id = :productId")
    int increaseStockQuantity(@Param("productId") String productId, @Param("quantity") Integer quantity);

    // Check inventory availability
    @Query("SELECT p.stockQuantity >= :requiredQuantity FROM Product p WHERE p.id = :productId")
    boolean isInventoryAvailable(@Param("productId") String productId, @Param("requiredQuantity") Integer requiredQuantity);

}