package sg.com.aori.repository;

/**
 * Detailed description of the class.
 *
 * @author Simon Lei
 * @date 2025-10-07
 * @version 1.0
 * @version 2.0 removed variant
 */
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import sg.com.aori.model.ProductReview;

public interface ProductReviewRepository extends JpaRepository<ProductReview, String> {

        @Query("SELECT r FROM ProductReview r LEFT JOIN FETCH r.user WHERE r.productId = :productId ORDER BY r.createdAt DESC")
        List<ProductReview> findByProductIdByCreatedAtDesc(
                        @Param("productId") String productId);

        long countByProductId(String productId);

        @Query("select coalesce(avg(r.rating), 0) from ProductReview r where r.productId = :productId")
        double avgRating(@Param("productId") String productId);

        @Query("select r.rating as rating, count(r) as cnt from ProductReview r where r.productId = :productId group by r.rating")
        List<Object[]> ratingBuckets(@Param("productId") String productId);

        Optional<ProductReview> findByProductIdAndUserId(String productId, String userId);
}
