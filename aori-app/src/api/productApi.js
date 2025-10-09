/**
 * Product API module for managing product data and interactions.
 * 
 * @author Yunhe
 * @date 2025-10-08
 * @version 1.0
 */
import API_CONFIG, { API_ENDPOINTS } from '../config/apiConfig';

const mockProducts = [
    {
        id: 1,
        name: 'Abstract Print Shirt',
        type: 'Cotton T Shirt',
        category: 't-shirts',
        colors: ['#F5F5DC', '#808080', '#000000', '#87CEEB', '#FFFFFF', '#9B9BFF'],
        availableColors: 6,
        image: 'https://images.unsplash.com/photo-1583743814966-8936f5b7be1a?w=400&h=600&fit=crop',
        images: [
            'https://images.unsplash.com/photo-1583743814966-8936f5b7be1a?w=600&h=800&fit=crop',
            'https://images.unsplash.com/photo-1622445275576-721325755b1d?w=600&h=800&fit=crop',
            'https://images.unsplash.com/photo-1562157873-818bc0726f68?w=600&h=800&fit=crop',
            'https://images.unsplash.com/photo-1618354691373-d851c5c3a990?w=600&h=800&fit=crop',
            'https://images.unsplash.com/photo-1521572163474-6864f9cf17ab?w=600&h=800&fit=crop'
        ],
        price: 99,
        inStock: true,
        size: ['XS', 'S', 'M', 'L', 'XL', '2X'],
        tags: ['new', 'best-seller'],
        rating: 4.5,
        description: 'Relaxed-fit shirt. Camp collar and short sleeves. Button-up front.',
        details: [
            '100% cotton',
            'Relaxed fit',
            'Machine washable',
            'Imported',
            'Model is 6\'1" and wearing size M'
        ]
    },
    {
        id: 2,
        name: 'Basic Heavy Weight T-Shirt',
        type: 'Crewneck T-Shirt',
        category: 't-shirts',
        colors: ['#000000'],
        availableColors: 6,
        image: 'https://images.unsplash.com/photo-1583743814966-8936f5b7be1a?w=400&h=600&fit=crop',
        price: 199,
        inStock: true,
        size: ['S', 'M', 'L', 'XL', '2X'],
        tags: ['new'],
        rating: 4.8
    },
    {
        id: 3,
        name: 'Full Sleeve Zipper',
        type: 'Cotton T Shirt',
        category: 'shirts',
        colors: ['#2F4F4F', '#228B22'],
        availableColors: 2,
        image: 'https://images.unsplash.com/photo-1602810318383-e386cc2a3ccf?w=400&h=600&fit=crop',
        price: 199,
        inStock: true,
        size: ['M', 'L', 'XL'],
        tags: ['new'],
        rating: 4.3
    },
    {
        id: 4,
        name: 'Oversized Graphic Tee',
        type: 'Cotton T Shirt',
        category: 't-shirts',
        colors: ['#8B4513', '#000000'],
        availableColors: 2,
        image: 'https://images.unsplash.com/photo-1618354691373-d851c5c3a990?w=400&h=600&fit=crop',
        price: 199,
        inStock: false,
        size: ['S', 'M', 'L'],
        tags: [],
        rating: 4.6
    },
    {
        id: 5,
        name: 'Classic Polo Shirt',
        type: 'Polo Shirt',
        category: 'polo-shirts',
        colors: ['#000000', '#FFFFFF', '#000080'],
        availableColors: 3,
        image: 'https://images.unsplash.com/photo-1586790170083-2f9ceadc732d?w=400&h=600&fit=crop',
        price: 249,
        inStock: true,
        size: ['XS', 'S', 'M', 'L', 'XL'],
        tags: ['best-seller'],
        rating: 4.7
    },
    {
        id: 6,
        name: 'Slim Fit Denim Shirt',
        type: 'Denim Shirt',
        category: 'shirts',
        colors: ['#4682B4', '#000000'],
        availableColors: 2,
        image: 'https://images.unsplash.com/photo-1603252109303-2751441dd157?w=400&h=600&fit=crop',
        price: 299,
        inStock: true,
        size: ['S', 'M', 'L', 'XL'],
        tags: ['new'],
        rating: 4.4
    },
    {
        id: 7,
        name: 'Vintage Washed Tee',
        type: 'Cotton T Shirt',
        category: 't-shirts',
        colors: ['#D3D3D3', '#696969'],
        availableColors: 2,
        image: 'https://images.unsplash.com/photo-1622445275576-721325755b1d?w=400&h=600&fit=crop',
        price: 179,
        inStock: true,
        size: ['M', 'L', 'XL', '2X'],
        tags: [],
        rating: 4.2
    },
    {
        id: 8,
        name: 'Casual Linen Shirt',
        type: 'Linen Shirt',
        category: 'shirts',
        colors: ['#F5F5DC', '#FFFFFF'],
        availableColors: 2,
        image: 'https://images.unsplash.com/photo-1596755094514-f87e34085b2c?w=400&h=600&fit=crop',
        price: 349,
        inStock: true,
        size: ['S', 'M', 'L'],
        tags: ['new', 'best-seller'],
        rating: 4.9
    },
    {
        id: 9,
        name: 'Striped Cotton Tee',
        type: 'Cotton T Shirt',
        category: 't-shirts',
        colors: ['#000080', '#FF0000'],
        availableColors: 2,
        image: 'https://images.unsplash.com/photo-1562157873-818bc0726f68?w=400&h=600&fit=crop',
        price: 189,
        inStock: false,
        size: ['XS', 'S', 'M', 'L'],
        tags: [],
        rating: 4.1
    }
];

