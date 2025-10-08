
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

const transformBackendProduct = (item) => {
    if (!item) return null;

    return {
        id: item.productId || item.id,
        productId: item.productId || item.id,
        name: item.productName || item.name,
        type: item.description || item.type || '',
        category: item.category?.categoryName?.toLowerCase().replace(/\s+/g, '-') ||
            item.category?.toLowerCase().replace(/\s+/g, '-') || 'other',
        colors: typeof item.colors === 'string' ? JSON.parse(item.colors) : (item.colors || []),
        availableColors: typeof item.colors === 'string' ?
            JSON.parse(item.colors).length :
            (item.colors?.length || 0),
        image: item.image,
        images: item.images || (item.image ? [item.image] : []),
        price: item.price || 0,
        inStock: item.inStock === 'true' || item.inStock === true || item.inStock === 1,
        size: typeof item.size === 'string' ? JSON.parse(item.size) : (item.size || []),
        tags: typeof item.tags === 'string' ?
            item.tags.split(',').map(t => t.trim()).filter(t => t) :
            (Array.isArray(item.tags) ? item.tags : []),
        rating: item.rating || 0,
        description: item.description,
        material: item.material,
        season: item.season,
        collection: item.collection,
        careInstructions: item.careInstructions,
        details: item.details || [
            item.material ? `Material: ${item.material}` : '',
            item.careInstructions || '',
            item.season ? `Season: ${item.season}` : '',
        ].filter(d => d),
    };
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

        if (Array.isArray(data)) {
            const page = filters.page || 1;
            const limit = filters.limit || 12;
            return {
                products: data.map(transformBackendProduct).filter(p => p !== null),
                total: data.length,
                page: page,
                totalPages: Math.ceil(data.length / limit),
            };
        } else if (data.products) {
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
        });

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
        console.error('Error fetching categories from API:', error);
        const categories = [...new Set(mockProducts.map(p => p.category))];
        return categories.map(cat => ({
            id: cat,
            name: cat.charAt(0).toUpperCase() + cat.slice(1).replace('-', ' '),
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
