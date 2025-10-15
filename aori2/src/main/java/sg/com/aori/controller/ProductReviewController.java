package sg.com.aori.controller;

import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import sg.com.aori.interfaces.IProductReview;
import sg.com.aori.model.Product;
import sg.com.aori.model.ProductReview;
import sg.com.aori.model.ProductReview.ReviewStatus;
import sg.com.aori.repository.ProductReviewRepository;
import sg.com.aori.service.CRUDProductService;
import sg.com.aori.service.ProductReviewService;

/**
 * Combined controller for:
 * - Authenticated customer review operations (create/update/read own)
 * - Public product review viewing (approved reviews only)
 * 
 * @author Derek
 * @date 2025-10-10
 * @version 1.1
 */

@RestController
@RequestMapping("/api")
public class ProductReviewController {

    private final IProductReview productReviewService;
    private final ProductReviewRepository productReviewRepository;
    @Autowired
    private CRUDProductService productservice;
    @Autowired
    private ProductReviewService productReviewService_;

    public ProductReviewController(IProductReview productReviewService,
            ProductReviewRepository productReviewRepository) {
        this.productReviewService = productReviewService;
        this.productReviewRepository = productReviewRepository;
    }

    /**
     * Create or update a review for the order item (only if the order is
     * Delivered).
     */
    @PostMapping("review")
    @ResponseStatus(HttpStatus.OK)
    public Map<String, Object> createOrUpdateReview(
            @RequestParam String customerId,
            @RequestParam String orderId,
            @RequestParam String productId,
            @RequestBody Map<String, Object> body) {
        Integer rating = body.get("rating") == null ? null : ((Number) body.get("rating")).intValue();
        String title = body.get("title") == null ? null : body.get("title").toString();
        String comment = body.get("comment") == null ? null : body.get("comment").toString();
        String images = body.get("imagesJson") == null ? null : body.get("imagesJson").toString();

        Optional<Product> product = productservice.getProductById(productId);
        if (!product.isPresent()) {
            throw new RuntimeException("Product not found with id: " + productId);
        }

        product.get().setRating((float) productReviewService_.getAverageRating(productId));
        productservice.saveProduct(product.get());
        System.out.println("[ProductReviewController] Product rating updated to: " + product.get().getRating());

        return productReviewService.createOrUpdateReview(customerId, orderId, productId, rating, title, comment,
                images, null);

    }

    /**
     * Get the caller’s own review for the product tied to this order item (if any).
     */
    @GetMapping("review")
    public Map<String, Object> getOwnReview(
            @RequestParam String customerId,
            @RequestParam String orderId,
            @RequestParam String productId) {
        return productReviewService.getOwnReviewForOrderItem(customerId, orderId, productId);
    }

    /**
     * List all APPROVED reviews for a product (publicly visible).
     */
    @GetMapping("/public/products/{productId}/reviews")
    @ResponseStatus(HttpStatus.OK)
    public Page<ProductReview> listApprovedReviews(
            @PathVariable String productId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return productReviewRepository.findByProductIdAndStatusOrderByCreatedAtDesc(
                productId, ReviewStatus.Approved, PageRequest.of(page, size));
    }

    /**
     * Get summary statistics for approved product reviews (publicly visible).
     */
    @GetMapping("/public/products/{productId}/reviews/stats")
    @ResponseStatus(HttpStatus.OK)
    public Map<String, Object> getReviewStatistics(@PathVariable String productId) {
        double avgRating = productReviewRepository.avgRating(productId, ReviewStatus.Approved);
        long count = productReviewRepository.countByProductIdAndStatus(productId, ReviewStatus.Approved);
        List<Object[]> ratingBuckets = productReviewRepository.ratingBuckets(productId, ReviewStatus.Approved);

        Map<Integer, Long> bucketMap = new LinkedHashMap<>();
        for (Object[] obj : ratingBuckets) {
            Integer rating = (Integer) obj[0];
            Long cnt = (Long) obj[1];
            bucketMap.put(rating, cnt);
        }

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("averageRating", avgRating);
        response.put("totalReviews", count);
        response.put("distribution", bucketMap);
        return response;
    }
}