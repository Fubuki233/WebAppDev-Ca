/**
 * Order API module for managing order data.
 * 
 * @author Yunhe
 * @date 2025-10-11
 * @version 1.0
 */
import API_CONFIG, { API_ENDPOINTS } from '../config/apiConfig';
import { authenticatedFetch } from './apiUtils';

/**
 * Get all orders for the current user
 */
export const getUserOrders = async () => {
    try {
        const response = await authenticatedFetch(`${API_CONFIG.BASE_URL}${API_ENDPOINTS.ORDERS}`, {
            method: 'GET',
        });

        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }

        const data = await response.json();
        console.log('User orders:', data);
        return data;
    } catch (error) {
        console.error('Error fetching user orders:', error);
        throw error;
    }
};

/**
 * Get order details by orderId
 */
export const getOrderDetails = async (orderId) => {
    try {
        const response = await authenticatedFetch(`${API_CONFIG.BASE_URL}${API_ENDPOINTS.ORDERS}/${orderId}`, {
            method: 'GET',
        });

        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }

        const data = await response.json();
        console.log('Order details:', data);
        console.log('Order items with products:', data.orderItems?.map(item => ({
            productId: item.productId,
            productName: item.product?.productName,
            image: item.product?.image,
            quantity: item.quantity,
            unitPrice: item.unitPrice || item.priceAtPurchase
        })));
        return data;
    } catch (error) {
        console.error('Error fetching order details:', error);
        throw error;
    }
};

/**
 * Cancel an order
 */
export const cancelOrder = async (orderId) => {
    try {
        const response = await authenticatedFetch(`${API_CONFIG.BASE_URL}${API_ENDPOINTS.ORDERS}/${orderId}/cancellation`, {
            method: 'POST',
        });

        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }

        const data = await response.json();
        console.log('Cancel order response:', data);
        return data;
    } catch (error) {
        console.error('Error cancelling order:', error);
        throw error;
    }
};

/**
 * Process payment for an order
 */
export const processPayment = async (orderId) => {
    try {
        const response = await authenticatedFetch(`${API_CONFIG.BASE_URL}${API_ENDPOINTS.ORDERS}/${orderId}/payment`, {
            method: 'POST',
        });

        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }

        const data = await response.json();
        console.log('Process payment response:', data);
        return data;
    } catch (error) {
        console.error('Error processing payment:', error);
        throw error;
    }
};

/**
 * Get order status
 */
export const getOrderStatus = async (orderId) => {
    try {
        const response = await authenticatedFetch(`${API_CONFIG.BASE_URL}${API_ENDPOINTS.ORDERS}/${orderId}/status`, {
            method: 'GET',
        });

        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }

        const data = await response.json();
        console.log('Order status:', data);
        return data;
    } catch (error) {
        console.error('Error fetching order status:', error);
        throw error;
    }
};
