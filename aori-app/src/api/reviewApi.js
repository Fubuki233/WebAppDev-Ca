/**
 * Review API module for managing product reviews
 * 
 * @author Yunhe
 * @date 2025-10-14
 * @version 1.0
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
 */
export const getProductReviews = async (productId, page = 0, size = 10) => {
    try {
        const response = await fetch(
            `${API_CONFIG.BASE_URL}/public/products/${productId}/reviews?page=${page}&size=${size}`,
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
        console.error('Error fetching product reviews:', error);
        return {
            content: [],
            totalElements: 0,
            totalPages: 0,
            number: 0
        };
    }
};

/**
 * Get user's own review for a specific order item
 */
export const getOwnReview = async (customerId, orderId, orderItemId) => {
    try {
        const params = new URLSearchParams({
            customerId,
            orderId,
            orderItemId
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
