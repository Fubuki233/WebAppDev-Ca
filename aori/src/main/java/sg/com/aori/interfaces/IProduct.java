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
public class IProduct {

    public interface ICreateProduct {
        Product createProduct(Product product);
    }

    public interface IReadProduct {
        Optional<Product> getProductById(String productId);

        Optional<List<Product>> getProductsByName(String productName);

        Optional<List<Product>> getProductsByCategory(String category);

        // Optional<List<Product>> getProductsByPriceRange(double minPrice, double
        // maxPrice);

        // Optional<List<Product>> findByRatingBetween(double minRating, double
        // maxRating);

        Optional<List<Product>> getProductsByBrand(String brand);

        // Optional<List<Product>> getProductsBySupplier(String supplier);

        Optional<List<Product>> getAllProducts();
    }

    public interface IUpdateProduct {
        Product updateProduct(String productId, Product product);
    }

    public interface IDeleteProduct {
        Product deleteProduct(String productId);
    }
}
