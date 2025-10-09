/**
 * Cart API module for managing shopping cart operations.
 * 
 * Backend ShoppingCart Entity:
 * - cartId (String, UUID)
 * - customerId (String, UUID) - Required
 * - variantId (String, UUID) - ProductVariant ID
 * - quantity (Integer)
 * - addedAt (LocalDateTime)
 * - customer (Customer object)
 * - variant (ProductVariant object with product details)
 * 
 * @author Yunhe
 * @date 2025-10-08
 * @version 1.1
 */
import API_CONFIG, { API_ENDPOINTS } from '../config/apiConfig';

const CART_STORAGE_KEY = 'aori_shopping_cart';
const TEMP_CUSTOMER_ID = 'temp-customer-id'; // Temporary customer ID for guest users

export const getCart = async (customerId, useMock = false) => {
    if (useMock) {
        try {
            const cart = localStorage.getItem(CART_STORAGE_KEY);
            return cart ? JSON.parse(cart) : [];
        } catch (error) {
            console.error('Error getting cart:', error);
            return [];
        }
    }

    try {

        const custId = "5f2f7b1d-c3d1-4a3e-abca-6447215ea70a";
        const url = `${API_CONFIG.BASE_URL}${API_ENDPOINTS.CART}?customerId=${custId}`;
        console.log('Fetching cart from:', url);

        const response = await fetch(url, {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json',
            },
        });

        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }

        const data = await response.json();
        console.log('Cart data from API:', data);

        // Transform backend cart items to frontend format
        if (Array.isArray(data)) {
            return data.map(transformCartItem).filter(item => item !== null);
        } else if (data.cart && Array.isArray(data.cart)) {
            return data.cart.map(transformCartItem).filter(item => item !== null);
        }

        return [];
    } catch (error) {
        console.error('Error fetching cart from API:', error);
        console.error('Falling back to localStorage');
        const cart = localStorage.getItem(CART_STORAGE_KEY);
        return cart ? JSON.parse(cart) : [];
    }
};

/**
 * Transform backend cart item to frontend format
 */
const transformCartItem = (backendItem) => {
    if (!backendItem) return null;

    try {
        return {
            cartId: backendItem.cartId,
            customerId: backendItem.customerId,
            variantId: backendItem.variantId,
            quantity: backendItem.quantity,
            addedAt: backendItem.addedAt,

            // Frontend fields from variant/product
            productId: backendItem.variant?.productId || backendItem.productId,
            productName: backendItem.variant?.product?.productName || backendItem.name,
            price: backendItem.variant?.price || backendItem.price || 0,
            image: backendItem.variant?.product?.image || backendItem.image,
            size: backendItem.variant?.size || backendItem.size,
            color: backendItem.variant?.color || backendItem.color,
            sku: backendItem.variant?.sku,
            stockQuantity: backendItem.variant?.stockQuantity,

            // Full objects for reference
            variant: backendItem.variant,
            product: backendItem.variant?.product,
        };
    } catch (error) {
        console.error('Error transforming cart item:', error);
        return null;
    }
};

export const addToCart = async (item, useMock = false) => {
    if (useMock) {
        try {
            const cart = await getCart(true);

            const existingIndex = cart.findIndex(
                cartItem =>
                    cartItem.productId === item.productId &&
                    cartItem.color === item.color &&
                    cartItem.size === item.size
            );

            if (existingIndex > -1) {
                cart[existingIndex].quantity += item.quantity || 1;
            } else {
                cart.push({
                    ...item,
                    quantity: item.quantity || 1,
                    addedAt: new Date().toISOString(),
                });
            }

            localStorage.setItem(CART_STORAGE_KEY, JSON.stringify(cart));
            return { success: true, cart };
        } catch (error) {
            console.error('Error adding to cart:', error);
            return { success: false, error: error.message };
        }
    }

    try {
        // Backend expects: { customerId, variantId, quantity }
        const cartItem = {
            customerId: "5f2f7b1d-c3d1-4a3e-abca-6447215ea70a",
            variantId: item.variantId || item.variant_id,
            quantity: item.quantity || 1,
        };

        console.log('Adding to cart:', cartItem);

        const response = await fetch(`${API_CONFIG.BASE_URL}${API_ENDPOINTS.CART_ADD}`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(cartItem),
        });

        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }

        const data = await response.json();
        console.log('Added to cart:', data);
        return { success: true, data };
    } catch (error) {
        console.error('Error adding to cart via API:', error);
        console.error('Falling back to localStorage');

        // Fallback to localStorage
        return addToCart(item, true);
    }
};

