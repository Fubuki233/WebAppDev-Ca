/**
 * SKU API module for managing product inventory by SKU.
 * 
 * @author Yunhe
 * @date 2025-10-13
 * @version 1.1 - Remove # from color parameter
 */
import API_CONFIG from '../config/apiConfig';

/**
 * Helper function to normalize color value (remove # prefix if present)
 * @param {string} color - The color value (may include #)
 * @returns {string} The color value without #
 */
const normalizeColor = (color) => {
    if (!color) return color;
    return color.startsWith('#') ? color.substring(1) : color;
};

/**
 * Get the available quantity for a specific product SKU
 * @param {string} productId - The product ID
 * @param {string} colour - The selected color (# will be removed if present)
 * @param {string} size - The selected size
 * @returns {Promise<number>} The available quantity (-1 if product not found)
 */
export const getSkuQuantity = async (productId, colour, size) => {
    try {
        const normalizedColour = normalizeColor(colour);
        const url = `${API_CONFIG.BASE_URL}/product/sku?id=${encodeURIComponent(productId)}&colour=${encodeURIComponent(normalizedColour)}&size=${encodeURIComponent(size)}`;

        console.log('[SKU API] Fetching quantity:', { productId, colour: normalizedColour, size });

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

        const quantity = await response.json();
        console.log('[SKU API] Available quantity:', quantity);

        return quantity;
    } catch (error) {
        console.error('[SKU API] Error fetching SKU quantity:', error);
        throw error;
    }
};

/**
 * Checkout a product SKU (decrease quantity by 1)
 * @param {string} productId - The product ID
 * @param {string} colour - The selected color (# will be removed if present)
 * @param {string} size - The selected size
 * @returns {Promise<number>} The remaining quantity after checkout (-1 if failed)
 */
export const checkoutSku = async (productId, colour, size) => {
    try {
        const normalizedColour = normalizeColor(colour);
        const url = `${API_CONFIG.BASE_URL}/product/sku/checkout`;

        console.log('[SKU API] Checking out SKU:', { productId, colour: normalizedColour, size });

        const response = await fetch(url, {
            method: 'POST',
            credentials: 'include',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded',
            },
            body: new URLSearchParams({
                id: productId,
                colour: normalizedColour,
                size: size
            })
        });

        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }

        const remainingQuantity = await response.json();
        console.log('[SKU API] Remaining quantity after checkout:', remainingQuantity);

        return remainingQuantity;
    } catch (error) {
        console.error('[SKU API] Error checking out SKU:', error);
        throw error;
    }
};

/**
 * Create or update SKU inventory (Admin only)
 * @param {string} productId - The product ID
 * @param {string} colour - The color (# will be removed if present)
 * @param {string} size - The size
 * @param {number} quantity - The quantity to set
 * @returns {Promise<string>} The SKU identifier
 */
export const createSku = async (productId, colour, size, quantity) => {
    try {
        const normalizedColour = normalizeColor(colour);
        const url = `${API_CONFIG.BASE_URL}/product/admin/sku`;

        console.log('[SKU API] Creating/updating SKU:', { productId, colour: normalizedColour, size, quantity });

        const response = await fetch(url, {
            method: 'POST',
            credentials: 'include',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded',
            },
            body: new URLSearchParams({
                id: productId,
                colour: normalizedColour,
                size: size,
                quantity: quantity.toString()
            })
        });

        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }

        const sku = await response.text();
        console.log('[SKU API] Created SKU:', sku);

        return sku;
    } catch (error) {
        console.error('[SKU API] Error creating SKU:', error);
        throw error;
    }
};
