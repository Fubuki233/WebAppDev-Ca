/**
 * @author Jiang
 * @date 2025-10-07
 * @version 1.0
 */

package sg.com.aori.repository;

import sg.com.aori.model.ProductVariant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InventoryRepository extends JpaRepository<ProductVariant, String> {

    // Find variant by SKU
    Optional<ProductVariant> findBySku(String sku);

    // Find variants by product ID
    List<ProductVariant> findByProductId(String productId);

    // Find variants with sufficient stock
    List<ProductVariant> findByStockQuantityGreaterThan(Integer quantity);

    // Update stock quantity
    @Modifying
    @Query("UPDATE ProductVariant pv SET pv.stockQuantity = pv.stockQuantity - :quantity WHERE pv.id = :variantId AND pv.stockQuantity >= :quantity")
    int decreaseStockQuantity(@Param("variantId") String variantId, @Param("quantity") Integer quantity);

    // Restore stock quantity
    @Modifying
    @Query("UPDATE ProductVariant pv SET pv.stockQuantity = pv.stockQuantity + :quantity WHERE pv.id = :variantId")
    int increaseStockQuantity(@Param("variantId") String variantId, @Param("quantity") Integer quantity);

    // Check inventory availability
    @Query("SELECT pv.stockQuantity >= :requiredQuantity FROM ProductVariant pv WHERE pv.id = :variantId")
    boolean isInventoryAvailable(@Param("variantId") String variantId, @Param("requiredQuantity") Integer requiredQuantity);
}