/**
 * CartPage.jsx
 * 
 * @author Yunhe
 * @date 2025-10-08
 * @version 1.2 - Using unified Navbar component
 * 
 * @date 2025-10-15
 * @version 1.3 - Added order confirmation and return request features
 */

import React, { useState, useEffect } from 'react';
import { getCart, removeFromCart, getCartTotal } from '../api/cartApi';
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

    const handleRemoveItem = async (index) => {
        await removeFromCart(index);
        await loadCart();
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
                                    </div>

                                    <div className="item-footer">
                                        <div className="item-info-display">
                                            <span className="quantity-display">Quantity: {item.quantity}</span>
                                            <span className="price">${item.price}</span>
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
