/**
 * Cart API module for managing shopping cart operations.
 * 
 * @author Yunhe
 * @date 2025-10-08
 * @version 1.2 - Updated to use getUserUuid() from apiUtils
 * 
 * @author Yunhe
 * @date 2025-10-9
 * @version 1.3 - Fixed addToCart to use productId instead of variantId
 * 
 * @author Yunhe
 * @date 2025-10-11
 * @version 1.4 - will redirect to login when fetching cart for guest
 */
import API_CONFIG, { API_ENDPOINTS } from '../config/apiConfig';
import { getUserUuid } from './apiUtils';

const CART_STORAGE_KEY = 'aori_shopping_cart';

export const getCart = async (customerId, useMock = false, stayAsGuest = false) => {
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
        const custId = await getUserUuid();
        if (!custId) {
            console.warn('No customer UUID available, using localStorage');
            const cart = localStorage.getItem(CART_STORAGE_KEY);
            return cart ? JSON.parse(cart) : [];
        }

        const url = `${API_CONFIG.BASE_URL}${API_ENDPOINTS.CART}`;
        console.log('Fetching cart from:', url);

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

        const data = await response.json();
        console.log('Cart data from API:', data);


        if (Array.isArray(data)) {
            return data.map(transformCartItem).filter(item => item !== null);
        } else if (data.cartItems && Array.isArray(data.cartItems)) {
            return data.cartItems.map(transformCartItem).filter(item => item !== null);
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
 * Backend returns: { cartId, customerId, productId, quantity, addedAt, customer, product }
 */
const transformCartItem = (backendItem) => {
    if (!backendItem) return null;

    try {
        const product = backendItem.product;

        return {
            cartId: backendItem.cartId,
            customerId: backendItem.customerId,
            productId: backendItem.productId || product?.productId,
            quantity: backendItem.quantity,
            addedAt: backendItem.addedAt,

            productName: product?.productName || backendItem.productName,
            name: product?.productName || backendItem.productName,
            price: product?.price || backendItem.price || 0,
            image: product?.image || backendItem.image,
            size: (() => {
                try {
                    const sizes = product?.size || backendItem.size;
                    if (typeof sizes === 'string') {
                        const parsed = JSON.parse(sizes);
                        return Array.isArray(parsed) && parsed.length > 0 ? parsed[0] : 'M';
                    }
                    return sizes || 'M';
                } catch (e) {
                    return 'M';
                }
            })(),
            color: (() => {
                try {
                    const colors = product?.colors || backendItem.colors || product?.color || backendItem.color;
                    if (typeof colors === 'string') {
                        const parsed = JSON.parse(colors);
                        return Array.isArray(parsed) && parsed.length > 0 ? parsed[0] : '#000';
                    }
                    return colors || '#000';
                } catch (e) {
                    return '#000';
                }
            })(),
            colors: product?.colors || backendItem.colors,
            sizes: product?.size || backendItem.size,
            stockQuantity: product?.stockQuantity || backendItem.stockQuantity,
            description: product?.description,
            productCode: product?.productCode,

            product: product,
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
        const custId = await getUserUuid();
        if (!custId) {
            console.warn('No customer UUID available, falling back to localStorage');
            return addToCart(item, true);
        }

        const cartItem = {
            productId: item.productId,
            quantity: item.quantity || 1,
        };

        console.log('Adding to cart:', cartItem);

        const response = await fetch(`${API_CONFIG.BASE_URL}${API_ENDPOINTS.CART_ADD}`, {
            method: 'POST',
            credentials: 'include',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(cartItem),
        });

        if (!response.ok) {
            const errorData = await response.json().catch(() => ({}));
            throw new Error(errorData.message || `HTTP error! status: ${response.status}`);
        }

        const data = await response.json();
        console.log('Added to cart:', data);
        return { success: true, data };
    } catch (error) {
        console.error('Error adding to cart via API:', error);
        console.error('Falling back to localStorage');

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

        const cartId = item.cartId || item.id;
        const response = await fetch(`${API_CONFIG.BASE_URL}${API_ENDPOINTS.CART}/items/${cartId}`, {
            method: 'PUT',
            credentials: 'include',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({ quantity }),
        });

        if (!response.ok) {
            const errorData = await response.json().catch(() => ({}));
            throw new Error(errorData.message || `HTTP error! status: ${response.status}`);
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

        const cartId = item.cartId || item.id;
        const response = await fetch(`${API_CONFIG.BASE_URL}${API_ENDPOINTS.CART}/items/${cartId}`, {
            method: 'DELETE',
            credentials: 'include',
            headers: {
                'Content-Type': 'application/json',
            },
        });

        if (!response.ok) {
            const errorData = await response.json().catch(() => ({}));
            throw new Error(errorData.message || `HTTP error! status: ${response.status}`);
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
            credentials: 'include',
            headers: {
                'Content-Type': 'application/json',
            },
        });

        if (!response.ok) {
            const errorData = await response.json().catch(() => ({}));
            throw new Error(errorData.message || `HTTP error! status: ${response.status}`);
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
            credentials: 'include',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({ cart }),
        });

        if (!response.ok) {
            const errorData = await response.json().catch(() => ({}));
            throw new Error(errorData.message || `HTTP error! status: ${response.status}`);
        }

        const data = await response.json();
        return data;
    } catch (error) {
        console.error('Error syncing cart:', error);
        return { success: false, error: error.message };
    }
};
