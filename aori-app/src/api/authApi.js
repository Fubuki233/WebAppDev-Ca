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
 * @param {Object} userData 
 * @returns {Object}s
 */
const validateRegistrationData = (userData) => {
    const errors = {};

    const firstNameError = ValidationRules.name.validate(userData.firstName, 'First name');
    if (firstNameError) errors.firstName = firstNameError;

    const lastNameError = ValidationRules.name.validate(userData.lastName, 'Last name');
    if (lastNameError) errors.lastName = lastNameError;

    const emailError = ValidationRules.email.validate(userData.email);
    if (emailError) errors.email = emailError;

    const passwordError = ValidationRules.password.validate(userData.password);
    if (passwordError) errors.password = passwordError;

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
 * @param {string} email
 * @param {string} password 
 * @returns {Object}
 */
const validateLoginData = (email, password) => {
    const errors = {};

    const emailError = ValidationRules.email.validate(email);
    if (emailError) errors.email = emailError;

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
 * @param {string} email 
 * @param {string} password 
 * @returns {Promise<Object>} 
 */
export const login = async (email, password) => {
    try {
        const validation = validateLoginData(email, password);
        if (!validation.isValid) {
            return {
                success: false,
                message: Object.values(validation.errors)[0],
                errors: validation.errors
            };
        }

        const url = `${API_CONFIG.BASE_URL}${API_ENDPOINTS.USER_LOGIN}?email=${encodeURIComponent(email)}&passwd=${encodeURIComponent(password)}`;

        const response = await fetch(url, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            credentials: 'include',
        });

        const data = await response.json();

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
 * @param {Object} userData 
 * @returns {Promise<Object>} 
 */
export const register = async (userData) => {
    try {
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
 * @returns {Promise<Object>}
 */
export const logout = async () => {
    try {
        const response = await fetch(`${API_CONFIG.BASE_URL}${API_ENDPOINTS.USER_LOGOUT}`, {
            method: 'POST',
            credentials: 'include',
        });

        const data = await response.json();

        localStorage.removeItem('user');

        return {
            success: data.success || false,
            message: data.message || (data.success ? 'Logout successful' : 'Logout failed')
        };
    } catch (error) {
        console.error('Logout error:', error);
        localStorage.removeItem('user');
        return {
            success: false,
            message: 'Network error during logout'
        };
    }
};

/**
 * Check if user is authenticated
 * @returns {boolean} 
 */
export const isAuthenticated = () => {
    const user = localStorage.getItem('user');
    return !!user;
};

/**
 * Get current user from local storage
 * @returns {Object|null} 
 */
export const getCurrentUser = () => {
    const user = localStorage.getItem('user');
    return user ? JSON.parse(user) : null;
};

/**
 * Export validation utilities for use in components
 */
export { ValidationRules, validateRegistrationData, validateLoginData };
