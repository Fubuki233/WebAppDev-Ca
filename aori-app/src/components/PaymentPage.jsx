/**
 * PaymentPage.jsx - Order Payment Page
 * 
 * @author Yunhe
 * @date 2025-10-15
 * @version 1.0
 */
import React, { useState, useEffect } from 'react';
import Navbar from './Navbar';
import { getOrderDetails, processPayment } from '../api/orderApi';
import '../styles/PaymentPage.css';

const PaymentPage = () => {
    const [order, setOrder] = useState(null);
    const [loading, setLoading] = useState(true);
    const [processing, setProcessing] = useState(false);
    const [error, setError] = useState('');
    const [paymentMethod, setPaymentMethod] = useState('card');
    const [cardDetails, setCardDetails] = useState({
        cardNumber: '',
        cardName: '',
        expiryDate: '',
        cvv: ''
    });

    // Get orderId from URL hash
    const getOrderIdFromHash = () => {
        const hash = window.location.hash;
        const match = hash.match(/payment\/(.+)/);
        return match ? match[1] : null;
    };

    useEffect(() => {
        loadOrderDetails();
    }, []);

    const loadOrderDetails = async () => {
        const orderId = getOrderIdFromHash();
        if (!orderId) {
            setError('No order ID provided');
            setLoading(false);
            return;
        }

        try {
            const response = await getOrderDetails(orderId);
            if (response.success) {
                setOrder(response.order);
            } else {
                setError(response.message || 'Failed to load order details');
            }
        } catch (err) {
            console.error('Error loading order:', err);
            setError('Failed to load order details');
        } finally {
            setLoading(false);
        }
    };

    const handleCardInputChange = (e) => {
        const { name, value } = e.target;

        // Format card number with spaces
        if (name === 'cardNumber') {
            const cleaned = value.replace(/\s/g, '');
            const formatted = cleaned.match(/.{1,4}/g)?.join(' ') || cleaned;
            setCardDetails(prev => ({ ...prev, [name]: formatted }));
        }
        // Format expiry date
        else if (name === 'expiryDate') {
            const cleaned = value.replace(/\D/g, '');
            const formatted = cleaned.length >= 2
                ? cleaned.slice(0, 2) + (cleaned.length > 2 ? '/' + cleaned.slice(2, 4) : '')
                : cleaned;
            setCardDetails(prev => ({ ...prev, [name]: formatted }));
        }
        else {
            setCardDetails(prev => ({ ...prev, [name]: value }));
        }
    };

    const handlePayment = async (e) => {
        e.preventDefault();
        setError('');
        setProcessing(true);

        // Validate payment details
        if (paymentMethod === 'card') {
            if (!cardDetails.cardNumber || !cardDetails.cardName ||
                !cardDetails.expiryDate || !cardDetails.cvv) {
                setError('Please fill in all card details');
                setProcessing(false);
                return;
            }
        }

        try {
            const orderId = getOrderIdFromHash();
            const response = await processPayment(orderId);

            if (response.success) {
                alert('Payment successful!');
                window.location.hash = '#profile';
            } else {
                setError(response.message || 'Payment failed');
            }
        } catch (err) {
            console.error('Payment error:', err);
            setError('Payment failed. Please try again.');
        } finally {
            setProcessing(false);
        }
    };

    if (loading) {
        return (
            <div className="payment-page">
                <Navbar />
                <div className="payment-container">
                    <div className="loading">Loading order details...</div>
                </div>
            </div>
        );
    }

    if (error && !order) {
        return (
            <div className="payment-page">
                <Navbar />
                <div className="payment-container">
                    <div className="error-message">{error}</div>
                    <button onClick={() => window.location.hash = '#profile'}>
                        Back to Orders
                    </button>
                </div>
            </div>
        );
    }

    return (
        <div className="payment-page">
            <Navbar />

            <div className="payment-container">
                <div className="payment-header">
                    <h1>Complete Your Payment</h1>
                    <p>Order #{order?.orderNumber || order?.orderId}</p>
                </div>

                <div className="payment-content">
                    {/* Order Summary */}
                    <div className="order-summary-section">
                        <h2>Order Summary</h2>
                        <div className="summary-details">
                            <div className="summary-row">
                                <span>Order Total:</span>
                                <span className="amount">${order?.totalAmount?.toFixed(2)}</span>
                            </div>
                            <div className="summary-row">
                                <span>Status:</span>
                                <span className={`status ${order?.orderStatus?.toLowerCase()}`}>
                                    {order?.orderStatus}
                                </span>
                            </div>
                            <div className="summary-row">
                                <span>Created:</span>
                                <span>{new Date(order?.createdAt).toLocaleString()}</span>
                            </div>
                        </div>
                    </div>

                    {/* Payment Form */}
                    <div className="payment-form-section">
                        <h2>Payment Method</h2>

                        {error && (
                            <div className="error-message">{error}</div>
                        )}

                        <form onSubmit={handlePayment}>
                            {/* Payment Method Selection */}
                            <div className="payment-methods">
                                <label className={`payment-method-option ${paymentMethod === 'card' ? 'selected' : ''}`}>
                                    <input
                                        type="radio"
                                        name="paymentMethod"
                                        value="card"
                                        checked={paymentMethod === 'card'}
                                        onChange={(e) => setPaymentMethod(e.target.value)}
                                    />
                                    <div className="method-info">
                                        <span>Credit/Debit Card</span>
                                    </div>
                                </label>

                                <label className={`payment-method-option ${paymentMethod === 'paypal' ? 'selected' : ''}`}>
                                    <input
                                        type="radio"
                                        name="paymentMethod"
                                        value="paypal"
                                        checked={paymentMethod === 'paypal'}
                                        onChange={(e) => setPaymentMethod(e.target.value)}
                                    />
                                    <div className="method-info">
                                        <span>PayLah!</span>
                                    </div>
                                </label>
                            </div>

                            {/* Card Details Form */}
                            {paymentMethod === 'card' && (
                                <div className="card-details">
                                    <div className="form-group">
                                        <label htmlFor="cardNumber">Card Number</label>
                                        <input
                                            type="text"
                                            id="cardNumber"
                                            name="cardNumber"
                                            value={cardDetails.cardNumber}
                                            onChange={handleCardInputChange}
                                            placeholder="1234 5678 9012 3456"
                                            maxLength="19"
                                            required
                                        />
                                    </div>

                                    <div className="form-group">
                                        <label htmlFor="cardName">Cardholder Name</label>
                                        <input
                                            type="text"
                                            id="cardName"
                                            name="cardName"
                                            value={cardDetails.cardName}
                                            onChange={handleCardInputChange}
                                            placeholder="John Doe"
                                            required
                                        />
                                    </div>

                                    <div className="form-row">
                                        <div className="form-group">
                                            <label htmlFor="expiryDate">Expiry Date</label>
                                            <input
                                                type="text"
                                                id="expiryDate"
                                                name="expiryDate"
                                                value={cardDetails.expiryDate}
                                                onChange={handleCardInputChange}
                                                placeholder="MM/YY"
                                                maxLength="5"
                                                required
                                            />
                                        </div>

                                        <div className="form-group">
                                            <label htmlFor="cvv">CVV</label>
                                            <input
                                                type="text"
                                                id="cvv"
                                                name="cvv"
                                                value={cardDetails.cvv}
                                                onChange={handleCardInputChange}
                                                placeholder="123"
                                                maxLength="4"
                                                required
                                            />
                                        </div>
                                    </div>
                                </div>
                            )}

                            {/* PayPal Message */}
                            {paymentMethod === 'paypal' && (
                                <div className="paypal-message">
                                    <p>You will be redirected to PayPal to complete your payment.</p>
                                </div>
                            )}

                            {/* Action Buttons */}
                            <div className="payment-actions">
                                <button
                                    type="button"
                                    className="btn-cancel"
                                    onClick={() => window.location.hash = '#profile'}
                                    disabled={processing}
                                >
                                    Cancel
                                </button>
                                <button
                                    type="submit"
                                    className="btn-pay"
                                    disabled={processing}
                                >
                                    {processing ? 'Processing...' : `Pay $${order?.totalAmount?.toFixed(2)}`}
                                </button>
                            </div>
                        </form>
                    </div>
                </div>
            </div>
        </div>
    );
};

export default PaymentPage;
