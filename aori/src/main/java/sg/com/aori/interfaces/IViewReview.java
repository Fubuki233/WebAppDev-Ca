package sg.com.aori.interfaces;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import sg.com.aori.model.ProductReview;

public interface IViewReview {

    List<ProductReview> listApprovedReviews(String productId);

    BigDecimal getAverageRating(String productId);

    Map<Integer, Long> getRatingBuckets(String productId);
}
