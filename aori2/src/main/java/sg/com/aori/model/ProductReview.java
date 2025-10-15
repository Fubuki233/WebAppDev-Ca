package sg.com.aori.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Detailed description of the class.
 *
 * @author Simon Lei
 * @date 2025-10-07
 * @version 1.0
 */

@Entity
@Table(name = "ProductReview", indexes = {
        @Index(name = "idx_pr_product_status_created", columnList = "product_id, status, created_at"),
        @Index(name = "idx_pr_user", columnList = "user_id")
})
public class ProductReview {

    public enum ReviewStatus {
        Pending,
        Approved,
        Rejected,
        Flagged
    }

    @Id
    @Column(name = "review_id", length = 36, nullable = false)
    private String reviewId = UUID.randomUUID().toString();

    @Column(name = "product_id", length = 36, nullable = false)
    private String productId;

    @Column(name = "variant_id", length = 36)
    private String variantId;

    @Column(name = "user_id", length = 36, nullable = false)
    private String userId;

    @Column(name = "rating", nullable = false)
    private Integer rating;

    @Column(name = "title", length = 150)
    private String title;

    @Column(name = "comment", columnDefinition = "TEXT", nullable = false)
    private String comment;

    @Column(name = "images", columnDefinition = "JSON")
    private String imagesJson;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ReviewStatus status = ReviewStatus.Pending;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", insertable = false, updatable = false)
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "variant_id", insertable = false, updatable = false)
    private ProductVariant variant;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private Customer user;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public ProductReview() {
    }

    public ProductReview(String productId, String userId, Integer rating, String comment, ReviewStatus status) {
        this.productId = productId;
        this.userId = userId;
        this.rating = rating;
        this.comment = comment;
        this.status = status;
    }

    public String getReviewId() {
        return reviewId;
    }

    public void setReviewId(String reviewId) {
        this.reviewId = reviewId;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getVariantId() {
        return variantId;
    }

    public void setVariantId(String variantId) {
        this.variantId = variantId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getImages() {
        return imagesJson;
    }

    public void setImages(String imagesJson) {
        this.imagesJson = imagesJson;
    }

    public ReviewStatus getStatus() {
        return status;
    }

    public void setStatus(ReviewStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public ProductVariant getVariant() {
        return variant;
    }

    public void setVariant(ProductVariant variant) {
        this.variant = variant;
    }

    public Customer getUser() {
        return user;
    }

    public void setUser(Customer user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return "{" +
                "reviewId='" + reviewId + '\'' +
                ", productId='" + productId + '\'' +
                ", variantId='" + variantId + '\'' +
                ", userId='" + userId + '\'' +
                ", rating=" + rating +
                ", title='" + title + '\'' +
                ", comment='" + comment + '\'' +
                ", images='" + imagesJson + '\'' +
                ", status=" + status +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
