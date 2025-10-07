package sg.com.aori.repository;

/**
 * Detailed description of the class.
 *
 * @author Simon Lei
 * @date 2025-10-07
 * @version 1.0
 */
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import sg.com.aori.model.ProductReview;

public interface ProductReviewRepository extends JpaRepository<ProductReview, String> {

  Page<ProductReview> findByProductIdAndStatusOrderByCreatedAtDesc(
      String productId, ProductReview.ReviewStatus status, Pageable pageable);

  long countByProductIdAndStatus(String productId, ProductReview.ReviewStatus status);

  @Query("select coalesce(avg(r.rating), 0) from ProductReview r where r.productId = :productId and r.status = :status")
  double avgRating(@Param("productId") String productId, @Param("status") ProductReview.ReviewStatus status);

  @Query("select r.rating as rating, count(r) as cnt from ProductReview r " +
         "where r.productId = :productId and r.status = :status group by r.rating")
  List<Object[]> ratingBuckets(@Param("productId") String productId, @Param("status") ProductReview.ReviewStatus status);

  Optional<ProductReview> findByProductIdAndUserIdAndVariantId(String productId, String userId, String variantId);
}

