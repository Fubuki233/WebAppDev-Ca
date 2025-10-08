/**
 * API configuration module for managing API endpoints and settings.
 * 
 * @author Yunhe
 * @date 2025-10-08
 * @version 1.0
 */
const DEV_CONFIG = {
    BASE_URL: 'http://localhost:8080/api',
    USE_MOCK: true,
};



const config = DEV_CONFIG;

export const API_ENDPOINTS = {
    PRODUCTS: '/products',
    PRODUCT_BY_ID: (id) => `/products/${id}`,
    PRODUCTS_BY_CATEGORY: (category) => `/products/category/${category}`,
    PRODUCT_SEARCH: '/products/search',

    CATEGORIES: '/categories',

    FILTERS: '/filters',

    USER_LOGIN: '/auth/login',
    USER_REGISTER: '/auth/register',
    USER_PROFILE: '/user/profile',

    CART: '/cart',
    CART_ADD: '/cart/add',
    CART_REMOVE: '/cart/remove',

    FAVOURITES: '/favourites',
    FAVOURITES_ADD: '/favourites/add',
    FAVOURITES_REMOVE: '/favourites/remove',

    ORDERS: '/orders',
    ORDER_CREATE: '/orders/create',
};

export const API_CONFIG = {
    ...config,
    TIMEOUT: 10000,
    RETRY_TIMES: 3,
};

export default API_CONFIG;
