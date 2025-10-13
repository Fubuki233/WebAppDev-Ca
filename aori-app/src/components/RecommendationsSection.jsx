/**
 * RecommendationsSection Component
 * Displays personalized product recommendations based on user purchase history
 * 
 * Features:
 * - Shows personalized recommendations for logged-in users
 * - Shows popular products for guests
 * - Responsive grid layout
 * - Loading states
 * - Error handling
 * 
 * @author Yunhe
 * @date 2025-10-12
 * @version 1.0
 */

import React, { useState, useEffect } from 'react';
import { getRecommendations, getCartRecommendations, getViewHistoryRecommendations } from '../api/recommendationApi';
import ProductCard from './ProductCard';
import '../styles/RecommendationsSection.css';

const RecommendationsSection = ({
    limit = 12,
    title = 'Recommended for You',
    useCartRecommendations = false,
    useViewHistoryRecommendations = false
}) => {
    const [recommendations, setRecommendations] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    useEffect(() => {
        const fetchRecommendations = async () => {
            setLoading(true);
            setError(null);

            try {
                let products;
                if (useViewHistoryRecommendations) {
                    products = await getViewHistoryRecommendations(limit);
                } else if (useCartRecommendations) {
                    products = await getCartRecommendations(limit);
                } else {
                    products = await getRecommendations(limit);
                }

                if (products && products.length > 0) {
                    setRecommendations(products);
                } else {
                    setError('No recommendations available at the moment.');
                }
            } catch (err) {
                console.error('Failed to fetch recommendations:', err);
                setError('Failed to load recommendations, please try again later.');
            } finally {
                setLoading(false);
            }
        };

        fetchRecommendations();
    }, [limit, useCartRecommendations, useViewHistoryRecommendations]);

    if (loading) {
        return (
            <section className="recommendations-section">
                <h2>{title}</h2>
                <div className="products-grid">
                    {Array(limit).fill(0).map((_, index) => (
                        <div key={index} className="product-card-skeleton">
                            <div className="skeleton-image"></div>
                            <div className="skeleton-text"></div>
                            <div className="skeleton-text short"></div>
                        </div>
                    ))}
                </div>
            </section>
        );
    }

    if (error) {
        return (
            <section className="recommendations-section">
                <h2>{title}</h2>
                <div className="recommendations-error">
                    <p>{error}</p>
                </div>
            </section>
        );
    }

    if (recommendations.length === 0) {
        return null;
    }

    return (
        <section className="recommendations-section">
            <h2>{title}</h2>
            <div className="products-grid">
                {recommendations.map(product => (
                    <ProductCard
                        key={product.productId}
                        product={product}
                    />
                ))}
            </div>
        </section>
    );
};

export default RecommendationsSection;
