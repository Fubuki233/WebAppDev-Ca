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
import React, { useState, useEffect } from 'react';
import Navbar from './Navbar';
import ProductCarousel from './ProductCarousel';
import RecommendationsSection from './RecommendationsSection';
import { fetchProducts, fetchCollectionDisplay } from '../api/productApi';
import '../styles/HomePage.css';

const HomePage = () => {
    const [products, setProducts] = useState({ collection: [], newThisWeek: [] });
    const [collection, setCollection] = useState('');
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        const loadData = async () => {
            try {
                const currentCollection = await fetchCollectionDisplay();
                setCollection(currentCollection);

                const result = await fetchProducts({ collections: [currentCollection] });
                const collectionProducts = result.products || [];

                const allProductsResult = await fetchProducts();
                const oneWeekAgo = new Date();
                oneWeekAgo.setDate(oneWeekAgo.getDate() - 7);

                const collectionProductIds = new Set(
                    collectionProducts.map(p => p.id || p.productId)
                );

                const newProducts = (allProductsResult.products || [])
                    .filter(product => {
                        const productId = product.id || product.productId;
                        if (collectionProductIds.has(productId)) {
                            return false;
                        }

                        if (product.createdAt) {
                            const createdDate = new Date(product.createdAt);
                            return createdDate >= oneWeekAgo;
                        }
                        return false;
                    })
                    .slice(0, 8);

                console.log('[HomePage] Collection products:', collectionProducts.length);
                console.log('[HomePage] Collection product IDs:', Array.from(collectionProductIds));
                console.log('[HomePage] New products this week (after removing duplicates):', newProducts.length);
                console.log('[HomePage] New products data:', newProducts);

                setProducts({
                    collection: collectionProducts,
                    newThisWeek: newProducts
                });
                setLoading(false);
            } catch (error) {
                console.error('Error loading data:', error);
                setLoading(false);
            }
        };

        loadData();
    }, []);

    if (loading) {
        return <div className="loading">Loading...</div>;
    }

    return (
        <div className="homepage">
            <Navbar />
            <div className="main-content">
                <ProductCarousel
                    products={products.collection || []}
                    newProducts={products.newThisWeek || []}
                    collection={collection}
                />

                {/* Recommendations based on browsing history */}
                <RecommendationsSection
                    limit={12}
                    title="You May Also Like"
                    useViewHistoryRecommendations={true}
                />

                {/* Recommendations based on purchase history */}
                <RecommendationsSection
                    limit={12}
                    title="Picked For You"
                    useCartRecommendations={false}
                />
            </div>
        </div>
    );
};

export default HomePage;
