/* @author Derek
 * @date 2025-10-15
 * @version 1.2 - Added getOrderReviewStatus endpoint
 *
 * Combined controller for:
 * - Authenticated customer review operations (create/update/read own)
 * - Public product review viewing (approved reviews only)
 * - Order review status tracking
 * 
 * 
 */

package sg.com.aori.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import sg.com.aori.interfaces.IProductReview;
import sg.com.aori.model.Customer;
import sg.com.aori.model.Product;
import sg.com.aori.model.ProductReview;
import sg.com.aori.repository.CustomerRepository;
import sg.com.aori.repository.ProductReviewRepository;
import sg.com.aori.service.CRUDProductService;
import sg.com.aori.service.ProductReviewService;

@RestController
@RequestMapping("/api")
public class ProductReviewController {

    private final IProductReview productReviewService;
    private final ProductReviewRepository productReviewRepository;
    @Autowired
    private CRUDProductService productservice;
    @Autowired
    private ProductReviewService productReviewService_;
    @Autowired
    private CustomerRepository customerRepository;

    public ProductReviewController(IProductReview productReviewService,
            ProductReviewRepository productReviewRepository) {
        this.productReviewService = productReviewService;
        this.productReviewRepository = productReviewRepository;
    }

    // =======================================================================
    // CUSTOMER SECTION (Authenticated endpoints)
    // =======================================================================

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

        Optional<Product> product = productservice.getProductById(productId);
        if (!product.isPresent()) {
            throw new RuntimeException("Product not found with id: " + productId);
        }

        product.get().setRating((float) productReviewService_.getAverageRating(productId));
        productservice.saveProduct(product.get());
        System.out.println("[ProductReviewController] Product rating updated to: " + product.get().getRating());

        return productReviewService.createOrUpdateReview(customerId, orderId, productId, rating, title, comment);

    }

    /**
     * Get the caller's own review for the product tied to this order item (if any).
     */
    @GetMapping("review")
    public Map<String, Object> getOwnReview(
            @RequestParam String customerId,
            @RequestParam String orderId,
            @RequestParam String productId) {
        return productReviewService.getOwnReviewForOrderItem(customerId, orderId, productId);
    }

    /**
     * Get review status for all items in an order.
     * Returns which products have been reviewed and whether all items are reviewed.
     */
    @GetMapping("review/order-status")
    @ResponseStatus(HttpStatus.OK)
    public Map<String, Object> getOrderReviewStatus(
            @RequestParam String customerId,
            @RequestParam String orderId) {
        return productReviewService.getOrderReviewStatus(customerId, orderId);
    }

    // =======================================================================
    // PUBLIC SECTION (Open access for reading reviews)
    // =======================================================================

    /** List all APPROVED reviews for a product (publicly visible). */
    @GetMapping("/public/products/{productId}/reviews")
    @ResponseStatus(HttpStatus.OK)
    public List<Map<String, Object>> listApprovedReviews(
            @PathVariable String productId) {
        List<ProductReview> reviewPage = productReviewRepository
                .findByProductIdByCreatedAtDesc(productId);

        List<Map<String, Object>> result = new ArrayList<>();

        for (ProductReview review : reviewPage) {
            Map<String, Object> reviewData = new HashMap<>();
            reviewData.put("reviewId", review.getReviewId());
            reviewData.put("productId", review.getProductId());
            reviewData.put("rating", review.getRating());
            reviewData.put("title", review.getTitle());
            reviewData.put("comment", review.getComment());
            reviewData.put("createdAt", review.getCreatedAt());
            
            // Get customer name
            String customerName = "Anonymous";
            if (review.getUserId() != null) {
                Optional<Customer> customer = customerRepository.findById(review.getUserId());
                if (customer.isPresent()) {
                    Customer c = customer.get();
                    customerName = (c.getFirstName() != null ? c.getFirstName() : "") + 
                                 (c.getLastName() != null ? " " + c.getLastName() : "");
                    customerName = customerName.trim();
                    if (customerName.isEmpty()) {
                        customerName = "Anonymous";
                    }
                }
            }
            reviewData.put("customerName", customerName);
            
            result.add(reviewData);
        }

        return result;

    }

    // /** Get summary statistics for approved product reviews. */
    // @GetMapping("/public/products/{productId}/reviews/stats")
    // @ResponseStatus(HttpStatus.OK)
    // public Map<String, Object> getReviewStatistics(@PathVariable String
    // productId) {
    // double avgRating = productReviewRepository.avgRating(productId);
    // long count = productReviewRepository.countByProductId(productId);
    // List<Object[]> ratingBuckets =
    // productReviewRepository.ratingBuckets(productId);

    // Map<Integer, Long> bucketMap = new LinkedHashMap<>();
    // for (Object[] obj : ratingBuckets) {
    // Integer rating = (Integer) obj[0];
    // Long cnt = (Long) obj[1];
    // bucketMap.put(rating, cnt);
    // }

    // Map<String, Object> response = new LinkedHashMap<>();
    // response.put("averageRating", avgRating);
    // response.put("totalReviews", count);
    // response.put("distribution", bucketMap);
    // return response;
    // }
}