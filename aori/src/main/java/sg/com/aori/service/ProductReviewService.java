package sg.com.aori.service;

import java.util.*;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import sg.com.aori.interfaces.IProductReview;
import sg.com.aori.model.Orders;
import sg.com.aori.model.ProductReview;
import sg.com.aori.model.ProductReview.ReviewStatus;
import sg.com.aori.repository.*;

/**
 * @author Derek
 * @date 2025-10-15
 * @version 1.2 - Allow reviews for Shipped orders, added getOrderReviewStatus
 *          method
 */

@Service
@Transactional
public class ProductReviewService implements IProductReview {

        private final OrderRepository ordersRepository;
        private final ProductReviewRepository productReviewRepository;
        private final OrderItemRepository orderItemRepository;

        public ProductReviewService(OrderRepository ordersRepository,
                        ProductReviewRepository reviewRepository,
                        OrderItemRepository orderItemRepository) {
                this.ordersRepository = ordersRepository;
                this.productReviewRepository = reviewRepository;
                this.orderItemRepository = orderItemRepository;
        }

        @Override
        public Map<String, Object> createOrUpdateReview(
                        String customerId,
                        String orderId,
                        String productId,
                        Integer rating,
                        String title,
                        String comment) {
                Orders order = ordersRepository.findByOrderIdAndCustomerId(orderId, customerId)
                                .orElseThrow(() -> new ResponseStatusException(
                                                HttpStatus.NOT_FOUND,
                                                "Order not found or does not belong to customer"));

                if (order.getOrderStatus() != Orders.OrderStatus.Delivered
                                && order.getOrderStatus() != Orders.OrderStatus.Shipped) {
                        throw new ResponseStatusException(
                                        HttpStatus.BAD_REQUEST,
                                        "Review allowed only after order is shipped or delivered");
                }

                if (rating == null || rating < 1 || rating > 5) {
                        throw new ResponseStatusException(
                                        HttpStatus.BAD_REQUEST, "Rating must be between 1 and 5");
                }
                if (comment == null || comment.trim().length() < 12 || comment.trim().length() > 500) {
                        throw new ResponseStatusException(
                                        HttpStatus.BAD_REQUEST, "Comment length must be between 12 and 500 characters");
                }

                ProductReview review = productReviewRepository
                                .findByProductIdAndUserId(productId, customerId)
                                .orElseGet(ProductReview::new);

                boolean isUpdate = review.getReviewId() != null;

                review.setUserId(customerId);
                review.setProductId(productId);
                review.setRating(rating);
                review.setTitle(title);
                review.setComment(comment);
                if (!isUpdate)
                        review.setStatus(ReviewStatus.Pending);

                ProductReview saved = productReviewRepository.save(review);
                return toResponseMap(saved, isUpdate);
        }

        @Override
        @Transactional(readOnly = true)
        public Map<String, Object> getOwnReviewForOrderItem(String customerId, String orderId, String productId) {
                ordersRepository.findByOrderIdAndCustomerId(orderId, customerId)
                                .orElseThrow(() -> new ResponseStatusException(
                                                HttpStatus.NOT_FOUND,
                                                "Order not found or does not belong to customer"));

                Optional<ProductReview> maybe = productReviewRepository.findByProductIdAndUserId(productId, customerId);
                return maybe.<Map<String, Object>>map(pr -> toResponseMap(pr, true)).orElseGet(Map::of);
        }

        @Override
        @Transactional(readOnly = true)
        public Map<String, Object> getOrderReviewStatus(String customerId, String orderId) {
                java.util.List<sg.com.aori.model.OrderItem> orderItems = orderItemRepository.findByOrderId(orderId);

                java.util.List<Map<String, Object>> itemsStatus = new java.util.ArrayList<>();
                int reviewedCount = 0;

                for (sg.com.aori.model.OrderItem item : orderItems) {
                        Map<String, Object> itemStatus = new LinkedHashMap<>();
                        itemStatus.put("productId", item.getProductId());
                        itemStatus.put("orderItemId", item.getOrderItemId());

                        Optional<ProductReview> review = productReviewRepository
                                        .findByProductIdAndUserId(item.getProductId(), customerId);

                        if (review.isPresent()) {
                                itemStatus.put("reviewed", true);
                                itemStatus.put("review", toResponseMap(review.get(), true));
                                reviewedCount++;
                        } else {
                                itemStatus.put("reviewed", false);
                                itemStatus.put("review", null);
                        }

                        itemsStatus.add(itemStatus);
                }

                Map<String, Object> response = new LinkedHashMap<>();
                response.put("orderId", orderId);
                response.put("totalItems", orderItems.size());
                response.put("reviewedItems", reviewedCount);
                response.put("allReviewed", reviewedCount == orderItems.size());
                response.put("items", itemsStatus);

                return response;
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