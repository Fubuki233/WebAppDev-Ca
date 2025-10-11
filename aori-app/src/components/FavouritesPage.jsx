/**
 * FavouritesPage.jsx
 * 
 * @author Yunhe
 * @date 2025-10-08
 * @version 1.2 - Using favouritesApi for backend integration
 */
import React, { useState, useEffect } from 'react';
import Navbar from './Navbar';
import { getFavourites, removeFromFavourites as removeFromFavouritesApi } from '../api/favouritesApi';
import { addToCart as addToCartApi } from '../api/cartApi';
import '../styles/FavouritesPage.css';

const FavouritesPage = () => {
    const [favourites, setFavourites] = useState([]);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        loadFavourites();
    }, []);

    const loadFavourites = async () => {
        try {
            setLoading(true);
            const items = await getFavourites();
            // Check if user is a guest
            if (items && items.stayAsGuest) {
                setFavourites([]);
            } else {
                setFavourites(Array.isArray(items) ? items : []);
            }
        } catch (error) {
            console.error('Failed to load favourites:', error);
            setFavourites([]);
        } finally {
            setLoading(false);
        }
    };

    const removeFromFavourites = async (index) => {
        try {
            const item = favourites[index];
            if (!item) return;

            const result = await removeFromFavouritesApi(item.productId);
            if (result.success || result.success === undefined) {
                await loadFavourites(); // Reload to get fresh data
            }
        } catch (error) {
            console.error('Failed to remove from favourites:', error);
        }
    };

    const addToCart = async (item) => {
        try {
            const result = await addToCartApi({
                productId: item.productId || item.id,
                quantity: 1
            });

            if (result.success) {
                alert('Added to cart!');
            } else {
                alert('Failed to add to cart');
            }
        } catch (error) {
            console.error('Failed to add to cart:', error);
            alert('Failed to add to cart');
        }
    };

    const goToProduct = (productId) => {
        window.location.hash = `#product/${productId}`;
    };

    return (
        <div className="favourites-page">
            <Navbar />

            <div className="favourites-container">
                <div className="favourites-header">
                    <h1>MY FAVOURITES</h1>
                    <p className="favourites-count">{favourites.length} {favourites.length === 1 ? 'item' : 'items'}</p>
                </div>

                {loading ? (
                    <div className="loading-favourites">
                        <p>Loading your favourites...</p>
                    </div>
                ) : favourites.length === 0 ? (
                    <div className="empty-favourites">
                        <div className="empty-icon">
                            <svg width="80" height="80" viewBox="0 0 24 24" fill="none">
                                <path d="M20.84 4.61a5.5 5.5 0 0 0-7.78 0L12 5.67l-1.06-1.06a5.5 5.5 0 0 0-7.78 7.78l1.06 1.06L12 21.23l7.78-7.78 1.06-1.06a5.5 5.5 0 0 0 0-7.78z"
                                    stroke="#ddd"
                                    strokeWidth="2"
                                    strokeLinecap="round"
                                    strokeLinejoin="round"
                                />
                            </svg>
                        </div>
                        <h2>Your Favourites List is Empty</h2>
                        <p>Start adding items you love to your favourites!</p>
                        <button
                            className="browse-button"
                            onClick={() => window.location.hash = '#products'}
                        >
                            Browse Products
                        </button>
                    </div>
                ) : (
                    <div className="favourites-grid">
                        {favourites.map((item, index) => (
                            <div key={index} className="favourite-card">
                                <div className="favourite-image-container">
                                    <img
                                        src={item.image || '/placeholder-product.jpg'}
                                        alt={item.name}
                                        onClick={() => goToProduct(item.productId || 1)}
                                    />
                                    <button
                                        className="remove-favourite-btn"
                                        onClick={() => removeFromFavourites(index)}
                                        title="Remove from favourites"
                                    >
                                        <svg width="20" height="20" viewBox="0 0 24 24" fill="currentColor">
                                            <path d="M20.84 4.61a5.5 5.5 0 0 0-7.78 0L12 5.67l-1.06-1.06a5.5 5.5 0 0 0-7.78 7.78l1.06 1.06L12 21.23l7.78-7.78 1.06-1.06a5.5 5.5 0 0 0 0-7.78z" />
                                        </svg>
                                    </button>
                                </div>

                                <div className="favourite-info">
                                    <div className="favourite-details">
                                        <h3 onClick={() => goToProduct(item.productId || 1)}>
                                            {item.name}
                                        </h3>
                                        <p className="favourite-category">Cotton T-Shirt</p>

                                        {item.size && (
                                            <div className="favourite-variant">
                                                <span className="variant-label">Size:</span>
                                                <span className="variant-value">{item.size}</span>
                                            </div>
                                        )}

                                        {item.color && (
                                            <div className="favourite-variant">
                                                <span className="variant-label">Color:</span>
                                                <div
                                                    className="color-preview"
                                                    style={{ backgroundColor: item.color }}
                                                ></div>
                                            </div>
                                        )}
                                    </div>

                                    <div className="favourite-actions">
                                        <div className="favourite-price">
                                            ${item.price || '99'}
                                        </div>
                                        <button
                                            className="add-to-cart-btn"
                                            onClick={() => addToCart(item)}
                                        >
                                            Add to Cart
                                        </button>
                                    </div>
                                </div>
                            </div>
                        ))}
                    </div>
                )}
            </div>
        </div>
    );
};

export default FavouritesPage;
