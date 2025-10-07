package sg.com.aori.service;

/**
 * Detailed description of the class.
 *
 * @author Simon Lei
 * @date 2025-10-07
 * @version 1.0
 */

import java.time.LocalDateTime;
import java.util.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import sg.com.aori.model.ProductReview;
import sg.com.aori.model.ProductReview.ReviewStatus;
import sg.com.aori.repository.ProductReviewRepository;

@Service
@RequiredArgsConstructor
public class ProductReviewService {

    private final ProductReviewRepository reviewRepo;

    @Transactional
    public ProductReview createReview(String productId, String userId, int rating, String title, String comment) {
        reviewRepo.findByProductIdAndUserIdAndVariantId(productId, userId, null)
                .ifPresent(r -> {
                    throw new IllegalStateException("You have already reviewed this product.");
                });

        ProductReview review = new ProductReview();
        review.setReviewId(UUID.randomUUID().toString());
        review.setProductId(productId);
        review.setUserId(userId);
        review.setRating(rating);
        review.setTitle(title);
        review.setComment(comment);
        review.setStatus(ReviewStatus.Pending);
        review.setCreatedAt(LocalDateTime.now());
        review.setUpdatedAt(LocalDateTime.now());

        return reviewRepo.save(review);
    }

    @Transactional(readOnly = true)
    public Page<ProductReview> listApprovedReviews(String productId, int page, int size) {
        return reviewRepo.findByProductIdAndStatusOrderByCreatedAtDesc(
                productId, ReviewStatus.Approved, PageRequest.of(page, size));
    }

    @Transactional(readOnly = true)
    public double getAverageRating(String productId) {
        return reviewRepo.avgRating(productId, ReviewStatus.Approved);
    }

    @Transactional(readOnly = true)
    public Map<Integer, Long> getRatingBuckets(String productId) {
        Map<Integer, Long> buckets = new HashMap<>();
        reviewRepo.ratingBuckets(productId, ReviewStatus.Approved)
                .forEach(obj -> buckets.put((Integer) obj[0], (Long) obj[1]));
        for (int i = 1; i <= 5; i++) buckets.putIfAbsent(i, 0L);
        return buckets;
    }

    @Transactional
    public ProductReview moderate(String reviewId, ReviewStatus status) {
        ProductReview review = reviewRepo.findById(reviewId)
                .orElseThrow(() -> new NoSuchElementException("Review not found"));
        review.setStatus(status);
        review.setUpdatedAt(LocalDateTime.now());
        return reviewRepo.save(review);
    }

    @Transactional
    public void delete(String reviewId) {
        if (!reviewRepo.existsById(reviewId)) {
            throw new NoSuchElementException("Review not found");
        }
        reviewRepo.deleteById(reviewId);
    }
}
