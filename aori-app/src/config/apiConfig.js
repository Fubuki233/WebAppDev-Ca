/**
 * API configuration module for managing API endpoints and settings.
 * 
 * @author Yunhe
 * @date 2025-10-08
 * @version 1.1
 * 
 * @author Yunhe
 * @date 2025-10-11
 * @version 1.2 - Added collection display endpoint
 */
const DEV_CONFIG = {
    BASE_URL: 'http://localhost:8080/api',
    USE_MOCK: false,  // Set to false to use real backend
};



const config = DEV_CONFIG;

export const API_ENDPOINTS = {
    PRODUCTS: '/products',
    PRODUCT_BY_ID: (id) => `/products/${id}`,
    PRODUCTS_BY_CATEGORY: (category) => `/products/category/${category}`,
    PRODUCT_SEARCH: '/products/search',
    COLLECTION_DISPLAY: '/products/collectionDisplay',

    CATEGORIES: '/categories',

    FILTERS: '/filters',

    USER_LOGIN: '/auth/login',
    USER_REGISTER: '/auth/register',
    USER_LOGOUT: '/auth/logout',
    USER_UUID: '/auth/uuid',
    USER_PROFILE: '/account/profile',
    USER_PROFILE_EDIT: '/account/profile/edit',
    USER_ADDRESSES: '/account/addresses',

    CUSTOMER: '/customers',
    CUSTOMER_REGISTER: '/customers',

    CART: '/cart',
    CART_ADD: '/cart/items',
    CART_CHECKOUT: '/cart/checkout',

    FAVOURITES: '/wishlist',
    FAVOURITES_ADD: '/wishlist',
    FAVOURITES_REMOVE: '/wishlist',

    ORDERS: '/order',
    ORDER_CREATE: '/order/create',

};

export const API_CONFIG = {
    ...config,
    TIMEOUT: 10000,
    RETRY_TIMES: 3,
};

export default API_CONFIG;
