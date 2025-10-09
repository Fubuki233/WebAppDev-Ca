/**
 *  HomePage.jsx
 * 
 * @author Yunhe
 * @date 2025-10-08
 * @version 1.1
 */
import React, { useState, useEffect } from 'react';
import Navbar from './Navbar';
import Sidebar from './Sidebar';
import ProductCarousel from './ProductCarousel';
import { fetchProducts } from '../api/productApi';
import '../styles/HomePage.css';

const HomePage = () => {
    const [products, setProducts] = useState([]);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        const loadProducts = async () => {
            try {
                const result = await fetchProducts();
                setProducts(result.products || []);
                setLoading(false);
            } catch (error) {
                console.error('Error loading products:', error);
                setLoading(false);
            }
        };

        loadProducts();
    }, []);

    if (loading) {
        return <div className="loading">Loading...</div>;
    }

    return (
        <div className="homepage">
            <Navbar />
            <div className="main-content">
                <Sidebar />
                <ProductCarousel products={products} />
            </div>
        </div>
    );
};

export default HomePage;
