/**
 * CartPage.jsx
 * 
 * @author Yunhe
 * @date 2025-10-08
 * @version 1.2 - Using unified Navbar component
 */

import React, { useState, useEffect } from 'react';
import { getCart, updateCartItem, removeFromCart, getCartTotal } from '../api/cartApi';
import Navbar from './Navbar';
import RecommendationsSection from './RecommendationsSection';
import '../styles/CartPage.css';

const CartPage = () => {
    const [cart, setCart] = useState([]);
    const [agreeToTerms, setAgreeToTerms] = useState(false);

    useEffect(() => {
        loadCart();
    }, []);

    const loadCart = async () => {
        try {
            const cartItems = await getCart();
            setCart(Array.isArray(cartItems) ? cartItems : []);
        } catch (error) {
            console.error('Error loading cart:', error);
            setCart([]);
        }
    };

    const handleQuantityChange = async (index, delta) => {
        const item = cart[index];
        const newQuantity = item.quantity + delta;

        if (newQuantity < 1) return;

        await updateCartItem(index, newQuantity);
        await loadCart();
    };

    const handleRemoveItem = async (index) => {
        await removeFromCart(index);
        await loadCart();
    };

    const handleRefresh = (index) => {
        console.log('Refresh item:', index);
    };

    const handleContinue = () => {
        if (!agreeToTerms) {
            alert('Please agree to the terms and conditions');
            return;
        }
        window.location.hash = '#checkout';
    };

    const subtotal = cart.reduce((sum, item) => sum + (item.price * item.quantity), 0);
    const shipping = 10;
    const total = subtotal + shipping;

    return (
        <div className="cart-page">
            <Navbar />

            <div className="cart-content">
                {/* Tabs */}
                <div className="cart-tabs">
                    <button
                        className={`cart-tab active`}
                    >
                        SHOPPING BAG
                    </button>
                </div>

                <div className="cart-main">
                    <div className="cart-items">
                        {cart.length === 0 && (
                            <div className="empty-cart">
                                <p>Your shopping bag is empty</p>
                                <button onClick={() => window.location.hash = '#products'}>
                                    Continue Shopping
                                </button>
                            </div>
                        )}

                        {cart.map((item, index) => (
                            <div key={index} className="cart-item">
                                <div className="item-image">
                                    <img src={item.image || '/placeholder-product.jpg'} alt={item.name} />
                                </div>

                                <div className="item-details">
                                    <div className="item-header">
                                        <div className="item-info">
                                            <h3>Cotton T Shirt</h3>
                                            <p className="item-name">{item.name}</p>
                                        </div>
                                        <button
                                            className="remove-button"
                                            onClick={() => handleRemoveItem(index)}
                                            title="Remove item"
                                        >
                                            ×
                                        </button>
                                    </div>

                                    <div className="item-options">
                                        <div className="size-color-group">
                                            <div className="size-selector">
                                                <span>{item.size || 'L'}</span>
                                            </div>

                                            <div
                                                className="color-selector"
                                                style={{ backgroundColor: item.color || '#000' }}
                                            ></div>
                                        </div>

                                        <div className="quantity-controls">
                                            <button
                                                onClick={() => handleQuantityChange(index, 1)}
                                            >
                                                +
                                            </button>
                                            <span className="quantity">{item.quantity}</span>
                                            <button
                                                onClick={() => handleQuantityChange(index, -1)}
                                            >
                                                −
                                            </button>
                                        </div>
                                    </div>

                                    <div className="item-footer">
                                        <div className="item-price">
                                            <span className="price">${item.price}</span>
                                        </div>

                                        <div className="item-actions">
                                            <button
                                                className="action-button"
                                                onClick={() => handleRefresh(index)}
                                                title="Refresh"
                                            >
                                                <svg width="18" height="18" viewBox="0 0 24 24" fill="none">
                                                    <path d="M23 4v6h-6M1 20v-6h6M3.51 9a9 9 0 0 1 14.85-3.36L23 10M1 14l4.64 4.36A9 9 0 0 0 20.49 15" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" />
                                                </svg>
                                            </button>

                                            <button className="action-button" title="Add to favorites">
                                                <svg width="18" height="18" viewBox="0 0 24 24" fill="none">
                                                    <path d="M20.84 4.61a5.5 5.5 0 0 0-7.78 0L12 5.67l-1.06-1.06a5.5 5.5 0 0 0-7.78 7.78l1.06 1.06L12 21.23l7.78-7.78 1.06-1.06a5.5 5.5 0 0 0 0-7.78z" stroke="currentColor" strokeWidth="2" />
                                                </svg>
                                            </button>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        ))}
                    </div>

                    {cart.length > 0 && (
                        <div className="order-summary">
                            <h2>ORDER SUMMARY</h2>

                            <div className="summary-row">
                                <span>Subtotal</span>
                                <span>${subtotal}</span>
                            </div>

                            <div className="summary-row">
                                <span>Shipping</span>
                                <span>${shipping}</span>
                            </div>

                            <div className="summary-total">
                                <span>TOTAL</span>
                                <span className="total-price">${total}</span>
                            </div>

                            <div className="tax-note">(TAX INCL.)</div>

                            <div className="terms-checkbox">
                                <input
                                    type="checkbox"
                                    id="terms"
                                    checked={agreeToTerms}
                                    onChange={(e) => setAgreeToTerms(e.target.checked)}
                                />
                                <label htmlFor="terms">
                                    I AGREE TO THE TERMS AND CONDITIONS
                                </label>
                            </div>

                            <button
                                className="continue-button"
                                onClick={handleContinue}
                                disabled={!agreeToTerms}
                            >
                                CONTINUE
                            </button>
                        </div>
                    )}
                </div>

                {/* Recommendations Section - Based on Cart Contents */}
                <RecommendationsSection
                    limit={cart.length > 0 ? 6 : 12}
                    title={cart.length > 0 ? "Pair with Your Cart" : "Recommended for You"}
                    useCartRecommendations={true}
                />
            </div>
        </div>
    );
};

export default CartPage;
