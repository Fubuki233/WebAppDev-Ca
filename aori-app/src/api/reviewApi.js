/**
 * Review API module for managing product reviews
 * 
 * @author Yunhe
 * @date 2025-10-15
 * @version 1.1 - Added getOrderReviewStatus API
 */
import API_CONFIG, { API_ENDPOINTS } from '../config/apiConfig';
import { apiRequest } from './apiUtils';

/**
 * Get review statistics for a product (public)
 */
export const getReviewStats = async (productId) => {
    try {
        const response = await fetch(
            `${API_CONFIG.BASE_URL}/public/products/${productId}/reviews/stats`,
            {
                method: 'GET',
                headers: {
                    'Content-Type': 'application/json',
                },
            }
        );

        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }

        const data = await response.json();
        return data;
    } catch (error) {
        console.error('Error fetching review stats:', error);
        return {
            averageRating: 0,
            totalReviews: 0,
            distribution: {}
        };
    }
};

/**
 * Get approved reviews for a product (public)
 * Returns a simple array of review objects
 */
export const getProductReviews = async (productId, page = 0, size = 10) => {
    try {
        const response = await fetch(
            `${API_CONFIG.BASE_URL}/public/products/${productId}/reviews`,
            {
                method: 'GET',
                headers: {
                    'Content-Type': 'application/json',
                },
            }
        );

        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }

        const data = await response.json();
        // Backend now returns a simple array, wrap it in pagination-like structure for compatibility
        return {
            content: data || [],
            totalElements: data ? data.length : 0,
            totalPages: 1,
            number: 0,
            last: true
        };
    } catch (error) {
        console.error('Error fetching product reviews:', error);
        return {
            content: [],
            totalElements: 0,
            totalPages: 0,
            number: 0,
            last: true
        };
    }
};

/**
 * Get user's own review for a specific product in an order
 */
export const getOwnReview = async (customerId, orderId, productId) => {
    try {
        const params = new URLSearchParams({
            customerId,
            orderId,
            productId
        });

        const response = await fetch(
            `${API_CONFIG.BASE_URL}/review?${params.toString()}`,
            {
                method: 'GET',
                headers: {
                    'Content-Type': 'application/json',
                },
                credentials: 'include',
            }
        );

        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }

        const data = await response.json();
        return data;
    } catch (error) {
        console.error('Error fetching own review:', error);
        return null;
    }
};

/**
 * Get review status for all items in an order
 * Returns which items have been reviewed and which haven't
 */
export const getOrderReviewStatus = async (customerId, orderId) => {
    try {
        const params = new URLSearchParams({
            customerId,
            orderId
        });

        const response = await fetch(
            `${API_CONFIG.BASE_URL}/review/order-status?${params.toString()}`,
            {
                method: 'GET',
                headers: {
                    'Content-Type': 'application/json',
                },
                credentials: 'include',
            }
        );

        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }

        const data = await response.json();
        return {
            success: true,
            data
        };
    } catch (error) {
        console.error('Error fetching order review status:', error);
        return {
            success: false,
            reviewedProducts: [],
            allReviewed: false
        };
    }
};

/**
 * Create or update a review for an order item
 */
export const submitReview = async (customerId, orderId, productId, reviewData) => {
    try {
        const params = new URLSearchParams({
            customerId,
            orderId,
            productId
        });

        const body = {
            rating: reviewData.rating,
            title: reviewData.title || '',
            comment: reviewData.comment || '',
            imagesJson: reviewData.images ? JSON.stringify(reviewData.images) : null
        };

        const response = await fetch(
            `${API_CONFIG.BASE_URL}/review?${params.toString()}`,
            {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                credentials: 'include',
                body: JSON.stringify(body)
            }
        );

        if (!response.ok) {
            const errorData = await response.json().catch(() => ({}));
            throw new Error(errorData.message || `HTTP error! status: ${response.status}`);
        }

        const data = await response.json();
        return {
            success: true,
            data
        };
    } catch (error) {
        console.error('Error submitting review:', error);
        return {
            success: false,
            message: error.message || 'Failed to submit review'
        };
    }
};
