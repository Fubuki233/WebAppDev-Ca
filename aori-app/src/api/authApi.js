/**
 * authApi.js - Authentication API calls
 * 
 * @author Yunhe
 * @date 2025-10-09
 * @version 1.0
 * 
 * @date 2025-10-11
 * @version 1.1 - Added input validation for login and registration
 * 
 * @date 2025-10-11
 * @version 1.2 - Updated login to use query parameters as per API spec
 */

import API_CONFIG, { API_ENDPOINTS } from '../config/apiConfig';

/**
 * Validation utility functions
 */
const ValidationRules = {
    // Name validation: only alphabets, max 50 characters
    name: {
        pattern: /^[A-Za-z]+$/,
        maxLength: 50,
        validate: (value, fieldName) => {
            if (!value || value.trim() === '') {
                return `${fieldName} is required`;
            }
            if (!ValidationRules.name.pattern.test(value)) {
                return `${fieldName} must contain alphabets only`;
            }
            if (value.length > ValidationRules.name.maxLength) {
                return `${fieldName} must not exceed ${ValidationRules.name.maxLength} characters`;
            }
            return null;
        }
    },

    // Email validation
    email: {
        pattern: /^[^\s@]+@[^\s@]+\.[^\s@]+$/,
        maxLength: 255,
        validate: (value) => {
            if (!value || value.trim() === '') {
                return 'Email is required';
            }
            if (!ValidationRules.email.pattern.test(value)) {
                return 'Please enter a valid email address';
            }
            if (value.length > ValidationRules.email.maxLength) {
                return `Email must not exceed ${ValidationRules.email.maxLength} characters`;
            }
            return null;
        }
    },

    // Password validation: min 8 characters
    password: {
        minLength: 8,
        maxLength: 255,
        validate: (value) => {
            if (!value || value.trim() === '') {
                return 'Password is required';
            }
            if (value.length < ValidationRules.password.minLength) {
                return `Password must be at least ${ValidationRules.password.minLength} characters`;
            }
            if (value.length > ValidationRules.password.maxLength) {
                return `Password must not exceed ${ValidationRules.password.maxLength} characters`;
            }
            return null;
        }
    },

    // Phone number validation: E.164 format
    phone: {
        pattern: /^\+?[1-9]\d{1,14}$/,
        maxLength: 15,
        validate: (value) => {
            // Phone is optional
            if (!value || value.trim() === '') {
                return null;
            }
            if (!ValidationRules.phone.pattern.test(value)) {
                return 'Phone number must follow E.164 format (e.g., +1234567890)';
            }
            if (value.length > ValidationRules.phone.maxLength) {
                return `Phone number must not exceed ${ValidationRules.phone.maxLength} characters`;
            }
            return null;
        }
    }
};

/**
 * Validate user registration data
 * @param {Object} userData - User data to validate
 * @returns {Object} Validation result with success flag and errors
 */
const validateRegistrationData = (userData) => {
    const errors = {};

    // Validate first name
    const firstNameError = ValidationRules.name.validate(userData.firstName, 'First name');
    if (firstNameError) errors.firstName = firstNameError;

    // Validate last name
    const lastNameError = ValidationRules.name.validate(userData.lastName, 'Last name');
    if (lastNameError) errors.lastName = lastNameError;

    // Validate email
    const emailError = ValidationRules.email.validate(userData.email);
    if (emailError) errors.email = emailError;

    // Validate password
    const passwordError = ValidationRules.password.validate(userData.password);
    if (passwordError) errors.password = passwordError;

    // Validate phone (optional)
    if (userData.phone) {
        const phoneError = ValidationRules.phone.validate(userData.phone);
        if (phoneError) errors.phone = phoneError;
    }

    return {
        isValid: Object.keys(errors).length === 0,
        errors
    };
};

/**
 * Validate login data
 * @param {string} email - User email
 * @param {string} password - User password
 * @returns {Object} Validation result with success flag and errors
 */
const validateLoginData = (email, password) => {
    const errors = {};

    // Validate email
    const emailError = ValidationRules.email.validate(email);
    if (emailError) errors.email = emailError;

    // Validate password (just check if not empty for login)
    if (!password || password.trim() === '') {
        errors.password = 'Password is required';
    }

    return {
        isValid: Object.keys(errors).length === 0,
        errors
    };
};

/**
 * Login user
 * @param {string} email - User email
 * @param {string} password - User password
 * @returns {Promise<Object>} Response with user data and session info
 */
export const login = async (email, password) => {
    try {
        // Validate input data
        const validation = validateLoginData(email, password);
        if (!validation.isValid) {
            return {
                success: false,
                message: Object.values(validation.errors)[0], // Return first error
                errors: validation.errors
            };
        }

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
        // Validate input data
        const validation = validateRegistrationData(userData);
        if (!validation.isValid) {
            return {
                success: false,
                message: 'Please correct the validation errors',
                errors: validation.errors
            };
        }

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

/**
 * Export validation utilities for use in components
 */
export { ValidationRules, validateRegistrationData, validateLoginData };
