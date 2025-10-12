/**
 * viewHistoryApi.js - View History API
 * 
 * @author Yunhe
 * @date 2025-10-12
 */

import { API_CONFIG, API_ENDPOINTS } from '../config/apiConfig';
import { authenticatedFetch } from './apiUtils';

const API_BASE = API_CONFIG.BASE_URL;
/**
 * fetch user's view history
 * @param {string} userId 
 * @returns {Promise<Array>} 
 */
export const getViewHistory = async (userId) => {
    try {
        const url = `${API_BASE}${API_ENDPOINTS.VIEW_HISTORY}?id=${userId}`;
        console.log('Fetching view history from:', url);

        const response = await authenticatedFetch(url, {
            method: 'GET',
        });

        console.log('View history response status:', response.status);

        if (!response.ok) {
            const errorText = await response.text();
            console.error('View history error response:', errorText);
            throw new Error('Failed to fetch view history');
        }

        const data = await response.json();
        console.log('View history data:', data);
        return data;
    } catch (error) {
        console.error('Get view history error:', error);
        return [];
    }
};

/**
 * Add a product to user's view history
 * @param {string} userId - User ID
 * @param {string} productId - Product ID
 * @returns {Promise<Object>} Operation result
 */
export const addViewHistory = async (userId, productId) => {
    try {
        const url = `${API_BASE}${API_ENDPOINTS.VIEW_HISTORY}?id=${userId}&product=${productId}`;
        console.log('Adding view history:', url);

        const response = await authenticatedFetch(url, {
            method: 'PUT',
        });

        console.log('Add view history response status:', response.status);

        if (!response.ok) {
            const errorText = await response.text();
            console.error('Add view history error response:', errorText);
            throw new Error('Failed to add view history');
        }

        const data = await response.text();
        console.log('Add view history raw response:', data);
        const result = JSON.parse(data);
        console.log('Add view history parsed result:', result);
        return { success: true, ...result };
    } catch (error) {
        console.error('Add view history error:', error);
        return { success: false, error: error.message };
    }
};
