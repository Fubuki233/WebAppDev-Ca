/**
 * authApi.js - Authentication API calls
 * 
 * @author Yunhe
 * @date 2025-10-09
 * @version 1.0
 */

import API_CONFIG, { API_ENDPOINTS } from '../config/apiConfig';
/**
 * Login user
 * @param {string} email - User email
 * @param {string} password - User password
 * @returns {Promise<Object>} Response with user data and session info
 */
export const login = async (email, password) => {
    try {
        // Use query parameters as per API specification
        const url = `${API_CONFIG.BASE_URL}${API_ENDPOINTS.USER_LOGIN}?email=${encodeURIComponent(email)}&passwd=${encodeURIComponent(password)}`;

        const response = await fetch(url, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            credentials: 'include', // Important for session cookies
        });

        const data = await response.json();

        // Backend returns { success, message, user, sessionId }
        if (data.success) {
            return {
                success: true,
                user: data.user,
                sessionId: data.sessionId,
                message: data.message || 'Login successful'
            };
        } else {
            return {
                success: false,
                message: data.message || 'Login failed'
            };
        }
    } catch (error) {
        console.error('Login error:', error);
        return {
            success: false,
            message: 'Network error. Please check your connection and try again.'
        };
    }
};

/**
 * Register new user
 * @param {Object} userData - User registration data
 * @returns {Promise<Object>} Response with success status
 */
export const register = async (userData) => {
    try {
        const response = await fetch(`${API_CONFIG.BASE_URL}${API_ENDPOINTS.CUSTOMER_REGISTER}`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            credentials: 'include',
            body: JSON.stringify({
                firstName: userData.firstName,
                lastName: userData.lastName,
                email: userData.email,
                password: userData.password,
                phone: userData.phone || null,
                address: null,
                city: null,
                state: null,
                zipCode: null,
                country: null
            }),
        });

        if (!response.ok) {
            const errorData = await response.json();
            throw new Error(errorData.message || 'Registration failed');
        }

        const data = await response.json();
        return {
            success: true,
            message: 'Registration successful',
            user: data
        };
    } catch (error) {
        console.error('Registration error:', error);
        return {
            success: false,
            message: error.message || 'Registration failed'
        };
    }
};

/**
 * Logout user
 * @returns {Promise<Object>} Response with success status
 */
export const logout = async () => {
    try {
        const response = await fetch(`${API_CONFIG.BASE_URL}${API_ENDPOINTS.USER_LOGOUT}`, {
            method: 'POST',
            credentials: 'include',
        });

        const data = await response.json();

        // Clear local storage regardless of response
        localStorage.removeItem('user');

        return {
            success: data.success || false,
            message: data.message || (data.success ? 'Logout successful' : 'Logout failed')
        };
    } catch (error) {
        console.error('Logout error:', error);
        // Still clear local storage on error
        localStorage.removeItem('user');
        return {
            success: false,
            message: 'Network error during logout'
        };
    }
};

/**
 * Check if user is authenticated
 * @returns {boolean} Authentication status
 */
export const isAuthenticated = () => {
    const user = localStorage.getItem('user');
    return !!user;
};

/**
 * Get current user from local storage
 * @returns {Object|null} User object or null
 */
export const getCurrentUser = () => {
    const user = localStorage.getItem('user');
    return user ? JSON.parse(user) : null;
};
