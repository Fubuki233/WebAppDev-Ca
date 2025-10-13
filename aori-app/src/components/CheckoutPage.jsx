/**
 * CheckoutPage.jsx
 * 
 * @author Yunhe
 * @date 2025-10-08
 * @version 1.1
 * 
 * * @author Sun Rui
 * @date 2025-10-13
 * @version 1.2- update useEffect, handleSubmitOrder to integrate with backend APIs
 */
import React, { useState, useEffect } from 'react';
import { getCart, getCartTotal } from '../api/cartApi';
import { createOrderFromCart } from '../api/orderApi';
import '../styles/CheckoutPage.css';

const CheckoutPage = () => {
    const [currentStep, setCurrentStep] = useState(1);
    const [cart, setCart] = useState([]);
    const [isSubmitting, setIsSubmitting] = useState(false);
    const [subtotal, setSubtotal] = useState(0);
    const [formData, setFormData] = useState({
        // Contact Info
        email: '',
        phone: '',
        // Shipping Address
        firstName: '',
        lastName: '',
        country: '',
        state: '',
        address: '',
        city: '',
        postalCode: '',
        // Payment (would be added in step 3)
        shippingMethod: 'standard',
        paymentMethod: 'card'
    });

    useEffect(() => {
        const loadCart = async () => {
            try {
                const cartItems = await getCart();
                if (Array.isArray(cartItems)) {
                    setCart(cartItems);
                    // Calculate subtotal
                    const calculatedSubtotal = cartItems.reduce((sum, item) =>
                        sum + (item.price * item.quantity), 0
                    );
                    setSubtotal(calculatedSubtotal);

                    if (cartItems.length === 0) {
                        // Redirect to cart if empty
                        alert('Your cart is empty');
                        window.location.hash = '#cart';
                    }
                } else {
                    setCart([]);
                    setSubtotal(0);
                }
            } catch (error) {
                console.error('Error loading cart:', error);
                setCart([]);
            }
        };
        loadCart();
    }, []);


    const handleInputChange = (e) => {
        const { name, value } = e.target;
        setFormData(prev => ({
            ...prev,
            [name]: value
        }));
    };

    const handleBack = () => {
        window.location.hash = '#products';
    };

    const handleNextStep = () => {
        if (currentStep === 1) {
            if (!formData.email || !formData.phone || !formData.firstName ||
                !formData.lastName || !formData.address || !formData.city) {
                alert('Please fill in all required fields');
                return;
            }
        }

        if (currentStep < 3) {
            setCurrentStep(currentStep + 1);
        } else {
            handleSubmitOrder();
        }
    };

    const handleSubmitOrder = async () => {
        if (isSubmitting) return;

        setIsSubmitting(true);
        try {
            console.log('Submitting order with data:', { formData, cart });

            // Create order from cart
            const result = await createOrderFromCart();

            if (result.success && result.orderId) {
                console.log('Order created successfully:', result.orderId);
                alert(`Order placed successfully! Order ID: ${result.orderId}`);

                // Redirect to order confirmation or orders page
                window.location.hash = `#profile/orders`;
            } else {
                throw new Error(result.message || 'Failed to create order');
            }
        } catch (error) {
            console.error('Error submitting order:', error);
            alert(`Failed to place order: ${error.message}`);
        } finally {
            setIsSubmitting(false);
        }
    };

    const shipping = formData.shippingMethod === 'express' ? 15.00 : 0.00;
    const total = subtotal + shipping;

    const steps = [
        { id: 1, label: 'INFORMATION' },
        { id: 2, label: 'SHIPPING' },
        { id: 3, label: 'PAYMENT' }
    ];

    return (
        <div className="checkout-page">
            {/* Header */}
            <div className="checkout-header">
                <button className="back-button" onClick={handleBack}>
                    <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                        <path d="M19 12H5M12 19l-7-7 7-7" />
                    </svg>
                </button>
                <div className="checkout-logo">
                    <h1>AORI</h1>
                    <span className="logo-subtitle">-JAPAN</span>
                </div>
            </div>

            <div className="checkout-container">
                <div className="checkout-content">
                    <div className="checkout-form-section">
                        <h1 className="checkout-title">CHECKOUT</h1>

                        <div className="step-navigation">
                            {steps.map((step, index) => (
                                <React.Fragment key={step.id}>
                                    <button
                                        className={`step-button ${currentStep === step.id ? 'active' : ''} ${currentStep > step.id ? 'completed' : ''}`}
                                        onClick={() => currentStep > step.id && setCurrentStep(step.id)}
                                    >
                                        {step.label}
                                    </button>
                                    {index < steps.length - 1 && <span className="step-separator"></span>}
                                </React.Fragment>
                            ))}
                        </div>


                        {currentStep === 1 && (
                            <div className="form-step">
                                <div className="form-section">
                                    <h3 className="form-section-title">CONTACT INFO</h3>
                                    <div className="form-row">
                                        <input
                                            type="email"
                                            name="email"
                                            placeholder="Email"
                                            value={formData.email}
                                            onChange={handleInputChange}
                                            className="form-input full-width"
                                            required
                                        />
                                    </div>
                                    <div className="form-row">
                                        <input
                                            type="tel"
                                            name="phone"
                                            placeholder="Phone"
                                            value={formData.phone}
                                            onChange={handleInputChange}
                                            className="form-input full-width"
                                            required
                                        />
                                    </div>
                                </div>

                                <div className="form-section">
                                    <h3 className="form-section-title">SHIPPING ADDRESS</h3>
                                    <div className="form-row two-columns">
                                        <input
                                            type="text"
                                            name="firstName"
                                            placeholder="First Name"
                                            value={formData.firstName}
                                            onChange={handleInputChange}
                                            className="form-input"
                                            required
                                        />
                                        <input
                                            type="text"
                                            name="lastName"
                                            placeholder="Last Name"
                                            value={formData.lastName}
                                            onChange={handleInputChange}
                                            className="form-input"
                                            required
                                        />
                                    </div>
                                    <div className="form-row">
                                        <select
                                            name="country"
                                            value={formData.country}
                                            onChange={handleInputChange}
                                            className="form-input full-width"
                                            required
                                        >
                                            <option value="">Country</option>
                                            <option value="us">United States</option>
                                            <option value="uk">United Kingdom</option>
                                            <option value="jp">Japan</option>
                                            <option value="cn">China</option>
                                            <option value="other">Other</option>
                                        </select>
                                    </div>
                                    <div className="form-row">
                                        <input
                                            type="text"
                                            name="state"
                                            placeholder="State / Region"
                                            value={formData.state}
                                            onChange={handleInputChange}
                                            className="form-input full-width"
                                        />
                                    </div>
                                    <div className="form-row">
                                        <input
                                            type="text"
                                            name="address"
                                            placeholder="Address"
                                            value={formData.address}
                                            onChange={handleInputChange}
                                            className="form-input full-width"
                                            required
                                        />
                                    </div>
                                    <div className="form-row two-columns">
                                        <input
                                            type="text"
                                            name="city"
                                            placeholder="City"
                                            value={formData.city}
                                            onChange={handleInputChange}
                                            className="form-input"
                                            required
                                        />
                                        <input
                                            type="text"
                                            name="postalCode"
                                            placeholder="Postal Code"
                                            value={formData.postalCode}
                                            onChange={handleInputChange}
                                            className="form-input"
                                        />
                                    </div>
                                </div>

                                <button className="next-button" onClick={handleNextStep}>
                                    Shipping
                                    <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                                        <path d="M5 12h14M12 5l7 7-7 7" />
                                    </svg>
                                </button>
                            </div>
                        )}

                        {currentStep === 2 && (
                            <div className="form-step">
                                <div className="form-section">
                                    <h3 className="form-section-title">SHIPPING METHOD</h3>
                                    <div className="shipping-options">
                                        <label className="shipping-option">
                                            <input
                                                type="radio"
                                                name="shippingMethod"
                                                value="standard"
                                                checked={formData.shippingMethod === 'standard'}
                                                onChange={handleInputChange}
                                            />
                                            <div className="shipping-info">
                                                <span className="shipping-name">Standard Shipping</span>
                                                <span className="shipping-time">5-7 business days</span>
                                            </div>
                                            <span className="shipping-price">Free</span>
                                        </label>
                                        <label className="shipping-option">
                                            <input
                                                type="radio"
                                                name="shippingMethod"
                                                value="express"
                                                checked={formData.shippingMethod === 'express'}
                                                onChange={handleInputChange}
                                            />
                                            <div className="shipping-info">
                                                <span className="shipping-name">Express Shipping</span>
                                                <span className="shipping-time">2-3 business days</span>
                                            </div>
                                            <span className="shipping-price">$15.00</span>
                                        </label>
                                    </div>
                                </div>

                                <button className="next-button" onClick={handleNextStep}>
                                    Payment
                                    <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                                        <path d="M5 12h14M12 5l7 7-7 7" />
                                    </svg>
                                </button>
                            </div>
                        )}

                        {currentStep === 3 && (
                            <div className="form-step">
                                <div className="form-section">
                                    <h3 className="form-section-title">PAYMENT METHOD</h3>
                                    <div className="payment-options">
                                        <label className="payment-option">
                                            <input
                                                type="radio"
                                                name="paymentMethod"
                                                value="card"
                                                checked={formData.paymentMethod === 'card'}
                                                onChange={handleInputChange}
                                            />
                                            <span>Credit / Debit Card</span>
                                        </label>
                                        <label className="payment-option">
                                            <input
                                                type="radio"
                                                name="paymentMethod"
                                                value="paypal"
                                                checked={formData.paymentMethod === 'paypal'}
                                                onChange={handleInputChange}
                                            />
                                            <span>PayPal</span>
                                        </label>
                                    </div>
                                </div>

                                <button
                                    className="next-button submit-button"
                                    onClick={handleNextStep}
                                    disabled={isSubmitting}
                                >
                                    {isSubmitting ? 'Processing...' : 'Place Order'}
                                </button>
                            </div>
                        )}
                    </div>

                    <div className="order-summary-section">
                        <div className="order-summary-header">
                            <h3>YOUR ORDER</h3>
                            <span className="items-count">({cart.length})</span>
                        </div>

                        <div className="order-items">
                            {cart.map((item, index) => (
                                <div key={index} className="order-item">
                                    <img src={item.image} alt={item.name} className="item-image" />
                                    <div className="item-details">
                                        <h4 className="item-name">{item.name}</h4>
                                        <p className="item-variant">{item.color} / {item.size}</p>
                                        <div className="item-quantity-price">
                                            <span className="item-quantity">({item.quantity})</span>
                                            <span className="item-price">$ {item.price}</span>
                                        </div>
                                    </div>
                                    <a href="#change" className="change-link">Change</a>
                                </div>
                            ))}
                        </div>

                        <div className="order-totals">
                            <div className="total-row">
                                <span>Subtotal</span>
                                <span className="amount">${subtotal.toFixed(2)}</span>
                            </div>
                            <div className="total-row">
                                <span>Shipping</span>
                                {currentStep >= 2 ? (
                                    <span className="amount">${shipping.toFixed(2)}</span>
                                ) : (
                                    <span className="shipping-note">CALCULATED AT NEXT STEP</span>
                                )}
                            </div>
                            <div className="total-row final-total">
                                <span>Total</span>
                                <span className="amount">${total.toFixed(2)}</span>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    );
};

export default CheckoutPage;
