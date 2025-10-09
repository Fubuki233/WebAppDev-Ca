/**
 * FavouritesPage.jsx
 * 
 * @author Yunhe
 * @date 2025-10-08
 * @version 1.1
 */
import React, { useState, useEffect } from 'react';
import Navbar from './Navbar';
import '../styles/FavouritesPage.css';

const FavouritesPage = () => {
    const [favourites, setFavourites] = useState([]);

    useEffect(() => {
        loadFavourites();
    }, []);

    const loadFavourites = () => {
        try {
            const saved = localStorage.getItem('aori_favourites');
            if (saved) {
                setFavourites(JSON.parse(saved));
            }
        } catch (error) {
            console.error('Failed to load favourites:', error);
        }
    };

    const removeFromFavourites = (index) => {
        const updated = favourites.filter((_, i) => i !== index);
        setFavourites(updated);
        localStorage.setItem('aori_favourites', JSON.stringify(updated));
    };

    const addToCart = (item) => {
        try {
            const cart = JSON.parse(localStorage.getItem('aori_shopping_cart') || '[]');
            const existingIndex = cart.findIndex(
                cartItem => cartItem.productId === item.productId &&
                    cartItem.size === item.size &&
                    cartItem.color === item.color
            );

            if (existingIndex >= 0) {
                cart[existingIndex].quantity += 1;
            } else {
                cart.push({
                    ...item,
                    quantity: 1
                });
            }

            localStorage.setItem('aori_shopping_cart', JSON.stringify(cart));
            alert('Added to cart!');
        } catch (error) {
            console.error('Failed to add to cart:', error);
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

                {favourites.length === 0 ? (
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
