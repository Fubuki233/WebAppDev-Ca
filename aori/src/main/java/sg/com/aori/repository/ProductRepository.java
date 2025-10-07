package sg.com.aori.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import sg.com.aori.model.Product;

/**
 * Repository interface for Product entity.
 *
 * @author Yunhe
 * @date 2025-10-07
 * @version 1.0
 */
@Repository
public interface ProductRepository extends JpaRepository<Product, String> {

    List<Product> findByProductName(String productName);

    List<Product> findByCategoryId(String categoryId);

    List<Product> findByBrand(String brand);

    List<Product> findBySeason(Product.Season season);

    List<Product> findByProductNameContaining(String keyword);

    List<Product> findByBrandAndSeason(String brand, Product.Season season);

    // List<Product> findByPriceBetween(double minPrice, double maxPrice);

    // List<Product> findByRatingBetween(double minRating, double maxRating);

    // List<Product> findBySupplier(String supplier);

    @Query("SELECT p FROM Product p JOIN p.category c WHERE c.categoryName = :categoryName")
    List<Product> findProductsByCategoryName(@Param("categoryName") String categoryName);

    @Modifying
    @Transactional
    @Query("UPDATE Product p SET p.brand = :brand WHERE p.productId = :productId")
    int updateProductBrand(@Param("productId") String productId, @Param("brand") String brand);

    @Query("SELECT COUNT(p) FROM Product p WHERE p.categoryId = :categoryId")
    long countByCategoryId(@Param("categoryId") String categoryId);
}
