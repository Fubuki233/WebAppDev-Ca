package sg.com.aori.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import sg.com.aori.interfaces.IProduct;
import sg.com.aori.repository.OrderItemRepository;
import sg.com.aori.repository.ProductRepository;
import sg.com.aori.model.Product;

/**
 * Service class for CRUD products, merged all in one.
 *
 * @author Yunhe
 * @date 2025-10-07
 * @version 1.0
 * 
 * @author Ying Chun
 * @date 2025-10-10 (v2.0)
 * @version 2.0 - Refactored to use OrderItemRepository to prevent deletion of
 *          products that have orders.
 */

@Service
public class CRUDProductService implements IProduct {
    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Override
    public Product createProduct(Product product) {
        return productRepository.save(product);
    }

    @Override
    public Optional<Product> getProductById(String productId) {
        return productRepository.findById(productId);
    }

    @Override
    public Optional<List<Product>> getProductsByName(String productName) {
        return Optional.ofNullable(productRepository.findByProductName(productName));
    }

    @Override
    public Optional<List<Product>> getProductsByCategory(String category) {
        return Optional.ofNullable(productRepository.findByCategoryId(category));
    }

    /**
     * Get products by category slug or categoryId
     * First tries to find by slug, then by categoryId
     */
    public Optional<List<Product>> getProductsByCategorySlugOrId(String categoryIdentifier) {
        // Try to find by slug first
        List<Product> products = productRepository.findProductsByCategorySlug(categoryIdentifier);

        // If not found by slug, try by categoryId
        if (products == null || products.isEmpty()) {
            products = productRepository.findByCategoryId(categoryIdentifier);
        }

        return Optional.ofNullable(products);
    }

    /*
     * @Override
     * public Optional<List<Product>> getProductsByPriceRange(double minPrice,
     * double maxPrice) {
     * return Optional.ofNullable(productRepository.findByPriceBetween(minPrice,
     * maxPrice));
     * }
     * 
     * @Override
     * public Optional<List<Product>> findByRatingBetween(double minRating, double
     * maxRating) {
     * return Optional.ofNullable(productRepository.findByRatingBetween(minRating,
     * maxRating));
     */

    @Override
    public Optional<List<Product>> getProductsByCollection(String collection) {
        return Optional.ofNullable(productRepository.findByCollection(collection));
    }

    /*
     * @Override
     * public Optional<List<Product>> getProductsBySupplier(String supplier) {
     * return Optional.ofNullable(productRepository.findBySupplier(supplier));
     * }
     */
    @Override
    public Optional<List<Product>> getAllProducts() {
        return Optional.ofNullable(productRepository.findAll());
    }

    @Override
    public Product updateProduct(String productId, Product product) {
        product.setProductId(productId);
        return productRepository.save(product);
    }

    /*
     * Commented out old delete method
     * 
     * @Override
     * public Product deleteProduct(String productId) {
     * Product product = productRepository.findById(productId).orElse(null);
     * if (product != null) {
     * productRepository.deleteById(productId);
     * }
     * return product;
     * }
     */

    // Revised method to prevent deletion of products that have orders
    @Override
    public Product deleteProduct(String productId) {
        // 1. Find the product first. This also handles the "not found" case.
        Product productToDelete = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Delete failed: Product not found with id: " + productId));

        // 2. Check if there are any orders for this product.
        long orderCount = orderItemRepository.countByProductId(productId);
        if (orderCount > 0) {
            // 3. If orders exist, throw a specific error.
            throw new IllegalStateException(
                    "Product cannot be deleted because it is part of " + orderCount + " existing order(s).");
        }

        // 4. If all checks pass, delete the product.
        productRepository.delete(productToDelete);

        // 5. Return the full object that was just deleted.
        return productToDelete;
    }
}
