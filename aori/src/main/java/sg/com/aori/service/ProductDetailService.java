package sg.com.aori.service;

import java.util.Map;
import java.util.NoSuchElementException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import sg.com.aori.model.Product;
import sg.com.aori.service.ProductDetailVM;
import sg.com.aori.repository.ProductRepository;

/**
 * Service for fetching detailed product information along with review statistics.
 *
 * @author Simon Lei
 * @date 2025-10-08
 * @version 1.0
 */
@Service
@RequiredArgsConstructor
public class ProductDetailService {

    private final ProductRepository productRepo;
    private final ProductReviewService reviewService;

    public ProductDetailVM getDetail(String productId) {
        Product p = productRepo.findById(productId)
                .orElseThrow(() -> new NoSuchElementException("Product not found"));

        double avg = reviewService.getAverageRating(productId);
        Map<Integer, Long> buckets = reviewService.getRatingBuckets(productId);

        return ProductDetailVM.of(p, avg, buckets);
    }
}
