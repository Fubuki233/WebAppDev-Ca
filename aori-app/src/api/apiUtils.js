/**
 * apiUtils.js - Common API utilities
 * 
 * @author Yunhe
 * @date 2025-10-09
 * @version 1.0
 * 
 * @date 2025-10-10
 * @version 1.1 - Added getUserUuid function to fetch user UUID from backend session
 * 
 * @date 2025-10-11
 * @version 1.2 - Improved authentication error handling with stayAsGuest option
 * 
 * @date 2025-10-14
 * @version 1.3 - Refactored apiRequest to return structured error info instead of throwing
 */

/**
 * Handle authentication error responses
 * @param {Response} response 
 * @param {boolean} stayAsGuest
 * @returns {boolean} 
 */
export const handleAuthError = async (response, stayAsGuest = false) => {
    if (response.status === 401) {
        if (stayAsGuest) {
            console.log('Authentication required but staying as guest');
            return false;
        }

        try {
            const errorData = await response.json();
            if (errorData.redirectTo) {
                console.log('Authentication required, redirecting to:', errorData.redirectTo);
                window.location.href = errorData.redirectTo;
            } else {
                console.log('Authentication required, redirecting to login');
                window.location.href = '/#login';
            }
        } catch (e) {
            console.log('Authentication required, redirecting to login');
            window.location.href = '/#login';
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

    if (await handleAuthError(response)) {
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
            // Try to parse error response body
            let errorMessage = `HTTP error! status: ${response.status}`;
            try {
                const errorData = await response.json();
                if (errorData.message) {
                    errorMessage = errorData.message;
                } else if (errorData.error) {
                    errorMessage = errorData.error;
                }
                // Return error data with success: false
                return {
                    success: false,
                    message: errorMessage,
                    status: response.status
                };
            } catch (parseError) {
                // If response is not JSON, return generic error
                return {
                    success: false,
                    message: errorMessage,
                    status: response.status
                };
            }
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
 * @param {boolean} stayAsGuest - If true, don't redirect on 401, just return null
 * @returns {Promise<string|null>} User UUID or null if not authenticated
 */
export const getUserUuid = async (stayAsGuest = false) => {
    try {
        const response = await fetch(`${API_CONFIG.BASE_URL}${API_ENDPOINTS.USER_UUID}`, {
            method: 'GET',
            credentials: 'include',
            headers: {
                'Content-Type': 'application/json',
            },
        });

        if (await handleAuthError(response, stayAsGuest)) {
            return null;
        }

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