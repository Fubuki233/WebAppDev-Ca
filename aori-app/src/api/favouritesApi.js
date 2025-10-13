/**
 * Favourites API module for managing user favourites (Wishlist).
 * 
 * Backend Wishlist Entity (Composite Key):
 * - productId (String, UUID) - Part of composite key
 * - customerId (String, UUID) - Part of composite key
 * - createdAt (LocalDateTime)
 * - updatedAt (LocalDateTime)
 * - product (Product object with full details)
 * - customer (Customer object)
 * 
 * @author Yunhe
 * @date 2025-10-08
 * @version 1.2 - Updated to use getUserUuid() from apiUtils
 * 
 * @author Yunhe
 * @date 2025-10-11
 * @version 1.3 - Debug, now guest won't redirect to login when fetching favourites, but will redirect when adding/removing
 */
import API_CONFIG, { API_ENDPOINTS } from '../config/apiConfig';
import { getUserUuid } from './apiUtils';

const FAVOURITES_KEY = 'aori_favourites';


/**
 * Transform backend wishlist item to frontend format
 */
const transformWishlistItem = (backendItem) => {
    if (!backendItem) return null;

    try {
        const product = backendItem.product || {};

        return {
            productId: backendItem.productId,
            customerId: backendItem.customerId,
            createdAt: backendItem.createdAt,
            updatedAt: backendItem.updatedAt,

            id: product.productId || backendItem.productId,
            name: product.productName,
            productName: product.productName,
            productCode: product.productCode,
            description: product.description,
            price: product.price || 0,
            image: product.image,
            images: product.images || (product.image ? [product.image] : []),
            colors: typeof product.colors === 'string' ? JSON.parse(product.colors) : product.colors,
            size: typeof product.size === 'string' ? JSON.parse(product.size) : product.size,
            rating: product.rating,
            inStock: product.inStock === 'true' || product.inStock === true,
            tags: typeof product.tags === 'string' ? product.tags.split(',') : product.tags,

            categoryId: product.categoryId,
            category: product.category,

            product: product,
        };
    } catch (error) {
        console.error('Error transforming wishlist item:', error);
        return null;
    }
};

export const getFavourites = async (customerId, useMock = API_CONFIG.USE_MOCK, stayAsGuest = true) => {
    if (useMock) {
        try {
            const favourites = localStorage.getItem(FAVOURITES_KEY);
            return favourites ? JSON.parse(favourites) : [];
        } catch (error) {
            console.error('Failed to get favourites:', error);
            return [];
        }
    }

    try {
        const custId = customerId || await getUserUuid(stayAsGuest);
        if (!custId) {
            console.warn('No customer UUID available, returning empty response');
            return [];
        }

        const url = `${API_CONFIG.BASE_URL}${API_ENDPOINTS.FAVOURITES}?customerId=${custId}`;
        console.log('Fetching wishlist from:', url);

        const response = await fetch(url, {
            method: 'GET',
            credentials: 'include',
            headers: {
                'Content-Type': 'application/json',
            },
        });

        if (response.status === 401) {
            console.warn('Unauthorized, user is a guest');
            return { stayAsGuest: true };
        }

        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }

        const data = await response.json();
        console.log('Wishlist data from API:', data);

        if (Array.isArray(data)) {
            const transformed = data.map(transformWishlistItem).filter(item => item !== null);
            console.log('Transformed favourites:', transformed);
            return transformed;
        } else if (data.favourites && Array.isArray(data.favourites)) {
            return data.favourites.map(transformWishlistItem).filter(item => item !== null);
        }

        return [];
    } catch (error) {
        console.error('Failed to get favourites from API:', error);
        console.error('Falling back to localStorage');
        const favourites = localStorage.getItem(FAVOURITES_KEY);
        return favourites ? JSON.parse(favourites) : [];
    }
};