export const updateCartItem = async (index, quantity, useMock = API_CONFIG.USE_MOCK) => {
    if (useMock) {
        try {
            const cart = await getCart(true);
            if (cart[index]) {
                cart[index].quantity = quantity;
                localStorage.setItem(CART_STORAGE_KEY, JSON.stringify(cart));
                return { success: true, cart };
            }
            return { success: false, error: 'Item not found' };
        } catch (error) {
            console.error('Error updating cart:', error);
            return { success: false, error: error.message };
        }
    }

    try {
        const cart = await getCart(false);
        const item = cart[index];
        if (!item) {
            return { success: false, error: 'Item not found' };
        }

        const response = await fetch(`${API_CONFIG.BASE_URL}${API_ENDPOINTS.CART}/${item.id}`, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({ quantity }),
        });

        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }

        const data = await response.json();
        return data;
    } catch (error) {
        console.error('Error updating cart via API:', error);
        return { success: false, error: error.message };
    }
};

export const removeFromCart = async (index, useMock = API_CONFIG.USE_MOCK) => {
    if (useMock) {
        try {
            const cart = await getCart(true);
            cart.splice(index, 1);
            localStorage.setItem(CART_STORAGE_KEY, JSON.stringify(cart));
            return { success: true, cart };
        } catch (error) {
            console.error('Error removing from cart:', error);
            return { success: false, error: error.message };
        }
    }

    try {
        const cart = await getCart(false);
        const item = cart[index];
        if (!item) {
            return { success: false, error: 'Item not found' };
        }

        const response = await fetch(`${API_CONFIG.BASE_URL}${API_ENDPOINTS.CART_REMOVE}/${item.id}`, {
            method: 'DELETE',
            headers: {
                'Content-Type': 'application/json',
            },
        });

        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }

        const data = await response.json();
        return data;
    } catch (error) {
        console.error('Error removing from cart via API:', error);
        return { success: false, error: error.message };
    }
};

export const clearCart = async (useMock = API_CONFIG.USE_MOCK) => {
    if (useMock) {
        try {
            localStorage.removeItem(CART_STORAGE_KEY);
            return { success: true };
        } catch (error) {
            console.error('Error clearing cart:', error);
            return { success: false, error: error.message };
        }
    }

    try {
        const response = await fetch(`${API_CONFIG.BASE_URL}${API_ENDPOINTS.CART}`, {
            method: 'DELETE',
            headers: {
                'Content-Type': 'application/json',
            },
        });

        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }

        const data = await response.json();
        return data;
    } catch (error) {
        console.error('Error clearing cart via API:', error);
        return { success: false, error: error.message };
    }
};

export const getCartCount = async (useMock = API_CONFIG.USE_MOCK) => {
    const cart = await getCart(useMock);
    return cart.reduce((total, item) => total + item.quantity, 0);
};

export const getCartTotal = async (useMock = API_CONFIG.USE_MOCK) => {
    const cart = await getCart(useMock);
    return cart.reduce((total, item) => total + item.price * item.quantity, 0);
};

export const syncCartWithServer = async (useMock = API_CONFIG.USE_MOCK) => {
    if (useMock) {
        return { success: true, message: 'Mock mode, no sync needed' };
    }

    try {
        const cart = await getCart(true);
        const response = await fetch(`${API_CONFIG.BASE_URL}${API_ENDPOINTS.CART}`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({ cart }),
        });

        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }

        const data = await response.json();
        return data;
    } catch (error) {
        console.error('Error syncing cart:', error);
        return { success: false, error: error.message };
    }
};
