/**
 * apiUtils.js - Common API utilities
 * 
 * @author Yunhe
 * @date 2025-10-09
 * @version 1.0
 */

/**
 * Handle authentication error responses
 * @param {Response} response - Fetch response object
 * @returns {boolean} true if authentication error was handled, false otherwise
 */
export const handleAuthError = async (response) => {
    if (response.status === 401) {
        try {
            const errorData = await response.json();
            if (errorData.redirectTo) {
                console.log('Authentication required, redirecting to:', errorData.redirectTo);
                window.location.href = errorData.redirectTo;
            } else {
                console.log('Authentication required, redirecting to login');
                window.location.hash = '#login';
            }
        } catch (e) {
            // If response is not JSON, just redirect to login
            console.log('Authentication required, redirecting to login');
            window.location.hash = '#login';
        }
        return true;
    }
    return false;
};

/**
 * Standard fetch wrapper with authentication handling
 * @param {string} url - Request URL
 * @param {Object} options - Fetch options
 * @returns {Promise<Response>} Fetch response
 */
export const authenticatedFetch = async (url, options = {}) => {
    const defaultOptions = {
        credentials: 'include',
        headers: {
            'Content-Type': 'application/json',
            ...options.headers
        },
        ...options
    };

    const response = await fetch(url, defaultOptions);

    // Handle authentication errors
    if (await handleAuthError(response)) {
        // Return a rejected promise to stop further processing
        throw new Error('Authentication required');
    }

    return response;
};

/**
 * Make an authenticated API request with JSON response
 * @param {string} url - Request URL
 * @param {Object} options - Fetch options
 * @returns {Promise<Object>} JSON response data
 */
export const apiRequest = async (url, options = {}) => {
    try {
        const response = await authenticatedFetch(url, options);

        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }

        return await response.json();
    } catch (error) {
        if (error.message === 'Authentication required') {
            return {
                success: false,
                message: 'Authentication required'
            };
        }
        throw error;
    }
};

import API_CONFIG, { API_ENDPOINTS } from '../config/apiConfig';

/**
 * Get current user's UUID from backend session
 * @returns {Promise<string|null>} User UUID or null if not authenticated
 */
export const getUserUuid = async () => {
    try {
        const response = await fetch(`${API_CONFIG.BASE_URL}${API_ENDPOINTS.USER_UUID}`, {
            method: 'GET',
            credentials: 'include',
            headers: {
                'Content-Type': 'application/json',
            },
        });

        if (!response.ok) {
            console.warn('Failed to get user UUID:', response.status);
            return null;
        }

        const data = await response.json();
        console.log('Fetched user UUID:', data.uuid);
        return data.uuid || null;
    } catch (error) {
        console.error('Error getting user UUID:', error);
        return null;
    }
};