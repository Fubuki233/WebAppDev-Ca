/**
 * authApi.js - Authentication API calls
 * 
 * @author Yunhe
 * @date 2025-10-09
 * @version 1.0
 */

import { API_BASE_URL } from './apiConfig';
import API_CONFIG, { API_ENDPOINTS } from '../config/apiConfig';
/**
 * Login user
 * @param {string} email - User email
 * @param {string} password - User password
 * @returns {Promise<Object>} Response with user data and token
 */
export const login = async (email, password) => {
    try {
        const response = await fetch(`$${API_CONFIG.BASE_URL}${API_ENDPOINTS.USER_LOGIN}`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            credentials: 'include', // Important for session cookies
            body: JSON.stringify({ email, password }),
        });

        if (!response.ok) {
            throw new Error('Login failed');
        }

        const data = await response.json();
        return {
            success: true,
            user: data.customer || data.user,
            message: 'Login successful'
        };
    } catch (error) {
        console.error('Login error:', error);
        return {
            success: false,
            message: error.message || 'Login failed'
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
        const response = await fetch(`${API_BASE_URL}/customers`, {
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
        const response = await fetch(`${API_BASE_URL}/logout`, {
            method: 'POST',
            credentials: 'include',
        });

        if (!response.ok) {
            throw new Error('Logout failed');
        }

        // Clear local storage
        localStorage.removeItem('user');

        return {
            success: true,
            message: 'Logout successful'
        };
    } catch (error) {
        console.error('Logout error:', error);
        return {
            success: false,
            message: error.message || 'Logout failed'
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
