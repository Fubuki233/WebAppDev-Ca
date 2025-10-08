
import API_CONFIG, { API_ENDPOINTS } from '../config/apiConfig';

const FAVOURITES_KEY = 'aori_favourites';


export const getFavourites = async (useMock = API_CONFIG.USE_MOCK) => {
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
        const response = await fetch(`${API_CONFIG.BASE_URL}${API_ENDPOINTS.FAVOURITES}`, {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json',
            },
        });

        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }

        const data = await response.json();
        return data.favourites || [];
    } catch (error) {
        console.error('Failed to get favourites from API:', error);
        const favourites = localStorage.getItem(FAVOURITES_KEY);
        return favourites ? JSON.parse(favourites) : [];
    }
};


export const addToFavourites = async (item, useMock = API_CONFIG.USE_MOCK) => {
    if (useMock) {
        try {
            const favourites = await getFavourites(true);
            const exists = favourites.some(
                fav => fav.productId === item.productId &&
                    fav.size === item.size &&
                    fav.color === item.color
            );

            if (exists) {
                return { success: false, message: 'Item already in favourites' };
            }

            const newItem = {
                ...item,
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
        const response = await fetch(`${API_CONFIG.BASE_URL}${API_ENDPOINTS.FAVOURITES_ADD}`, {
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
        console.error('Failed to add to favourites via API:', error);
        return { success: false, message: 'Failed to add to favourites' };
    }
};

export const removeFromFavourites = async (index, useMock = API_CONFIG.USE_MOCK) => {
    if (useMock) {
        try {
            const favourites = await getFavourites(true);

            if (index >= 0 && index < favourites.length) {
                favourites.splice(index, 1);
                localStorage.setItem(FAVOURITES_KEY, JSON.stringify(favourites));
                return { success: true, message: 'Removed from favourites' };
            }

            return { success: false, message: 'Invalid index' };
        } catch (error) {
            console.error('Failed to remove from favourites:', error);
            return { success: false, message: 'Failed to remove from favourites' };
        }
    }

    try {
        const favourites = await getFavourites(false);
        const item = favourites[index];
        if (!item) {
            return { success: false, message: 'Invalid index' };
        }

        const response = await fetch(`${API_CONFIG.BASE_URL}${API_ENDPOINTS.FAVOURITES_REMOVE}/${item.id}`, {
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
        console.error('Failed to remove from favourites via API:', error);
        return { success: false, message: 'Failed to remove from favourites' };
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
        const params = new URLSearchParams();
        params.append('productId', productId);
        if (size) params.append('size', size);
        if (color) params.append('color', color);

        const response = await fetch(`${API_CONFIG.BASE_URL}${API_ENDPOINTS.FAVOURITES_REMOVE}?${params.toString()}`, {
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
        console.error('Failed to remove from favourites via API:', error);
        return { success: false, message: 'Failed to remove from favourites' };
    }
};


export const isInFavourites = async (productId, size = null, color = null, useMock = API_CONFIG.USE_MOCK) => {
    try {
        const favourites = await getFavourites(useMock);

        return favourites.some(item => {
            if (item.productId !== productId) return false;
            if (size && item.size !== size) return false;
            if (color && item.color !== color) return false;
            return true;
        });
    } catch (error) {
        console.error('Failed to check favourites:', error);
        return false;
    }
};


export const getFavouritesCount = async (useMock = API_CONFIG.USE_MOCK) => {
    try {
        const favourites = await getFavourites(useMock);
        return favourites.length;
    } catch (error) {
        console.error('Failed to get favourites count:', error);
        return 0;
    }
};


export const clearFavourites = async (useMock = API_CONFIG.USE_MOCK) => {
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


export const toggleFavourite = async (item, useMock = API_CONFIG.USE_MOCK) => {
    const inFavourites = await isInFavourites(item.productId, item.size, item.color, useMock);
    if (inFavourites) {
        return await removeFromFavouritesByProduct(item.productId, item.size, item.color, useMock);
    } else {
        return await addToFavourites(item, useMock);
    }
};
