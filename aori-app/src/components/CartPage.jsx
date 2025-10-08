import React, { useState, useEffect } from 'react';
import { getCart, updateCartItem, removeFromCart, getCartTotal } from '../api/cartApi';
import '../styles/CartPage.css';

const CartPage = () => {
    const [cart, setCart] = useState([]);
    const [activeTab, setActiveTab] = useState('shopping-bag');
    const [agreeToTerms, setAgreeToTerms] = useState(false);

    useEffect(() => {
        loadCart();
    }, []);

    const loadCart = () => {
        const cartItems = getCart();
        setCart(cartItems);
    };

    const handleQuantityChange = (index, delta) => {
        const item = cart[index];
        const newQuantity = item.quantity + delta;

        if (newQuantity < 1) return;

        updateCartItem(index, newQuantity);
        loadCart();
    };

    const handleRemoveItem = (index) => {
        removeFromCart(index);
        loadCart();
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
            <nav className="cart-navbar">
                <div className="cart-navbar-container">
                    <div className="cart-navbar-logo">
                        <a href="#home" onClick={(e) => { e.preventDefault(); window.location.hash = ''; }}>
                            <h1>AORI</h1>
                            <span className="cart-logo-subtitle">-JAPAN</span>
                        </a>
                    </div>

                    <div className="cart-navbar-menu">
                        <a href="#home" onClick={(e) => { e.preventDefault(); window.location.hash = ''; }} className="cart-nav-link">Home</a>
                        <a href="#products" className="cart-nav-link">Collections</a>
                        <a href="#products?category=new" className="cart-nav-link">New</a>
                        <a href="#products" className="cart-nav-link">Products</a>
                    </div>

                    <div className="cart-navbar-icons">
                        <button className="cart-icon-button" onClick={() => window.location.hash = '#favourites'} title="Favourites">
                            <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                                <path d="M20.84 4.61a5.5 5.5 0 0 0-7.78 0L12 5.67l-1.06-1.06a5.5 5.5 0 0 0-7.78 7.78l1.06 1.06L12 21.23l7.78-7.78 1.06-1.06a5.5 5.5 0 0 0 0-7.78z"></path>
                            </svg>
                        </button>

                        <button className="cart-icon-button active-cart-button" title="Cart">
                            <span className="cart-button-text">Cart</span>
                            <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                                <circle cx="9" cy="21" r="1"></circle>
                                <circle cx="20" cy="21" r="1"></circle>
                                <path d="M1 1h4l2.68 13.39a2 2 0 0 0 2 1.61h9.72a2 2 0 0 0 2-1.61L23 6H6"></path>
                            </svg>
                        </button>

                        <button className="cart-icon-button" title="Account">
                            <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                                <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"></path>
                                <circle cx="12" cy="7" r="4"></circle>
                            </svg>
                        </button>
                    </div>
                </div>
            </nav>

            <div className="cart-content">
                {/* Tabs */}
                <div className="cart-tabs">
                    <button
                        className={`cart-tab ${activeTab === 'shopping-bag' ? 'active' : ''}`}
                        onClick={() => setActiveTab('shopping-bag')}
                    >
                        SHOPPING BAG
                    </button>
                    <button
                        className={`cart-tab ${activeTab === 'favourites' ? 'active' : ''}`}
                        onClick={() => setActiveTab('favourites')}
                    >
                        <svg width="16" height="16" viewBox="0 0 24 24" fill="none" style={{ marginRight: '8px' }}>
                            <path d="M20.84 4.61a5.5 5.5 0 0 0-7.78 0L12 5.67l-1.06-1.06a5.5 5.5 0 0 0-7.78 7.78l1.06 1.06L12 21.23l7.78-7.78 1.06-1.06a5.5 5.5 0 0 0 0-7.78z" stroke="currentColor" strokeWidth="2" />
                        </svg>
                        FAVOURITES
                    </button>
                </div>

                <div className="cart-main">
                    <div className="cart-items">
                        {activeTab === 'shopping-bag' && cart.length === 0 && (
                            <div className="empty-cart">
                                <p>Your shopping bag is empty</p>
                                <button onClick={() => window.location.hash = '#products'}>
                                    Continue Shopping
                                </button>
                            </div>
                        )}

                        {activeTab === 'shopping-bag' && cart.map((item, index) => (
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

                        {activeTab === 'favourites' && (
                            <div className="empty-cart">
                                <p>You haven't added any favourites yet</p>
                                <button onClick={() => window.location.hash = '#products'}>
                                    Browse Products
                                </button>
                            </div>
                        )}
                    </div>

                    {activeTab === 'shopping-bag' && cart.length > 0 && (
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
            </div>
        </div>
    );
};

export default CartPage;
