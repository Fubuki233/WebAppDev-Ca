/**
 * Interface for product-related operations.
 *
 * @author Yunhe & Ying chun
 * @date 2025-10-07 (v1.0), 2025-10-13 (v2.0)
 * @version 1.0
 * @version 1.1 - Added pagination method
 */

package sg.com.aori.interfaces;

import sg.com.aori.model.Product;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.repository.query.Param;


public interface IProduct {
    Product createProduct(Product product);

    Optional<Product> getProductById(String productId);

    Optional<List<Product>> getProductsByName(String productName);

    Optional<List<Product>> getProductsByCategory(String category);

    // Optional<List<Product>> getProductsByPriceRange(double minPrice, double
    // maxPrice);

    // Optional<List<Product>> findByRatingBetween(double minRating, double
    // maxRating);

    Optional<List<Product>> getProductsByCollection(String collection);

    // Optional<List<Product>> getProductsBySupplier(String supplier);

    Optional<List<Product>> getAllProducts();

    Product updateProduct(String productId, Product product);

    Product deleteProduct(String productId);

    Page<Product> findPaginated(int page, int size, String keyword, String category, String season,
            String collection);

    void saveProduct(Product product);

    String findProductIdByProductCode(@Param("productCode") String productCode);
}
