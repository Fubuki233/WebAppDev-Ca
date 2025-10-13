/* @author Derek
 * @date 2025-10-08
 * @version 1.0
 **/

package sg.com.aori.service;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import sg.com.aori.interfaces.IProductReview;
import sg.com.aori.model.OrderItem;
import sg.com.aori.model.Orders;
import sg.com.aori.model.ProductReview;
import sg.com.aori.model.ProductReview.ReviewStatus;
import sg.com.aori.repository.OrderItemRepository;
import sg.com.aori.repository.OrderRepository;
import sg.com.aori.repository.ProductReviewRepository;

@Service
@Transactional
public class ProductReviewService2 implements IProductReview {

    private final OrderRepository ordersRepository;
    private final OrderItemRepository orderItemRepository;
    private final ProductReviewRepository productReviewRepository;

    public ProductReviewService2(OrderRepository ordersRepository,
                                 OrderItemRepository orderItemRepository,
                                 ProductReviewRepository reviewRepository) {
        this.ordersRepository = ordersRepository;
        this.orderItemRepository = orderItemRepository;
        this.productReviewRepository = reviewRepository;
    }

    @Override
    public Map<String, Object> createOrUpdateReview(
            String customerId,
            String orderId,
            String orderItemId,
            Integer rating,
            String title,
            String comment,
            String imagesJson,
            String variantId // kept to match interface; ignored
    ) {
        // 1) Order must belong to customer
        Orders order = ordersRepository.findByOrderIdAndCustomerId(orderId, customerId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Order not found or does not belong to customer"));

        // 2) Must be Delivered (only for write)
        if (order.getOrderStatus() != Orders.OrderStatus.Delivered) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Review allowed only after delivery");
        }

        // 3) Order item must belong to the order
        OrderItem oi = orderItemRepository.findByOrderItemIdAndOrderId(orderItemId, order.getOrderId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Order item not found in this order"));

        String productId = oi.getProductId();

        // 4) Validate rating/comment
        if (rating == null || rating < 1 || rating > 5) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Rating must be between 1 and 5");
        }
        if (comment == null || comment.trim().length() < 12 || comment.trim().length() > 500) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Comment length must be between 12 and 500 characters");
        }

        // 5) Upsert by (productId, userId)
        ProductReview review = productReviewRepository
                .findByProductIdAndUserId(productId, customerId)
                .orElseGet(ProductReview::new);

        boolean isUpdate = review.getReviewId() != null;

        review.setUserId(customerId);
        review.setProductId(productId);
        review.setRating(rating);
        review.setTitle(title);
        review.setComment(comment);
        if (imagesJson != null) review.setImages(imagesJson);
        if (!isUpdate) review.setStatus(ReviewStatus.Pending);

        ProductReview saved = productReviewRepository.save(review);
        return toResponseMap(saved, isUpdate);
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getOwnReviewForOrderItem(String customerId, String orderId, String orderItemId) {
        // Validate order ownership
        Orders order = ordersRepository.findByOrderIdAndCustomerId(orderId, customerId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Order not found or does not belong to customer"));

        // Validate order item belongs to this order
        OrderItem oi = orderItemRepository.findByOrderItemIdAndOrderId(orderItemId, order.getOrderId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Order item not found in this order"));

        String productId = oi.getProductId();

        Optional<ProductReview> maybe = productReviewRepository.findByProductIdAndUserId(productId, customerId);
        return maybe.<Map<String, Object>>map(pr -> toResponseMap(pr, true)).orElseGet(Map::of);
    }

    private Map<String, Object> toResponseMap(ProductReview pr, boolean updated) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("reviewId", pr.getReviewId());
        m.put("productId", pr.getProductId());
        m.put("userId", pr.getUserId());
        m.put("rating", pr.getRating());
        m.put("title", pr.getTitle());
        m.put("comment", pr.getComment());
        m.put("imagesJson", pr.getImages());
        m.put("status", pr.getStatus());
        m.put("createdAt", pr.getCreatedAt());
        m.put("updatedAt", pr.getUpdatedAt());
        m.put("updated", updated);
        return m;
    }
}