const delay = (ms) => new Promise(resolve => setTimeout(resolve, ms));

/**
 * Apply client-side filters to products.
 * Used for filters not supported by backend API.
 */
const applyClientSideFilters = (products, filters) => {
    let filtered = [...products];

    // Color filter
    if (filters.colors && Array.isArray(filters.colors) && filters.colors.length > 0) {
        filtered = filtered.filter(p =>
            p.colors && Array.isArray(p.colors) &&
            filters.colors.some(filterColor => p.colors.includes(filterColor))
        );
    }

    // Collections filter
    if (filters.collections && Array.isArray(filters.collections) && filters.collections.length > 0) {
        filtered = filtered.filter(p =>
            p.collection && filters.collections.includes(p.collection)
        );
    }

    // Tags filter
    if (filters.tags && Array.isArray(filters.tags) && filters.tags.length > 0) {
        filtered = filtered.filter(p =>
            p.tags && Array.isArray(p.tags) &&
            filters.tags.some(filterTag => p.tags.includes(filterTag))
        );
    }

    // Availability filter (inStock)
    if (filters.inStock !== undefined) {
        filtered = filtered.filter(p => p.inStock === filters.inStock);
    }

    // Price range filter
    if (filters.priceMin !== undefined && filters.priceMin !== null && filters.priceMin !== '') {
        const minPrice = parseFloat(filters.priceMin);
        if (!isNaN(minPrice)) {
            filtered = filtered.filter(p => p.price && p.price >= minPrice);
        }
    }

    if (filters.priceMax !== undefined && filters.priceMax !== null && filters.priceMax !== '') {
        const maxPrice = parseFloat(filters.priceMax);
        if (!isNaN(maxPrice)) {
            filtered = filtered.filter(p => p.price && p.price <= maxPrice);
        }
    }

    // Rating filter (show products with rating >= selected rating)
    if (filters.rating && filters.rating > 0) {
        filtered = filtered.filter(p => p.rating && p.rating >= filters.rating);
    }

    return filtered;
};

