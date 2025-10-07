package sg.com.aori.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import sg.com.aori.interfaces.IProduct;
import sg.com.aori.repository.ProductRepository;
import sg.com.aori.model.Product;

/**
 * Service class for CRUD products, merged all in one.
 *
 * @author Yunhe
 * @date 2025-10-07
 * @version 1.0
 */

@Service
public class CRUDProductService implements IProduct {
    @Autowired
    private ProductRepository productRepository;

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

    @Override
    public Product deleteProduct(String productId) {
        Product product = productRepository.findById(productId).orElse(null);
        if (product != null) {
            productRepository.deleteById(productId);
        }
        return product;
    }
}
