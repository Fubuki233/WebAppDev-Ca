/**
 * CheckoutPage.jsx
 * 
 * @author Yunhe
 * @date 2025-10-08
 * @version 1.1
 * 
 *  @author Sun Rui
 * @date 2025-10-13
 * @version 1.2 -update the checkout page UI and logic
 */
import React, { useState, useEffect, useRef } from 'react';
import { getCart, getCartTotal, checkout } from '../api/cartApi';
import { createOrderFromCart } from '../api/orderApi';
import '../styles/CheckoutPage.css';

const CheckoutPage = () => {
    const [currentStep, setCurrentStep] = useState(1);
    const [cart, setCart] = useState([]);
    const [isSubmitting, setIsSubmitting] = useState(false);
    const [subtotal, setSubtotal] = useState(0);
    const [orderId, setOrderId] = useState(null);
    const [error, setError] = useState(null);
    const [isLoading, setIsLoading] = useState(true);
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

    const [fieldErrors, setFieldErrors] = useState({});
    const initializingRef = useRef(false);
    const initializedRef = useRef(false);

    useEffect(() => {
        // Prevent double execution in React strict mode
        if (initializingRef.current || initializedRef.current) {
            return;
        }

        const initCheckout = async () => {
            initializingRef.current = true;

            try {
                setIsLoading(true);

                // Load cart first
                const cartItems = await getCart();
                if (!cartItems || cartItems.length === 0) {
                    alert('Your cart is empty');
                    window.location.hash = '#cart';
                    return;
                }

                setCart(Array.isArray(cartItems) ? cartItems : []);
                const total = await getCartTotal();
                setSubtotal(total);

                // Create order immediately (only once)
                console.log('Creating order from cart...');
                const result = await checkout();
                if (result.success) {
                    setOrderId(result.orderId);
                    initializedRef.current = true;
                    console.log('Order created successfully:', result.orderId);
                } else {
                    throw new Error(result.message || 'Failed to create order');
                }
            } catch (err) {
                console.error('Checkout initialization error:', err);
                alert('Failed to create order: ' + err.message);
                window.location.hash = '#cart';
            } finally {
                setIsLoading(false);
                initializingRef.current = false;
            }
        };

        initCheckout();
    }, []);



    const validateField = (name, value) => {
        let error = '';

        switch (name) {
            case 'email':
                const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
                if (!value) {
                    error = 'Email is required';
                } else if (!emailRegex.test(value)) {
                    error = 'Please enter a valid email address';
                }
                break;

            case 'phone':
                const phoneRegex = /^[\d\s\-\+\(\)]{8,}$/;
                if (!value) {
                    error = 'Phone number is required';
                } else if (!phoneRegex.test(value)) {
                    error = 'Please enter a valid phone number (at least 8 digits)';
                }
                break;

            case 'firstName':
            case 'lastName':
                if (!value) {
                    error = `${name === 'firstName' ? 'First' : 'Last'} name is required`;
                } else if (value.length < 2) {
                    error = 'Name must be at least 2 characters';
                } else if (!/^[a-zA-Z\s\-']+$/.test(value)) {
                    error = 'Name can only contain letters, spaces, hyphens and apostrophes';
                }
                break;

            case 'address':
                if (!value) {
                    error = 'Address is required';
                } else if (value.length < 5) {
                    error = 'Address must be at least 5 characters';
                }
                break;

            case 'city':
                if (!value) {
                    error = 'City is required';
                } else if (value.length < 2) {
                    error = 'City name must be at least 2 characters';
                }
                break;

            case 'postalCode':
                if (!value) {
                    error = 'Postal code is required';
                } else if (!/^[A-Za-z0-9\s\-]{3,10}$/.test(value)) {
                    error = 'Please enter a valid postal code';
                }
                break;

            case 'country':
                if (!value) {
                    error = 'Country is required';
                }
                break;

            default:
                break;
        }

        return error;
    };

    const handleInputChange = (e) => {
        const { name, value } = e.target;
        setFormData(prev => ({
            ...prev,
            [name]: value
        }));

        // Clear error for this field when user starts typing
        if (fieldErrors[name]) {
            setFieldErrors(prev => ({
                ...prev,
                [name]: ''
            }));
        }
    };

    const handleBlur = (e) => {
        const { name, value } = e.target;
        const error = validateField(name, value);
        if (error) {
            setFieldErrors(prev => ({
                ...prev,
                [name]: error
            }));
        }
    };

    const handleBack = () => {
        window.location.hash = '#products';
    };

    const validateStep = () => {
        const errors = {};

        if (currentStep === 1) {
            // Validate all fields in step 1
            const fieldsToValidate = ['email', 'phone', 'firstName', 'lastName', 'country', 'address', 'city', 'postalCode'];

            fieldsToValidate.forEach(field => {
                const error = validateField(field, formData[field]);
                if (error) {
                    errors[field] = error;
                }
            });
        }

        setFieldErrors(errors);
        return Object.keys(errors).length === 0;
    };

    const handleNextStep = () => {
        if (currentStep === 1) {
            if (!validateStep()) {
                alert('Please correct the errors in the form');
                return;
            }
        }

        // After selecting shipping method (step 2), redirect to payment page
        if (currentStep === 2) {
            if (!orderId) {
                alert('Order ID not found. Please try again.');
                window.location.hash = '#cart';
                return;
            }
            // Redirect to payment page
            window.location.hash = `#payment/${orderId}`;
            return;
        }

        if (currentStep < 3) {
            setCurrentStep(currentStep + 1);
        } else {
            handleSubmitOrder();
        }
    };

    const handleSubmitOrder = async () => {
        try {
            setIsSubmitting(true);
            setError(null);

            if (!orderId) {
                alert('Order ID not found. Please try again.');
                window.location.hash = '#cart';
                return;
            }

            // Order is already created, just redirect to payment page
            alert(`Order confirmed!\nOrder ID: ${orderId}\nRedirecting to payment...`);
            window.location.hash = `#payment/${orderId}`;
        } catch (err) {
            setError(err.message);
            alert('Error: ' + err.message);
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

    if (isLoading) {
        return (
            <div className="checkout-page">
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
                <div className="checkout-container" style={{ textAlign: 'center', padding: '100px 20px' }}>
                    <h2>Creating your order...</h2>
                    <p>Please wait while we process your order.</p>
                </div>
            </div>
        );
    }

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

                        {error && (
                            <div className="checkout-error" style={{
                                padding: '12px',
                                marginBottom: '20px',
                                backgroundColor: '#fee',
                                border: '1px solid #fcc',
                                borderRadius: '4px',
                                color: '#c33'
                            }}>
                                {error}
                            </div>
                        )}

                        {orderId && (
                            <div className="order-info-box" style={{
                                padding: '12px',
                                marginBottom: '20px',
                                backgroundColor: '#e8f5e9',
                                border: '1px solid #81c784',
                                borderRadius: '4px',
                                color: '#2e7d32'
                            }}>
                                ✓ Order Created: #{orderId.substring(0, 8)}
                            </div>
                        )}

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
                                            placeholder="Email *"
                                            value={formData.email}
                                            onChange={handleInputChange}
                                            onBlur={handleBlur}
                                            className={`form-input full-width ${fieldErrors.email ? 'error' : ''}`}
                                            required
                                        />
                                        {fieldErrors.email && <span className="error-message">{fieldErrors.email}</span>}
                                    </div>
                                    <div className="form-row">
                                        <input
                                            type="tel"
                                            name="phone"
                                            placeholder="Phone *"
                                            value={formData.phone}
                                            onChange={handleInputChange}
                                            onBlur={handleBlur}
                                            className={`form-input full-width ${fieldErrors.phone ? 'error' : ''}`}
                                            required
                                        />
                                        {fieldErrors.phone && <span className="error-message">{fieldErrors.phone}</span>}
                                    </div>
                                </div>

                                <div className="form-section">
                                    <h3 className="form-section-title">SHIPPING ADDRESS</h3>
                                    <div className="form-row two-columns">
                                        <div className="form-field">
                                            <input
                                                type="text"
                                                name="firstName"
                                                placeholder="First Name *"
                                                value={formData.firstName}
                                                onChange={handleInputChange}
                                                onBlur={handleBlur}
                                                className={`form-input ${fieldErrors.firstName ? 'error' : ''}`}
                                                required
                                            />
                                            {fieldErrors.firstName && <span className="error-message">{fieldErrors.firstName}</span>}
                                        </div>
                                        <div className="form-field">
                                            <input
                                                type="text"
                                                name="lastName"
                                                placeholder="Last Name *"
                                                value={formData.lastName}
                                                onChange={handleInputChange}
                                                onBlur={handleBlur}
                                                className={`form-input ${fieldErrors.lastName ? 'error' : ''}`}
                                                required
                                            />
                                            {fieldErrors.lastName && <span className="error-message">{fieldErrors.lastName}</span>}
                                        </div>
                                    </div>
                                    <div className="form-row">
                                        <select
                                            name="country"
                                            value={formData.country}
                                            onChange={handleInputChange}
                                            onBlur={handleBlur}
                                            className={`form-input full-width ${fieldErrors.country ? 'error' : ''}`}
                                            required
                                        >
                                            <option value="">Country *</option>
                                            <option value="us">United States</option>
                                            <option value="uk">United Kingdom</option>
                                            <option value="jp">Japan</option>
                                            <option value="cn">China</option>
                                            <option value="sg">Singapore</option>
                                            <option value="other">Other</option>
                                        </select>
                                        {fieldErrors.country && <span className="error-message">{fieldErrors.country}</span>}
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
                                            placeholder="Address *"
                                            value={formData.address}
                                            onChange={handleInputChange}
                                            onBlur={handleBlur}
                                            className={`form-input full-width ${fieldErrors.address ? 'error' : ''}`}
                                            required
                                        />
                                        {fieldErrors.address && <span className="error-message">{fieldErrors.address}</span>}
                                    </div>
                                    <div className="form-row two-columns">
                                        <div className="form-field">
                                            <input
                                                type="text"
                                                name="city"
                                                placeholder="City *"
                                                value={formData.city}
                                                onChange={handleInputChange}
                                                onBlur={handleBlur}
                                                className={`form-input ${fieldErrors.city ? 'error' : ''}`}
                                                required
                                            />
                                            {fieldErrors.city && <span className="error-message">{fieldErrors.city}</span>}
                                        </div>
                                        <div className="form-field">
                                            <input
                                                type="text"
                                                name="postalCode"
                                                placeholder="Postal Code *"
                                                value={formData.postalCode}
                                                onChange={handleInputChange}
                                                onBlur={handleBlur}
                                                className={`form-input ${fieldErrors.postalCode ? 'error' : ''}`}
                                                required
                                            />
                                            {fieldErrors.postalCode && <span className="error-message">{fieldErrors.postalCode}</span>}
                                        </div>
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
