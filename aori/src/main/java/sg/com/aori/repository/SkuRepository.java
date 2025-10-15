package sg.com.aori.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import sg.com.aori.model.Sku;

/**
 * Repository for SKU operations.
 * 
 * @author Yunhe
 * @date 2025-10-13
 * @version 1.0
 * 
 * @author Yunhe
 * @date 2025-10-15
 * @version 1.1 - Added method to find SKUs by product code prefix and calculate
 *          total quantity
 */
@Repository
public interface SkuRepository extends JpaRepository<Sku, String> {

    /**
     * Find all SKUs that start with the given product code
     * SKU format: PRODUCTCODE&COLOR&SIZE
     * 
     * @param productCode the product code to search for
     * @return list of SKUs matching the product code
     */
    List<Sku> findBySkuStartingWith(String productCode);

    /**
     * Calculate the total quantity of all SKUs for a given product code
     * 
     * @param productCode the product code
     * @return the sum of quantities for all SKUs of this product
     */
    @Query("SELECT COALESCE(SUM(s.quantity), 0) FROM Sku s WHERE s.sku LIKE CONCAT(:productCode, '&%')")
    Integer getTotalQuantityByProductCode(@Param("productCode") String productCode);

}