export const addToFavourites = async (item, customerId, useMock = API_CONFIG.USE_MOCK) => {
    if (useMock) {
        try {
            const favourites = await getFavourites(null, true);
            const exists = favourites.some(fav => fav.productId === item.productId);

            if (exists) {
                return { success: false, message: 'Item already in favourites' };
            }

            const newItem = {
                ...item,
                customerId: customerId || await getUserUuid(true),
                addedAt: new Date().toISOString()
            };

            favourites.push(newItem);
            localStorage.setItem(FAVOURITES_KEY, JSON.stringify(favourites));

            return { success: true, message: 'Added to favourites' };
        } catch (error) {
            console.error('Failed to add to favourites:', error);
            return { success: false, message: 'Failed to add to favourites' };
        }
    }

    try {
        const custId = customerId || await getUserUuid(true);
        if (!custId) {
            console.warn('No customer UUID available, user needs to login');
            return {
                success: false,
                requiresLogin: true,
                message: 'Please login to add items to favourites'
            };
        }

        const wishlistItem = {
            customerId: custId,
            productId: item.productId || item.id,
        };

        console.log('Adding to wishlist:', wishlistItem);

        const response = await fetch(`${API_CONFIG.BASE_URL}${API_ENDPOINTS.FAVOURITES_ADD}?customerId=${wishlistItem.customerId}&productId=${wishlistItem.productId}`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            credentials: 'include',
        });

        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }

        const data = await response.json();
        console.log('Added to wishlist:', data);
        return { success: true, message: 'Added to favourites', data };
    } catch (error) {
        console.error('Failed to add to favourites via API:', error);
        console.error('Falling back to localStorage');
        return addToFavourites(item, customerId, true);
    }
};

export const removeFromFavourites = async (productId, customerId, useMock = API_CONFIG.USE_MOCK) => {
    if (useMock) {
        try {
            const favourites = await getFavourites(null, true);
            const filtered = favourites.filter(fav => fav.productId !== productId);

            if (filtered.length < favourites.length) {
                localStorage.setItem(FAVOURITES_KEY, JSON.stringify(filtered));
                return { success: true, message: 'Removed from favourites' };
            }

            return { success: false, message: 'Item not found in favourites' };
        } catch (error) {
            console.error('Failed to remove from favourites:', error);
            return { success: false, message: 'Failed to remove from favourites' };
        }
    }

    try {
        const custId = customerId || await getUserUuid(true);
        if (!custId) {
            console.warn('No customer UUID available, user needs to login');
            return {
                success: false,
                requiresLogin: true,
                message: 'Please login to manage favourites'
            };
        }

        const url = `${API_CONFIG.BASE_URL}${API_ENDPOINTS.FAVOURITES}?customerId=${custId}&productId=${productId}`;
        console.log('Toggling (removing) from wishlist:', url);

        const response = await fetch(url, {
            method: 'POST',
            credentials: 'include',
            headers: {
                'Content-Type': 'application/json',
            },
        });

        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }

        const data = await response.json();
        console.log('Toggled wishlist:', data);
        return { success: true, message: 'Removed from favourites', data };
    } catch (error) {
        console.error('Failed to remove from favourites via API:', error);
        console.error('Falling back to localStorage');
        return removeFromFavourites(productId, customerId, true);
    }
};


export const removeFromFavouritesByProduct = async (productId, size = null, color = null, useMock = API_CONFIG.USE_MOCK) => {
    if (useMock) {

        try {
            let favourites = await getFavourites(true);

            favourites = favourites.filter(item => {
                if (item.productId !== productId) return true;
                if (size && item.size !== size) return true;
                if (color && item.color !== color) return true;
                return false;
            });

            localStorage.setItem(FAVOURITES_KEY, JSON.stringify(favourites));
            return { success: true, message: 'Removed from favourites' };
        } catch (error) {
            console.error('Failed to remove from favourites:', error);
            return { success: false, message: 'Failed to remove from favourites' };
        }
    }


    try {
        const custId = await getUserUuid(true);
        if (!custId) {
            console.warn('No customer UUID available, falling back to localStorage');
            return removeFromFavouritesByProduct(productId, size, color, true);
        }

        const params = new URLSearchParams();
        params.append('customerId', custId);
        params.append('productId', productId);
        console.log('Removing from wishlist with params:', params.toString());


        const response = await fetch(`${API_CONFIG.BASE_URL}${API_ENDPOINTS.FAVOURITES}?${params.toString()}`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            credentials: 'include',
        });

        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }

        const data = await response.json();
        return data;
    } catch (error) {
        console.error('Failed to remove from favourites via API:', error);
        return { success: false, message: 'Failed to remove from favourites' };
    }
};


