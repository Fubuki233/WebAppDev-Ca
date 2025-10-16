package sg.com.aori.interfaces;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.repository.query.Param;

import sg.com.aori.model.Product;

/**
 * Interface for product-related operations.
 *
 * @author Yunhe
 * @date 2025-10-07
 * @version 1.0
 * 
 * @author Ying Chun
 * @date 2025-10-13
 * @version 1.1 - Added pagination method
 */

public interface IProduct {
    Product createProduct(Product product);

    Optional<Product> getProductById(String productId);

    Optional<List<Product>> getProductsByName(String productName);

    Optional<List<Product>> getProductsByCategory(String category);

    Optional<List<Product>> getProductsByCollection(String collection);

    Optional<List<Product>> getAllProducts();

    Product updateProduct(String productId, Product product);

    Product deleteProduct(String productId);

    Page<Product> findPaginated(int page, int size, String keyword, String category, String season,
            String collection);

    Product saveProduct(Product product);

    String findProductIdByProductCode(@Param("productCode") String productCode);
}
