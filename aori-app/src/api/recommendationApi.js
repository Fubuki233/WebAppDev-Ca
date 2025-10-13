/**
 * Product Recommendation API
 * Provides personalized product recommendations based on user purchase history
 * 
 * @author Yunhe
 * @date 2025-10-12
 * @version 1.0
 */

import API_CONFIG, { API_ENDPOINTS } from '../config/apiConfig';
import { getUserUuid } from './apiUtils';

/**
 * Transform backend product data to frontend format
 */
const transformProduct = (item) => {
    if (!item) return null;

    try {
        // Parse JSON fields safely
        let colors = [];
        if (item.colors) {
            try {
                colors = typeof item.colors === 'string' ? JSON.parse(item.colors) : item.colors;
            } catch (e) {
                console.warn('Failed to parse colors:', item.colors);
                colors = [];
            }
        }

        let sizes = [];
        if (item.size) {
            try {
                sizes = typeof item.size === 'string' ? JSON.parse(item.size) : item.size;
            } catch (e) {
                console.warn('Failed to parse size:', item.size);
                sizes = [];
            }
        }

        // Parse tags
        let tags = [];
        if (item.tags) {
            if (typeof item.tags === 'string') {
                tags = item.tags.split(',').map(t => t.trim()).filter(t => t);
            } else if (Array.isArray(item.tags)) {
                tags = item.tags;
            }
        }

        // Parse images
        let images = [];
        if (item.images) {
            try {
                images = typeof item.images === 'string' ? JSON.parse(item.images) : item.images;
            } catch (e) {
                images = item.image ? [item.image] : [];
            }
        } else if (item.image) {
            images = [item.image];
        }

        return {
            ...item,
            id: item.productId || item.id,
            colors: colors,
            size: sizes,
            tags: tags,
            images: images,
            inStock: item.stockQuantity > 0
        };
    } catch (error) {
        console.error('Error transforming product:', error);
        return item;
    }
};

/**
 * Get personalized product recommendations based on user's purchase history
 * 
 * Algorithm:
 * - Analyzes past orders to identify preferred categories
 * - Recommends products from frequently purchased categories
 * - Filters out already purchased products
 * - Returns highest rated products from those categories
 * - Falls back to popular products if no purchase history
 * 
 * @param {number} limit - Maximum number of recommendations (default: 10, max: 50)
 * @param {string} customerId - Optional customer ID (will fetch from session if not provided)
 * @returns {Promise<Array>} Array of recommended products
 */
export const getRecommendations = async (limit = 10, customerId = null) => {
    try {
        // Get customer ID if not provided
        if (!customerId) {
            customerId = await getUserUuid(true); // stayAsGuest = true
        }

        const params = new URLSearchParams();
        if (customerId) {
            params.append('customerId', customerId);
        }
        params.append('limit', limit.toString());

        const url = `${API_CONFIG.BASE_URL}${API_ENDPOINTS.PRODUCTS}/recommendations?${params.toString()}`;
        console.log('Fetching recommendations from:', url);

        const response = await fetch(url, {
            method: 'GET',
            credentials: 'include',
            headers: {
                'Content-Type': 'application/json',
            },
        });

        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }

        const data = await response.json();
        console.log('Recommendations received:', data.length, 'products');

        const transformedData = Array.isArray(data) ? data.map(transformProduct).filter(p => p !== null) : [];
        return transformedData;
    } catch (error) {
        console.error('Failed to fetch recommendations:', error);
        return [];
    }
};

/**
 * Get product recommendations within a specific category
 * Useful for showing recommendations on category pages
 * 
 * @param {string} categoryIdOrSlug - Category ID or slug (e.g., "women-loungewear")
 * @param {number} limit - Maximum number of recommendations
 * @param {string} customerId - Optional customer ID
 * @returns {Promise<Array>} Array of recommended products from the category
 */
export const getRecommendationsByCategory = async (categoryIdOrSlug, limit = 10, customerId = null) => {
    try {
        if (!customerId) {
            customerId = await getUserUuid(true);
        }

        const params = new URLSearchParams();
        if (customerId) {
            params.append('customerId', customerId);
        }
        params.append('limit', limit.toString());

        const url = `${API_CONFIG.BASE_URL}${API_ENDPOINTS.PRODUCTS}/recommendations/category/${categoryIdOrSlug}?${params.toString()}`;
        console.log('Fetching category recommendations from:', url);

        const response = await fetch(url, {
            method: 'GET',
            credentials: 'include',
            headers: {
                'Content-Type': 'application/json',
            },
        });

        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }

        const data = await response.json();
        console.log('Category recommendations received:', data.length, 'products');

        const transformedData = Array.isArray(data) ? data.map(transformProduct).filter(p => p !== null) : [];
        return transformedData;
    } catch (error) {
        console.error('Failed to fetch category recommendations:', error);
        return [];
    }
};

