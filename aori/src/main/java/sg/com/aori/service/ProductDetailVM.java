package sg.com.aori.service;

import java.util.Map;
import sg.com.aori.model.Product;

// View Model for Product Details
// Combines product info with review stats
// @author Simon Lei
// @date 2025-10-08
// @version 1.0

public record ProductDetailVM(
    String productId,
    String productName,
    String description,
    String categoryId,
    String material,
    String season,
    String collection,
    double avgRating,
    Map<Integer, Long> ratingBuckets
) {
    public static ProductDetailVM of(Product p, double avg, Map<Integer, Long> buckets) {
        return new ProductDetailVM(
            p.getProductId(),
            p.getProductName(),
            p.getDescription(),
            p.getCategoryId(),
            p.getMaterial(),
            p.getSeason() != null ? p.getSeason().name() : null,
            p.getCollection(),
            Math.round(avg * 10) / 10.0,
            buckets
        );
    }
}
