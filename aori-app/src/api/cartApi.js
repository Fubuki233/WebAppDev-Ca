
import API_CONFIG, { API_ENDPOINTS } from '../config/apiConfig';

const CART_STORAGE_KEY = 'aori_shopping_cart';

export const getCart = async (useMock = API_CONFIG.USE_MOCK) => {
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
        const response = await fetch(`${API_CONFIG.BASE_URL}${API_ENDPOINTS.CART}`, {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json',
            },
        });

        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }

        const data = await response.json();
        return data.cart || [];
    } catch (error) {
        console.error('Error fetching cart from API:', error);
        const cart = localStorage.getItem(CART_STORAGE_KEY);
        return cart ? JSON.parse(cart) : [];
    }
};

export const addToCart = async (item, useMock = API_CONFIG.USE_MOCK) => {
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
        const response = await fetch(`${API_CONFIG.BASE_URL}${API_ENDPOINTS.CART_ADD}`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(item),
        });

        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }

        const data = await response.json();
        return data;
    } catch (error) {
        console.error('Error adding to cart via API:', error);
        return { success: false, error: error.message };
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
