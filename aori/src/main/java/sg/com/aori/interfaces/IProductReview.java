/* @author Derek
 * @date 2025-10-08
 * @version 1.0
 **/

package sg.com.aori.interfaces;

import java.util.Map;

public interface IProductReview {
        /**
         * Create or update a review for the given (customerId, orderId, productId).
         * Business rules:
         * - Order must belong to customer and be Delivered
         * - Upsert by (userId, productId)
         * - rating must be 1..5
         * - comment length 12..500 (DB constraint)
         */
        Map<String, Object> createOrUpdateReview(
                        String customerId,
                        String orderId,
                        String productId,
                        Integer rating,
                        String title,
                        String comment,
                        String imagesJson,
                        String variantId);

        /**
         * Get the existing review for this customer & the product (if any).
         */
        Map<String, Object> getOwnReviewForOrderItem(
                        String customerId,
                        String orderId,
                        String productId);
}