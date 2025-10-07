package sg.com.aori.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import sg.com.aori.interfaces.IProduct.IReadProduct;
import sg.com.aori.repository.ProductRepository;
import sg.com.aori.model.Product;

import java.util.List;
import java.util.Optional;;

/**
 * Service class for reading product-related information.
 *
 * @author Yunhe
 * @date 2025-10-07
 * @version 1.0
 */
@Service
public class ReadProductService implements IReadProduct {
    @Autowired
    private ProductRepository productRepository;

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
    public Optional<List<Product>> getProductsByBrand(String brand) {
        return Optional.ofNullable(productRepository.findByBrand(brand));
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

}