/**
 * Transform backend product data to frontend format.
 * Backend fields:
 * - productId (String, UUID)
 * - productCode (String)
 * - productName (String)
 * - description (TEXT)
 * - categoryId (String, UUID)
 * - collection (String)
 * - material (String)
 * - season (Enum: Spring/Summer/Autumn/Winter/All_Season)
 * - careInstructions (TEXT)
 * - colors (JSON string: "[\"#000000\", \"#FFFFFF\"]")
 * - image (String, URL or path)
 * - price (Short, integer)
 * - inStock (String: "true" or "false")
 * - size (JSON string: "[\"XS\", \"S\", \"M\", \"L\", \"XL\"]")
 * - rating (Float)
 * - tags (String, comma-separated: "new,best-seller")
 * - category (Object with categoryName, categoryCode, etc.)
 * - createdAt, updatedAt (LocalDateTime)
 */
const transformBackendProduct = (item) => {
    if (!item) return null;

    try {
        // Parse JSON fields safely
        let colors = [];
        if (item.colors) {
            try {
                colors = typeof item.colors === 'string' ? JSON.parse(item.colors) : item.colors;
            } catch (e) {
                console.warn('Failed to parse colors:', item.colors);
                colors = [];
            }
        }

        let sizes = [];
        if (item.size) {
            try {
                sizes = typeof item.size === 'string' ? JSON.parse(item.size) : item.size;
            } catch (e) {
                console.warn('Failed to parse size:', item.size);
                sizes = [];
            }
        }

        // Parse tags
        let tags = [];
        if (item.tags) {
            if (typeof item.tags === 'string') {
                tags = item.tags.split(',').map(t => t.trim()).filter(t => t);
            } else if (Array.isArray(item.tags)) {
                tags = item.tags;
            }
        }

        // Determine category - prefer slug from backend
        let categorySlug = 'other';
        let categoryDisplayName = 'Other';

        if (item.category) {
            if (typeof item.category === 'object') {
                // Backend returns Category object
                categorySlug = item.category.slug || item.category.categoryName?.toLowerCase().replace(/\s+/g, '-') || 'other';
                categoryDisplayName = item.category.categoryName || 'Other';
            } else if (typeof item.category === 'string') {
                // Legacy: category is just a string
                categorySlug = item.category.toLowerCase().replace(/\s+/g, '-');
                categoryDisplayName = item.category;
            }
        }

        return {
            // Frontend fields
            id: item.productId || item.id,
            productId: item.productId || item.id,
            name: item.productName || item.name,
            productName: item.productName || item.name,
            productCode: item.productCode,

            // Type field (use collection or material as fallback)
            type: item.collection || item.material || item.description || '',

            // Category - use slug for filtering
            category: categorySlug,
            categoryName: categoryDisplayName,
            categoryId: item.categoryId,
            categoryData: item.category,

            // Colors
            colors: colors,
            availableColors: colors.length,

            // Images
            image: item.image || '',
            images: item.images || (item.image ? [item.image] : []),

            // Price
            price: item.price || 0,

            // Stock status
            inStock: item.stockQuantity > 0,

            // Sizes
            size: sizes,

            // Tags
            tags: tags,

            // Rating
            rating: item.rating || 0,

            // Description and details
            description: item.description || '',
            material: item.material || '',
            season: item.season || '',
            collection: item.collection || '',
            careInstructions: item.careInstructions || '',

            // Build details array
            details: [
                item.material ? `Material: ${item.material}` : '',
                item.season ? `Season: ${item.season}` : '',
                item.collection ? `Collection: ${item.collection}` : '',
                item.careInstructions || '',
            ].filter(d => d),

            // Timestamps
            createdAt: item.createdAt,
            updatedAt: item.updatedAt,
        };
    } catch (error) {
        console.error('Error transforming product:', error, item);
        return null;
    }
};