/**
 * Get similar products based on a specific product
 * Perfect for "You may also like" or "Similar products" sections
 * 
 * Returns products from the same category, excluding:
 * - The product itself
 * - Products already purchased by the user
 * 
 * @param {string} productId - Product ID to find similar products for
 * @param {number} limit - Maximum number of similar products (default: 8)
 * @param {string} customerId - Optional customer ID
 * @returns {Promise<Array>} Array of similar products
 */
export const getSimilarProducts = async (productId, limit = 8, customerId = null) => {
    try {
        if (!customerId) {
            customerId = await getUserUuid(true);
        }

        const params = new URLSearchParams();
        if (customerId) {
            params.append('customerId', customerId);
        }
        params.append('limit', limit.toString());

        const url = `${API_CONFIG.BASE_URL}${API_ENDPOINTS.PRODUCTS}/${productId}/similar?${params.toString()}`;
        console.log('Fetching similar products from:', url);

        const response = await fetch(url, {
            method: 'GET',
            credentials: 'include',
            headers: {
                'Content-Type': 'application/json',
            },
        });

        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }

        const data = await response.json();
        console.log('Similar products received:', data.length, 'products');

        // Transform products to frontend format
        const transformedData = Array.isArray(data) ? data.map(transformProduct).filter(p => p !== null) : [];
        return transformedData;
    } catch (error) {
        console.error('Failed to fetch similar products:', error);
        return [];
    }
};

/**
 * Get popular products (highest rated, in stock)
 * This is what guests see, or fallback when no purchase history
 * 
 * @param {number} limit - Maximum number of products
 * @returns {Promise<Array>} Array of popular products
 */
export const getPopularProducts = async (limit = 10) => {
    // Same as getRecommendations but without customer ID
    return getRecommendations(limit, null);
};

/**
 * Get product recommendations based on items in shopping cart
 * Analyzes cart contents and suggests complementary products
 * 
 * @param {number} limit - Maximum number of recommendations (default: 10)
 * @param {string} customerId - Optional customer ID (uses session if not provided)
 * @returns {Promise<Array>} Array of recommended products based on cart
 */
export const getCartRecommendations = async (limit = 10, customerId = null) => {
    try {
        if (!customerId) {
            customerId = await getUserUuid(true);
        }

        const params = new URLSearchParams();
        if (customerId) {
            params.append('customerId', customerId);
        }
        params.append('limit', limit.toString());

        const url = `${API_CONFIG.BASE_URL}${API_ENDPOINTS.PRODUCTS}/recommendations/cart?${params.toString()}`;
        console.log('Fetching cart-based recommendations from:', url);

        const response = await fetch(url, {
            method: 'GET',
            credentials: 'include',
            headers: {
                'Content-Type': 'application/json',
            },
        });

        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }

        const data = await response.json();
        console.log('Cart-based recommendations received:', data.length, 'products');

        const transformedData = Array.isArray(data) ? data.map(transformProduct).filter(p => p !== null) : [];
        return transformedData;
    } catch (error) {
        console.error('Failed to fetch cart recommendations:', error);
        return [];
    }
};

/**
 * Get product recommendations based on user's browsing/view history
 * Analyzes products the user has recently viewed and suggests similar items
 * 
 * Algorithm:
 * - Analyzes products user has recently viewed
 * - Identifies categories from viewed products
 * - Recommends products from frequently viewed categories
 * - Prioritizes by view frequency and recency
 * - Excludes products already viewed or purchased
 * - Returns highest rated products from those categories
 * - Falls back to popular products if no view history
 * 
 * @param {number} limit - Maximum number of recommendations (default: 10, max: 50)
 * @param {string} customerId - Optional customer ID (uses session if not provided)
 * @returns {Promise<Array>} Array of recommended products based on browsing history
 */
export const getViewHistoryRecommendations = async (limit = 10, customerId = null) => {
    try {
        if (!customerId) {
            customerId = await getUserUuid(true);
        }

        const params = new URLSearchParams();
        if (customerId) {
            params.append('customerId', customerId);
        }
        params.append('limit', limit.toString());

        const url = `${API_CONFIG.BASE_URL}${API_ENDPOINTS.PRODUCTS}/recommendations/history?${params.toString()}`;
        console.log('Fetching view history recommendations from:', url);

        const response = await fetch(url, {
            method: 'GET',
            credentials: 'include',
            headers: {
                'Content-Type': 'application/json',
            },
        });

        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }

        const data = await response.json();
        console.log('View history recommendations received:', data.length, 'products');

        const transformedData = Array.isArray(data) ? data.map(transformProduct).filter(p => p !== null) : [];
        return transformedData;
    } catch (error) {
        console.error('Failed to fetch view history recommendations:', error);
        return [];
    }
};