export const isInFavourites = async (productId, customerId, useMock = false) => {
    if (useMock) {
        try {
            const favourites = await getFavourites(null, true);
            return favourites.some(item => item.productId === productId);
        } catch (error) {
            console.error('Failed to check favourites:', error);
            return false;
        }
    }

    try {
        const custId = customerId || await getUserUuid(true);
        if (!custId) {
            console.warn('No customer UUID available, checking localStorage');
            const favourites = await getFavourites(null, false);
            if (favourites && favourites.stayAsGuest) {
                return false;
            }
            return Array.isArray(favourites) && favourites.some(item => item.productId === productId);
        }

        const url = `${API_CONFIG.BASE_URL}${API_ENDPOINTS.FAVOURITES}/exists?customerId=${custId}&productId=${productId}`;
        console.log('Checking if in wishlist:', url);

        const response = await fetch(url, {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json',
            },
            credentials: 'include',
        });

        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }

        const data = await response.json();
        console.log('Wishlist exists result:', data);
        return data.exists || false;
    } catch (error) {
        console.error('Failed to check favourites via API:', error);
        console.error('Falling back to localStorage');
        try {
            const favourites = await getFavourites(null, true);
            if (favourites && favourites.stayAsGuest) {
                return false;
            }
            return Array.isArray(favourites) && favourites.some(item => item.productId === productId);
        } catch (e) {
            return false;
        }
    }
};


export const getFavouritesCount = async (useMock = API_CONFIG.USE_MOCK, stayAsGuest = true) => {
    try {
        const favourites = await getFavourites(useMock);
        if (favourites && favourites.stayAsGuest) {
            return 0;
        }
        return Array.isArray(favourites) ? favourites.length : 0;
    } catch (error) {
        console.error('Failed to get favourites count:', error);
        return 0;
    }
};


export const clearFavourites = async (useMock = API_CONFIG.USE_MOCK, stayAsGuest = true) => {
    if (useMock) {
        try {
            localStorage.setItem(FAVOURITES_KEY, JSON.stringify([]));
            return { success: true, message: 'Favourites cleared' };
        } catch (error) {
            console.error('Failed to clear favourites:', error);
            return { success: false, message: 'Failed to clear favourites' };
        }
    }


    try {
        const response = await fetch(`${API_CONFIG.BASE_URL}${API_ENDPOINTS.FAVOURITES}`, {
            method: 'DELETE',
            headers: {
                'Content-Type': 'application/json',
            },
            credentials: 'include',
        });

        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }

        const data = await response.json();
        return data;
    } catch (error) {
        console.error('Failed to clear favourites via API:', error);
        return { success: false, message: 'Failed to clear favourites' };
    }
};


export const toggleFavourite = async (item, customerId, useMock = API_CONFIG.USE_MOCK) => {
    if (useMock) {
        const inFavourites = await isInFavourites(item.productId, customerId, true);
        if (inFavourites) {
            return await removeFromFavourites(item.productId, customerId, true);
        } else {
            return await addToFavourites(item, customerId, true);
        }
    }

    try {
        const custId = customerId || await getUserUuid(true);
        if (!custId) {
            console.warn('No customer UUID available, user needs to login');
            return {
                success: false,
                requiresLogin: true,
                message: 'Please login to add items to favourites'
            };
        }

        const productId = item.productId || item.id;
        const url = `${API_CONFIG.BASE_URL}${API_ENDPOINTS.FAVOURITES}?customerId=${custId}&productId=${productId}`;

        console.log('Toggling wishlist:', url);
        const response = await fetch(url, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            credentials: 'include',
        });

        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }

        const data = await response.json();
        console.log('Toggle result:', data);


        return {
            success: true,
            added: data.added
        };
    } catch (error) {
        console.error('Failed to toggle favourites via API:', error);
        console.error('Falling back to localStorage');
        return toggleFavourite(item, customerId, true);
    }
};
