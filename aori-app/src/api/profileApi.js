/**
 * profileApi.js - Profile Management API
 * 
 * @author Yunhe
 * @date 2025-10-09
 * @version 1.0
 * 
 * @version 1.1 - debug updateProfile function to better handle network errors
 * @date 2025-10-15
 */

import API_CONFIG, { API_ENDPOINTS } from '../config/apiConfig';
import { apiRequest } from './apiUtils';

/**
 * Get current user's profile
 * @returns {Promise<Object>} Response with profile data
 */
export const getProfile = async () => {
    try {
        const data = await apiRequest(`${API_CONFIG.BASE_URL}${API_ENDPOINTS.USER_PROFILE}`, {
            method: 'GET'
        });

        if (data.success) {
            return {
                success: true,
                profile: data.profile,
                message: 'Profile loaded successfully'
            };
        } else {
            return {
                success: false,
                message: data.message || 'Failed to load profile'
            };
        }
    } catch (error) {
        console.error('Get profile error:', error);
        return {
            success: false,
            message: 'Network error. Please check your connection and try again.'
        };
    }
};

/**
 * Update current user's profile
 * @param {Object} profileData - Updated profile data
 * @returns {Promise<Object>} Response with updated profile
 */
export const updateProfile = async (profileData) => {
    try {
        const data = await apiRequest(`${API_CONFIG.BASE_URL}${API_ENDPOINTS.USER_PROFILE_EDIT}`, {
            method: 'PUT',
            body: JSON.stringify(profileData)
        });

        if (data.success) {
            return {
                success: true,
                profile: data.profile,
                message: data.message || 'Profile updated successfully'
            };
        } else {
            return {
                success: false,
                message: data.message || 'Failed to update profile',
                status: data.status
            };
        }
    } catch (error) {
        console.error('Update profile error:', error);
        // Check if it's a network error or server error
        if (error.message && error.message.includes('Failed to fetch')) {
            return {
                success: false,
                message: 'Network error. Please check your connection and try again.'
            };
        }
        return {
            success: false,
            message: error.message || 'An unexpected error occurred. Please try again.'
        };
    }
};

/**
 * Get current user's addresses
 * @returns {Promise<Object>} Response with addresses data
 */
export const getAddresses = async () => {
    try {
        const response = await fetch(`${API_CONFIG.BASE_URL}${API_ENDPOINTS.USER_ADDRESSES}`, {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json',
            },
            credentials: 'include',
        });

        const data = await response.json();

        if (data.success) {
            return {
                success: true,
                addresses: data.addresses,
                message: 'Addresses loaded successfully'
            };
        } else {
            return {
                success: false,
                message: data.message || 'Failed to load addresses'
            };
        }
    } catch (error) {
        console.error('Get addresses error:', error);
        return {
            success: false,
            message: 'Network error. Please try again.'
        };
    }
};

/**
 * Update a specific address
 * @param {string} addressId - Address ID
 * @param {Object} addressData - Updated address data
 * @returns {Promise<Object>} Response with updated address
 */
export const updateAddress = async (addressId, addressData) => {
    try {
        const response = await fetch(`${API_CONFIG.BASE_URL}${API_ENDPOINTS.USER_ADDRESSES}/${addressId}`, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json',
            },
            credentials: 'include',
            body: JSON.stringify(addressData),
        });

        const data = await response.json();

        if (data.success) {
            return {
                success: true,
                address: data.address,
                message: 'Address updated successfully'
            };
        } else {
            return {
                success: false,
                message: data.message || 'Failed to update address'
            };
        }
    } catch (error) {
        console.error('Update address error:', error);
        return {
            success: false,
            message: 'Network error. Please try again.'
        };
    }
};
