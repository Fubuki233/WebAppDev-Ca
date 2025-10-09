/**
 *  ProductsPage.jsx
 * 
 * @author Yunhe
 * @date 2025-10-08
 * @version 1.1
 */
import React, { useState, useEffect } from 'react';
import Navbar from './Navbar';
import ProductFilters from './ProductFilters';
import ProductCard from './ProductCard';
import { fetchProducts, fetchCategories } from '../api/productApi';
import '../styles/ProductsPage.css';

const ProductsPage = () => {
    const [products, setProducts] = useState([]);
    const [categories, setCategories] = useState([]);
    const [loading, setLoading] = useState(true);
    const [filters, setFilters] = useState({});
    const [searchQuery, setSearchQuery] = useState('');
    const [activeCategory, setActiveCategory] = useState('all');
    const [totalProducts, setTotalProducts] = useState(0);
    const [categoryTabs, setCategoryTabs] = useState([]);
    const [availableColors, setAvailableColors] = useState([]);
    const [availableCollections, setAvailableCollections] = useState([]);
    const [availableTags, setAvailableTags] = useState([]);

    useEffect(() => {
        loadCategories();
    }, []);

    useEffect(() => {
        loadProducts();
    }, [filters]);

    const loadProducts = async () => {
        setLoading(true);
        try {
            const result = await fetchProducts(filters);
            console.log('Loaded products result:', result);
            const productsData = result?.products || [];
            setProducts(productsData);
            setTotalProducts(result?.total || 0);

            // Extract available colors, collections, and tags from products
            extractFilterOptions(productsData);
        } catch (error) {
            console.error('Error loading products:', error);
            setProducts([]);
            setTotalProducts(0);
        } finally {
            setLoading(false);
        }
    };

    const extractFilterOptions = (productsData) => {
        // Extract unique colors
        const colorsSet = new Set();
        productsData.forEach(product => {
            if (product.colors && Array.isArray(product.colors)) {
                product.colors.forEach(color => colorsSet.add(color));
            }
        });
        setAvailableColors(Array.from(colorsSet));

        // Extract unique collections
        const collectionsSet = new Set();
        productsData.forEach(product => {
            if (product.collection) {
                collectionsSet.add(product.collection);
            }
        });
        setAvailableCollections(Array.from(collectionsSet).filter(c => c));

        // Extract unique tags
        const tagsSet = new Set();
        productsData.forEach(product => {
            if (product.tags && Array.isArray(product.tags)) {
                product.tags.forEach(tag => tagsSet.add(tag));
            }
        });
        setAvailableTags(Array.from(tagsSet).filter(t => t));
    };

    const loadCategories = async () => {
        try {
            const cats = await fetchCategories();
            setCategories(cats);

            // Generate category tabs from fetched categories
            if (cats && cats.length > 0) {
                const tabs = cats.map(cat => ({
                    id: cat.slug || cat.category || cat.categoryId,
                    label: (cat.categoryName || cat.name || '').toUpperCase(),
                    categoryId: cat.categoryId,
                }));
                setCategoryTabs(tabs);
            }
        } catch (error) {
            console.error('Error loading categories:', error);
        }
    };

    const handleFilterChange = (newFilters) => {
        setFilters(prev => ({ ...prev, ...newFilters }));

        // Sync activeCategory with filter category
        if ('category' in newFilters) {
            setActiveCategory(newFilters.category || 'all');
        }
    };

    const handleSearch = (e) => {
        e.preventDefault();
        setFilters(prev => ({ ...prev, search: searchQuery }));
    };

    const handleCategoryClick = (categoryId) => {
        setActiveCategory(categoryId);
        if (categoryId === 'all') {
            const { category, ...rest } = filters;
            setFilters(rest);
        } else {
            setFilters(prev => ({ ...prev, category: categoryId }));
        }
    };

    return (
        <div className="products-page">
            <Navbar />

            <div className="page-container">
                <div className="breadcrumb">
                    <a href="#home" onClick={(e) => { e.preventDefault(); window.location.hash = ''; }}>Home</a>
                    <span className="separator">/</span>
                    <span className="current">Products</span>
                </div>

                <h1 className="page-title">PRODUCTS</h1>

                <div className="products-layout">
                    <ProductFilters
                        onFilterChange={handleFilterChange}
                        categories={categories}
                        activeCategory={activeCategory}
                        availableColors={availableColors}
                        availableCollections={availableCollections}
                        availableTags={availableTags}
                    />

                    <div className="products-main">
                        <form className="search-bar" onSubmit={handleSearch}>
                            <svg className="search-icon" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                                <circle cx="11" cy="11" r="8"></circle>
                                <path d="m21 21-4.35-4.35"></path>
                            </svg>
                            <input
                                type="text"
                                className="search-input"
                                placeholder="SEARCH"
                                value={searchQuery}
                                onChange={(e) => setSearchQuery(e.target.value)}
                            />
                        </form>

                        <div className="category-tabs">
                            <button
                                className={`category-tab ${activeCategory === 'all' ? 'active' : ''}`}
                                onClick={() => handleCategoryClick('all')}
                            >
                                ALL
                            </button>
                            {categoryTabs.map((tab) => (
                                <button
                                    key={tab.id}
                                    className={`category-tab ${activeCategory === tab.id ? 'active' : ''}`}
                                    onClick={() => handleCategoryClick(tab.id)}
                                >
                                    {tab.label}
                                </button>
                            ))}
                        </div>

                        {loading ? (
                            <div className="loading-container">
                                <div className="loading-spinner"></div>
                                <p>Loading products...</p>
                            </div>
                        ) : (
                            <>
                                <div className="products-count">
                                    Showing {products.length} of {totalProducts} products
                                </div>

                                <div className="products-grid">
                                    {products.map((product) => (
                                        <ProductCard key={product.id} product={product} />
                                    ))}
                                </div>

                                {products.length === 0 && (
                                    <div className="no-products">
                                        <p>No products found matching your criteria.</p>
                                    </div>
                                )}
                            </>
                        )}
                    </div>
                </div>
            </div>
        </div>
    );
};

export default ProductsPage;
