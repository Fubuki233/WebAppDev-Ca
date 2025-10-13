/**
 *  HomePage.jsx
 * 
 * @author Yunhe
 * @date 2025-10-08
 * @version 1.1
 * 
 * @date 2025-10-11
 * @version 1.2 - Integrated ProductCarousel for displaying collections and new products
 */
import React, { useState, useEffect, useCallback } from 'react';
import Navbar from './Navbar';
import ProductCarousel from './ProductCarousel';
import RecommendationsSection from './RecommendationsSection';
import { fetchProducts, fetchCollectionDisplay } from '../api/productApi';
import '../styles/HomePage.css';

const HomePage = () => {

    // ----Status-----
    const [products, setProducts] = useState([]);
    const [loading, setLoading] = useState(true);
    const [searching, setSearching] = useState(false);
    const [searchQuery, setSearchQuery] = useState('');
    const [searchMessage, setSearchMessage] = useState('');

    // ---- 加载/搜索逻辑 ----
    const loadProducts = useCallback(async (query = '') => {
        const trimmed = query.trim();
        const isSearch = trimmed.length > 0;
        isSearch ? setSearching(true) : setLoading(true);
        setSearchMessage('');

        try {
            const result = await fetchProducts(isSearch ? { search: trimmed, limit: 9 } : {});
            const list = result?.products ?? [];
            setProducts(list);

            if (isSearch && list.length === 0) {
                setSearchMessage(`没有找到与 “${trimmed}” 匹配的商品。`);
            }
        } catch (error) {
            console.error('Error loading products:', error);
            setProducts([]);
            setSearchMessage(isSearch ? '搜索失败，请稍后再试。' : '加载商品失败。');
        } finally {
            isSearch ? setSearching(false) : setLoading(false);
        }
    }, []);

    // ---- 初次加载 ----
    useEffect(() => {
        loadProducts();
    }, [loadProducts]);

    // ---- 处理搜索事件 ----
    const handleSearch = (query) => {
        setSearchQuery(query);
        if (!query.trim()) {
            loadProducts();
            return;
        }
        loadProducts(query);
    };

    // ---- 页面渲染 ----
    return (
        <div className="homepage">
            <Navbar />
            <div className="main-content">
                <Sidebar onSearch={handleSearch} initialQuery={searchQuery} />

                {loading || searching ? (
                    <div className="home-content-placeholder">加载中…</div>
                ) : searchMessage ? (
                    <div className="home-content-placeholder">
                        <p>{searchMessage}</p>
                        <button
                            type="button"
                            onClick={() => {
                                setSearchQuery('');
                                loadProducts();
                            }}
                        >
                            View all products
                        </button>
                    </div>
                ) : (
                    <ProductCarousel products={products} />
                )}
            </div>
        </div>
    );
};

export default HomePage;
