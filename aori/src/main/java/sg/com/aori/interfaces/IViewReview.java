package sg.com.aori.interfaces;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import sg.com.aori.model.ProductReview;

/**
 * Interface for viewing product reviews.
 *
 * @author Lei Nuozhen
 * @date 2025-10-16
 * @version 2.0
 */

public interface IViewReview {

    List<ProductReview> listApprovedReviews(String productId);

    BigDecimal getAverageRating(String productId);

    Map<Integer, Long> getRatingBuckets(String productId);
}