export const fetchProducts = async (filters = {}, useMock = false) => {
    if (useMock) {
        await delay(500);
        let filtered = [...mockProducts];

        if (filters.category && filters.category !== 'all') {
            filtered = filtered.filter(p => p.category === filters.category);
        }

        if (filters.size) {
            filtered = filtered.filter(p => p.size.includes(filters.size));
        }

        if (filters.inStock !== undefined) {
            filtered = filtered.filter(p => p.inStock === filters.inStock);
        }

        if (filters.priceMin !== undefined) {
            filtered = filtered.filter(p => p.price >= filters.priceMin);
        }

        if (filters.priceMax !== undefined) {
            filtered = filtered.filter(p => p.price <= filters.priceMax);
        }

        if (filters.tags && filters.tags.length > 0) {
            filtered = filtered.filter(p =>
                filters.tags.some(tag => p.tags.includes(tag))
            );
        }

        if (filters.search) {
            const search = filters.search.toLowerCase();
            filtered = filtered.filter(p =>
                p.name.toLowerCase().includes(search) ||
                p.type.toLowerCase().includes(search)
            );
        }
        const page = filters.page || 1;
        const limit = filters.limit || 12;
        const start = (page - 1) * limit;
        const end = start + limit;

        return {
            products: filtered.slice(start, end),
            total: filtered.length,
            page,
            totalPages: Math.ceil(filtered.length / limit),
        };
    }

    try {
        const params = new URLSearchParams();

        if (filters.category && filters.category !== 'all') params.append('category', filters.category);
        if (filters.size) params.append('size', filters.size);
        if (filters.inStock !== undefined) params.append('inStock', filters.inStock);
        if (filters.priceMin !== undefined) params.append('priceMin', filters.priceMin);
        if (filters.priceMax !== undefined) params.append('priceMax', filters.priceMax);
        if (filters.tags) params.append('tags', filters.tags.join(','));
        if (filters.search) params.append('search', filters.search);
        if (filters.page) params.append('page', filters.page);
        if (filters.limit) params.append('limit', filters.limit);

        const url = `${API_CONFIG.BASE_URL}${API_ENDPOINTS.PRODUCTS}?${params.toString()}`;
        console.log('Fetching products from:', url);

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
        console.log('Fetched data from API:', data);

        // Backend returns Optional<List<Product>>, which resolves to array directly
        if (Array.isArray(data)) {
            let transformedProducts = data.map(transformBackendProduct).filter(p => p !== null);

            console.log('[productApi] Transformed products:', transformedProducts.length);

            // Apply client-side filtering for features not supported by backend
            transformedProducts = applyClientSideFilters(transformedProducts, filters);

            const page = filters.page || 1;
            const limit = filters.limit || 12;

            return {
                products: transformedProducts,
                total: transformedProducts.length,
                page: page,
                totalPages: Math.ceil(transformedProducts.length / limit),
            };
        } else if (data.products) {
            // If backend returns object with products array
            return {
                ...data,
                products: data.products.map(transformBackendProduct).filter(p => p !== null),
            };
        } else {
            return {
                products: [],
                total: 0,
                page: 1,
                totalPages: 0,
            };
        }
    } catch (error) {
        console.error('Error fetching products from API:', error);
        console.error('Falling back to mock data');
        return {
            products: mockProducts.slice(0, 12),
            total: mockProducts.length,
            page: 1,
            totalPages: 1,
        };
    }
};

export const fetchProductsByCategory = async (category, useMock = API_CONFIG.USE_MOCK) => {
    return await fetchProducts({ category }, useMock);
};

export const fetchProductById = async (id, useMock = false) => {
    if (useMock) {
        await delay(300);
        return mockProducts.find(product =>
            product.id === id ||
            product.id === parseInt(id) ||
            product.productId === id
        );
    }

    try {
        const url = `${API_CONFIG.BASE_URL}${API_ENDPOINTS.PRODUCT_BY_ID(id)}`;
        console.log('Fetching product by ID from API:', id);

        const response = await fetch(url, {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json',
            },
            credentials: 'include', // Include session cookies
        });

        // Handle authentication required
        if (response.status === 401) {
            try {
                const errorData = await response.json();
                if (errorData.redirectTo) {
                    console.log('Authentication required, redirecting to:', errorData.redirectTo);
                    window.location.href = errorData.redirectTo;
                    return null;
                }
            } catch (e) {
                // If response is not JSON, just redirect to login
                console.log('Authentication required, redirecting to login');
                window.location.hash = '#login';
                return null;
            }
        }

        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }

        const data = await response.json();
        console.log('Fetched product by ID from API:', data);

        return transformBackendProduct(data);
    } catch (error) {
        console.error('Error fetching product by ID from API:', error);
        return mockProducts.find(product =>
            product.id === id ||
            product.id === parseInt(id) ||
            product.productId === id
        );
    }
};

