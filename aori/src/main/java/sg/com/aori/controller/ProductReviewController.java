/* @author Derek
 * @date 2025-10-10
 * @version 1.1
 *
 * Combined controller for:
 * - Authenticated customer review operations (create/update/read own)
 * - Public product review viewing (approved reviews only)
 */

package sg.com.aori.controller;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import sg.com.aori.interfaces.IProductReview;
import sg.com.aori.model.ProductReview;
import sg.com.aori.model.ProductReview.ReviewStatus;
import sg.com.aori.repository.ProductReviewRepository;

@RestController
@RequestMapping("/api")
public class ProductReviewController {

    private final IProductReview productReviewService;
    private final ProductReviewRepository productReviewRepository;

    public ProductReviewController(IProductReview productReviewService,
                                   ProductReviewRepository productReviewRepository) {
        this.productReviewService = productReviewService;
        this.productReviewRepository = productReviewRepository;
    }

    // =======================================================================
    // CUSTOMER SECTION (Authenticated endpoints)
    // =======================================================================

    /** Create or update a review for the order item (only if the order is Delivered). */
    @PostMapping("/customers/{customerId}/orders/{orderId}/items/{orderItemId}/review")
    @ResponseStatus(HttpStatus.OK)
    public Map<String, Object> createOrUpdateReview(
            @PathVariable String customerId,
            @PathVariable String orderId,
            @PathVariable String orderItemId,
            @RequestBody Map<String, Object> body
    ) {
        Integer rating   = body.get("rating")    == null ? null : ((Number) body.get("rating")).intValue();
        String  title    = body.get("title")     == null ? null : body.get("title").toString();
        String  comment  = body.get("comment")   == null ? null : body.get("comment").toString();
        String  images   = body.get("imagesJson")== null ? null : body.get("imagesJson").toString();

        return productReviewService.createOrUpdateReview(
                customerId, orderId, orderItemId, rating, title, comment, images, null
        );
    }

    /** Get the caller’s own review for the product tied to this order item (if any). */
    @GetMapping("/customers/{customerId}/orders/{orderId}/items/{orderItemId}/review")
    public Map<String, Object> getOwnReview(
            @PathVariable String customerId,
            @PathVariable String orderId,
            @PathVariable String orderItemId
    ) {
        return productReviewService.getOwnReviewForOrderItem(customerId, orderId, orderItemId);
    }

    // =======================================================================
    // PUBLIC SECTION (Open access for reading reviews)
    // =======================================================================

    /** List all APPROVED reviews for a product (publicly visible). */
    @GetMapping("/public/products/{productId}/reviews")
    @ResponseStatus(HttpStatus.OK)
    public Page<ProductReview> listApprovedReviews(
            @PathVariable String productId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return productReviewRepository.findByProductIdAndStatusOrderByCreatedAtDesc(
                productId, ReviewStatus.Approved, PageRequest.of(page, size)
        );
    }

    /** Get summary statistics for approved product reviews. */
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