/**
 * SKU API - Handles SKU inventory operations
 * 
 * @author Yunhe
 * @date 2025-10-15
 * @version 1.0
 */

import API_CONFIG from '../config/apiConfig';

const API_ENDPOINTS = {
    GET_SKU: '/product/sku',
    CHECKOUT_SKU: '/product/sku/checkout',
};

/**
 * Get the available quantity for a specific SKU (product + color + size combination)
 * 
 * @param {string} productId - Product UUID
 * @param {string} color - Hex color code (with or without #)
 * @param {string} size - Product size (e.g., 'S', 'M', 'L', 'XL')
 * @returns {Promise<number>} Available quantity for this SKU, or -1 if not found
 */
export const getSkuQuantity = async (productId, color, size) => {
    try {
        const cleanColor = color ? color.replace('#', '') : '';

        const url = new URL(`${API_CONFIG.BASE_URL}${API_ENDPOINTS.GET_SKU}`);
        url.searchParams.append('id', productId);
        url.searchParams.append('colour', cleanColor);
        url.searchParams.append('size', size);

        console.log('[skuApi] Fetching SKU quantity:', { productId, color: cleanColor, size });

        const response = await fetch(url, {
            method: 'GET',
            credentials: 'include',
        });

        if (!response.ok) {
            console.error('[skuApi] Failed to fetch SKU quantity:', response.status);
            return -1;
        }

        const quantity = await response.json();
        console.log('[skuApi] SKU quantity:', quantity);

        return quantity;
    } catch (error) {
        console.error('[skuApi] Error fetching SKU quantity:', error);
        return -1;
    }
};

/**
 * Check if a specific SKU is in stock
 * 
 * @param {string} productId - Product UUID
 * @param {string} color - Hex color code
 * @param {string} size - Product size
 * @returns {Promise<boolean>} True if in stock (quantity > 0)
 */
export const isSkuInStock = async (productId, color, size) => {
    const quantity = await getSkuQuantity(productId, color, size);
    return quantity > 0;
};