export const searchProducts = async (query, useMock = API_CONFIG.USE_MOCK) => {
    return await fetchProducts({ search: query }, useMock);
};

/**
 * Fetch all categories from backend.
 * Backend returns: Array of Category objects with:
 * - categoryId (String, UUID)
 * - categoryCode (String)
 * - categoryName (String)
 * - broadCategoryId (Enum: Men/Women/Girls/Boys/Unisex)
 * - slug (String)
 */
export const fetchCategories = async (useMock = API_CONFIG.USE_MOCK) => {
    if (useMock) {
        await delay(300);
        const categories = [...new Set(mockProducts.map(p => p.category))];
        return categories.map(cat => ({
            id: cat,
            name: cat.charAt(0).toUpperCase() + cat.slice(1).replace('-', ' '),
            count: mockProducts.filter(p => p.category === cat).length,
        }));
    }

    try {
        const url = `${API_CONFIG.BASE_URL}${API_ENDPOINTS.CATEGORIES}`;
        console.log('Fetching categories from:', url);

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
        console.log('Fetched categories from API:', data);

        // Transform backend category format to frontend format
        if (Array.isArray(data) && data.length > 0) {
            return data.map(cat => {
                // Generate slug if not provided by backend
                const slug = cat.slug || cat.categoryName?.toLowerCase().replace(/\s+/g, '-') || '';

                return {
                    // Backend fields
                    categoryId: cat.categoryId,
                    categoryCode: cat.categoryCode,
                    categoryName: cat.categoryName,
                    broadCategoryId: cat.broadCategoryId,
                    slug: slug,

                    // Frontend compatible fields
                    id: cat.categoryId,
                    name: cat.categoryName,
                    category: slug,
                    count: 0,
                };
            });
        }

        return [];
    } catch (error) {
        console.error('Error fetching categories from API:', error);
        console.error('Falling back to mock categories');
        const categories = [...new Set(mockProducts.map(p => p.category))];
        return categories.map(cat => ({
            id: cat,
            categoryId: cat,
            name: cat.charAt(0).toUpperCase() + cat.slice(1).replace('-', ' '),
            categoryName: cat.charAt(0).toUpperCase() + cat.slice(1).replace('-', ' '),
            slug: cat,
            category: cat,
            count: mockProducts.filter(p => p.category === cat).length,
        }));
    }
};

export const fetchFilters = async (useMock = API_CONFIG.USE_MOCK) => {
    if (useMock) {
        await delay(300);
        return {
            sizes: ['XS', 'S', 'M', 'L', 'XL', '2X'],
            priceRange: {
                min: Math.min(...mockProducts.map(p => p.price)),
                max: Math.max(...mockProducts.map(p => p.price)),
            },
            colors: [...new Set(mockProducts.flatMap(p => p.colors))],
            tags: ['new', 'best-seller'],
        };
    }

    try {
        const url = `${API_CONFIG.BASE_URL}${API_ENDPOINTS.FILTERS}`;
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
        return data;
    } catch (error) {
        console.error('Error fetching filters from API:', error);
        return {
            sizes: ['XS', 'S', 'M', 'L', 'XL', '2X'],
            priceRange: {
                min: Math.min(...mockProducts.map(p => p.price)),
                max: Math.max(...mockProducts.map(p => p.price)),
            },
            colors: [...new Set(mockProducts.flatMap(p => p.colors))],
            tags: ['new', 'best-seller'],
        };
    }
};
