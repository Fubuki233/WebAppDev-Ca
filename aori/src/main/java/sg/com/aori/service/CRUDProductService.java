package sg.com.aori.service;

import java.util.*;

import org.springframework.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import jakarta.persistence.criteria.Predicate;
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
 * @date 2025-10-10
 * @version 2.0 - Refactored to use OrderItemRepository to prevent deletion of
 *          products that have orders.
 * 
 * @author Yunhe
 * @date 2025-10-13
 * @version 2.1 - Added recalculateAverageRating method
 * 
 * @author Jiang
 * @date 2025-10-16
 * @version 2.2 - Added findProductIdByProductCode method
 */

@Service
public class CRUDProductService implements IProduct {
    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private ProductReviewService productReviewService;

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
        List<Product> products = productRepository.findProductsByCategorySlug(categoryIdentifier);

        if (products == null || products.isEmpty()) {
            products = productRepository.findByCategoryId(categoryIdentifier);
        }

        return Optional.ofNullable(products);
    }

    @Override
    public Optional<List<Product>> getProductsByCollection(String collection) {
        return Optional.ofNullable(productRepository.findByCollection(collection));
    }

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
    public void saveProduct(Product product) {
        productRepository.save(product);
    }

    @Override
    public Product deleteProduct(String productId) {
        Product productToDelete = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Delete failed: Product not found with id: " + productId));

        long orderCount = orderItemRepository.countByProductId(productId);
        if (orderCount > 0) {
            throw new IllegalStateException(
                    "Product cannot be deleted because it is part of " + orderCount + " existing order(s).");
        }

        productRepository.delete(productToDelete);

        return productToDelete;
    }

    /*
     * New pagination method with dynamic filtering
     */
    @Override
    public Page<Product> findPaginated(int page, int size, String keyword, String category, String season,
            String collection) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("productName").ascending());

        Specification<Product> spec = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (StringUtils.hasText(keyword)) {
                Predicate nameLike = criteriaBuilder.like(criteriaBuilder.lower(root.get("productName")),
                        "%" + keyword.toLowerCase() + "%");
                Predicate codeLike = criteriaBuilder.like(criteriaBuilder.lower(root.get("productCode")),
                        "%" + keyword.toLowerCase() + "%");
                predicates.add(criteriaBuilder.or(nameLike, codeLike));
            }

            if (StringUtils.hasText(category)) {
                predicates.add(criteriaBuilder.equal(root.join("category").get("categoryName"), category));
            }

            if (StringUtils.hasText(season)) {
                try {
                    Product.Season seasonEnum = Product.Season.valueOf(season);
                    predicates.add(criteriaBuilder.equal(root.get("season"), seasonEnum));
                } catch (IllegalArgumentException e) {
                }
            }

            if (StringUtils.hasText(collection)) {
                predicates.add(criteriaBuilder.equal(root.get("collection"), collection));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };

        return productRepository.findAll(spec, pageable);
    }

    @Override
    public String findProductIdByProductCode(String productCode) {
        String productId = productRepository.findProductIdByProductCode(productCode);
        return productId;
    }

}
