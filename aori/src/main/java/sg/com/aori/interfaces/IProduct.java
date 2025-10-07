package sg.com.aori.interfaces;

import sg.com.aori.model.Product;

import java.util.List;
import java.util.Optional;

/**
 * Interface for product-related operations.
 *
 * @author Yunhe
 * @date 2025-10-07
 * @version 1.0
 */
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
}
