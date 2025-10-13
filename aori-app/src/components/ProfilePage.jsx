/**
 * ProfilePage.jsx - User Profile & Order Management
 * 
 * @author Yunhe
 * @date 2025-10-09
 * @version 2.0
 */
import React, { useState, useEffect } from 'react';
import Navbar from './Navbar';
import { getProfile, updateProfile } from '../api/profileApi';
import { getUserOrders, getOrderDetails, cancelOrder } from '../api/orderApi';
import { getViewHistory } from '../api/viewHistoryApi';
import { fetchProductById } from '../api/productApi';
import '../styles/ProfilePage.css';

const ProfilePage = () => {
    // Active tab state: 'orders', 'profile', or 'history'
    const [activeTab, setActiveTab] = useState('orders');

    // Profile states
    const [profile, setProfile] = useState(null);
    const [formData, setFormData] = useState({
        firstName: '',
        lastName: '',
        email: '',
        phone: '',
        dateOfBirth: '',
        gender: ''
    });
    const [isEditing, setIsEditing] = useState(false);
    const [saving, setSaving] = useState(false);

    // Order states
    const [orders, setOrders] = useState([]);
    const [selectedOrder, setSelectedOrder] = useState(null);
    const [orderDetails, setOrderDetails] = useState(null);

    // View history states
    const [viewHistory, setViewHistory] = useState([]);
    const [historyProducts, setHistoryProducts] = useState({});
    const [loadingHistory, setLoadingHistory] = useState(false);

    // Common states
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');
    const [success, setSuccess] = useState('');

    // Load data on mount
    useEffect(() => {
        loadProfile();
        loadOrders();
    }, []);

    // Load view history when tab changes
    useEffect(() => {
        console.log('History tab effect triggered:', {
            activeTab,
            hasProfile: !!profile,
            profileCustomerId: profile?.customerId
        });

        if (activeTab === 'history' && profile?.customerId) {
            console.log('Conditions met, loading history...');
            loadViewHistory();
        } else if (activeTab === 'history') {
            console.log('Active tab is history but profile.customerId is missing');
        }
    }, [activeTab, profile]);

    const loadProfile = async () => {
        setError('');
        try {
            const response = await getProfile();
            console.log('Profile response:', response);

            if (response.success && response.profile) {
                console.log('Profile data:', response.profile);
                console.log('Profile UUID:', response.profile.uuid);
                console.log('Profile customerId:', response.profile.customerId);
                setProfile(response.profile);
                setFormData({
                    firstName: response.profile.firstName || '',
                    lastName: response.profile.lastName || '',
                    email: response.profile.email || '',
                    phone: response.profile.phone || '',
                    dateOfBirth: response.profile.dateOfBirth || '',
                    gender: response.profile.gender || ''
                });
            } else {
                setError(response.message || 'Failed to load profile');
            }
        } catch (err) {
            console.error('Load profile error:', err);
            setError('Failed to load profile. Please try again.');
        }
    };

    const loadOrders = async () => {
        setLoading(true);
        setError('');
        try {
            const response = await getUserOrders();
            console.log('Orders response:', response);

            if (Array.isArray(response)) {
                setOrders(response);
            } else if (response.orders && Array.isArray(response.orders)) {
                setOrders(response.orders);
            } else {
                setOrders([]);
            }
        } catch (err) {
            console.error('Load orders error:', err);
            setError('Failed to load orders. Please try again.');
            setOrders([]);
        } finally {
            setLoading(false);
        }
    };

    const loadViewHistory = async () => {
        if (!profile?.customerId) {
            console.log('Profile customerId not available, skipping history load');
            return;
        }

        console.log('Loading view history for user:', profile.customerId);
        setLoadingHistory(true);
        setError('');
        try {
            const history = await getViewHistory(profile.customerId);
            console.log('View history response:', history);
            console.log('History is array:', Array.isArray(history));
            console.log('History length:', history?.length);

            if (Array.isArray(history)) {
                setViewHistory(history);

                // Load product details for each history item
                const productPromises = history.map(item =>
                    fetchProductById(item.productId).catch(err => {
                        console.error(`Failed to load product ${item.productId}:`, err);
                        return null;
                    })
                );

                const products = await Promise.all(productPromises);
                const productMap = {};
                products.forEach((product, index) => {
                    if (product) {
                        productMap[history[index].productId] = product;
                    }
                });

                setHistoryProducts(productMap);
            } else {
                setViewHistory([]);
            }
        } catch (err) {
            console.error('Load view history error:', err);
            setError('Failed to load view history.');
            setViewHistory([]);
        } finally {
            setLoadingHistory(false);
        }
    };

    const handleViewOrderDetails = async (orderId) => {
        try {
            const response = await getOrderDetails(orderId);
            if (response.success) {
                setOrderDetails(response);
                setSelectedOrder(orderId);
            }
        } catch (err) {
            console.error('View order details error:', err);
            setError('Failed to load order details.');
        }
    };

    const handleCancelOrder = async (orderId) => {
        if (!window.confirm('Are you sure you want to cancel this order?')) {
            return;
        }

        try {
            const response = await cancelOrder(orderId);
            if (response.success) {
                setSuccess('Order cancelled successfully!');
                loadOrders(); // Reload orders
                if (selectedOrder === orderId) {
                    setSelectedOrder(null);
                    setOrderDetails(null);
                }
            } else {
                setError(response.message || 'Failed to cancel order');
            }
        } catch (err) {
            console.error('Cancel order error:', err);
            setError('Failed to cancel order. Please try again.');
        }
    };

    const handleChange = (e) => {
        const { name, value } = e.target;
        setFormData(prev => ({
            ...prev,
            [name]: value
        }));
        // Clear messages when user types
        if (error) setError('');
        if (success) setSuccess('');
    };

    const handleEdit = () => {
        setIsEditing(true);
        setError('');
        setSuccess('');
    };

    const handleCancel = () => {
        // Reset form data to original profile
        setFormData({
            firstName: profile.firstName || '',
            lastName: profile.lastName || '',
            email: profile.email || '',
            phone: profile.phone || '',
            dateOfBirth: profile.dateOfBirth || '',
            gender: profile.gender || ''
        });
        setIsEditing(false);
        setError('');
        setSuccess('');
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        setError('');
        setSuccess('');
        setSaving(true);

        try {
            const response = await updateProfile(formData);
            console.log('Update response:', response);

            if (response.success) {
                setProfile(response.profile);
                setSuccess('Profile updated successfully!');
                setIsEditing(false);
                // Reload to get latest data
                setTimeout(() => {
                    loadProfile();
                }, 1000);
            } else {
                setError(response.message || 'Failed to update profile');
            }
        } catch (err) {
            console.error('Update profile error:', err);
            setError('Failed to update profile. Please try again.');
        } finally {
            setSaving(false);
        }
    };

    const getStatusBadgeClass = (status) => {
        const statusMap = {
            'Pending': 'status-pending',
            'Paid': 'status-paid',
            'Processing': 'status-processing',
            'Shipped': 'status-shipped',
            'Delivered': 'status-delivered',
            'Cancelled': 'status-cancelled',
            'Failed': 'status-failed'
        };
        return statusMap[status] || 'status-default';
    };

    if (loading) {
        return (
            <div className="profile-page">
                <Navbar />
                <div className="profile-container">
                    <div className="loading-container">
                        <div className="loading-spinner"></div>
                        <p>Loading...</p>
                    </div>
                </div>
            </div>
        );
    }

    return (
        <div className="profile-page">
            <Navbar />
            <div className="profile-container">
                {/* Breadcrumb */}
                <div className="breadcrumb">
                    <a href="#" onClick={(e) => { e.preventDefault(); window.location.hash = ''; }}>Home</a>
                    <span className="separator">/</span>
                    <span className="current">
                        {activeTab === 'orders' ? 'My Orders' : activeTab === 'profile' ? 'My Profile' : 'View History'}
                    </span>
                </div>

                {/* Page Title */}
                <h1 className="page-title">MY ACCOUNT</h1>

                <div className="account-layout">
                    {/* Sidebar Navigation */}
                    <aside className="account-sidebar">
                        <nav className="sidebar-nav">
                            <button
                                className={`sidebar-item ${activeTab === 'orders' ? 'active' : ''}`}
                                onClick={() => {
                                    setActiveTab('orders');
                                    setError('');
                                    setSuccess('');
                                }}
                            >
                                <svg className="sidebar-icon" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                                    <path d="M21 16V8a2 2 0 0 0-1-1.73l-7-4a2 2 0 0 0-2 0l-7 4A2 2 0 0 0 3 8v8a2 2 0 0 0 1 1.73l7 4a2 2 0 0 0 2 0l7-4A2 2 0 0 0 21 16z"></path>
                                    <polyline points="3.27 6.96 12 12.01 20.73 6.96"></polyline>
                                    <line x1="12" y1="22.08" x2="12" y2="12"></line>
                                </svg>
                                <span>My Orders</span>
                            </button>
                            <button
                                className={`sidebar-item ${activeTab === 'profile' ? 'active' : ''}`}
                                onClick={() => {
                                    setActiveTab('profile');
                                    setError('');
                                    setSuccess('');
                                    setIsEditing(false);
                                }}
                            >
                                <svg className="sidebar-icon" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                                    <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"></path>
                                    <circle cx="12" cy="7" r="4"></circle>
                                </svg>
                                <span>Personal Info</span>
                            </button>
                            <button
                                className={`sidebar-item ${activeTab === 'history' ? 'active' : ''}`}
                                onClick={() => {
                                    setActiveTab('history');
                                    setError('');
                                    setSuccess('');
                                }}
                            >
                                <svg className="sidebar-icon" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                                    <circle cx="12" cy="12" r="10"></circle>
                                    <polyline points="12 6 12 12 16 14"></polyline>
                                </svg>
                                <span>View History</span>
                            </button>
                        </nav>
                    </aside>

                    {/* Main Content */}
                    <main className="account-main">
                        {/* Messages */}
                        {error && (
                            <div className="message error-message">
                                {error}
                            </div>
                        )}
                        {success && (
                            <div className="message success-message">
                                {success}
                            </div>
                        )}

                        {/* Orders Tab */}
                        {activeTab === 'orders' && (
                            <div className="orders-section">
                                <h2 className="section-title">My Orders</h2>

                                {orders.length === 0 ? (
                                    <div className="empty-state">
                                        <svg width="64" height="64" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.5">
                                            <path d="M21 16V8a2 2 0 0 0-1-1.73l-7-4a2 2 0 0 0-2 0l-7 4A2 2 0 0 0 3 8v8a2 2 0 0 0 1 1.73l7 4a2 2 0 0 0 2 0l7-4A2 2 0 0 0 21 16z"></path>
                                        </svg>
                                        <p>No orders yet</p>
                                        <button className="btn-shop" onClick={() => window.location.hash = 'products'}>
                                            Start Shopping
                                        </button>
                                    </div>
                                ) : (
                                    <div className="orders-list">
                                        {orders.map((order) => (
                                            <div key={order.orderId} className="order-card">
                                                <div className="order-header">
                                                    <div className="order-info">
                                                        <span className="order-id">Order #{order.orderNumber || order.orderId.substring(0, 8)}</span>
                                                        <span className="order-date">
                                                            {new Date(order.createdAt).toLocaleDateString()}
                                                        </span>
                                                    </div>
                                                    <div className="order-status-group">
                                                        <span className={`status-badge ${getStatusBadgeClass(order.orderStatus)}`}>
                                                            {order.orderStatus}
                                                        </span>
                                                        <span className={`status-badge ${getStatusBadgeClass(order.paymentStatus)}`}>
                                                            {order.paymentStatus}
                                                        </span>
                                                    </div>
                                                </div>

                                                <div className="order-body">
                                                    <div className="order-amount">
                                                        <span className="amount-label">Total Amount:</span>
                                                        <span className="amount-value">${order.totalAmount?.toFixed(2)}</span>
                                                    </div>
                                                </div>

                                                <div className="order-actions">
                                                    <button
                                                        className="btn-view"
                                                        onClick={() => handleViewOrderDetails(order.orderId)}
                                                    >
                                                        View Details
                                                    </button>
                                                    {(order.orderStatus === 'Pending' || order.orderStatus === 'Paid') && (
                                                        <button
                                                            className="btn-cancel"
                                                            onClick={() => handleCancelOrder(order.orderId)}
                                                        >
                                                            Cancel Order
                                                        </button>
                                                    )}
                                                </div>
                                            </div>
                                        ))}
                                    </div>
                                )}

                                {/* Order Details Modal */}
                                {selectedOrder && orderDetails && (
                                    <div className="modal-overlay" onClick={() => { setSelectedOrder(null); setOrderDetails(null); }}>
                                        <div className="modal-content" onClick={(e) => e.stopPropagation()}>
                                            <div className="modal-header">
                                                <h3>Order Details</h3>
                                                <button className="modal-close" onClick={() => { setSelectedOrder(null); setOrderDetails(null); }}>×</button>
                                            </div>
                                            <div className="modal-body">
                                                <div className="order-detail-info">
                                                    <div className="detail-row">
                                                        <span className="detail-label">Order Date:</span>
                                                        <span className="detail-value">
                                                            {new Date(orderDetails.order?.createdAt).toLocaleString('en-US', {
                                                                year: 'numeric',
                                                                month: 'long',
                                                                day: 'numeric',
                                                                hour: '2-digit',
                                                                minute: '2-digit'
                                                            })}
                                                        </span>
                                                    </div>
                                                    <div className="detail-row">
                                                        <span className="detail-label">Order Number:</span>
                                                        <span className="detail-value">
                                                            #{orderDetails.order?.orderNumber || orderDetails.order?.orderId?.substring(0, 8).toUpperCase()}
                                                        </span>
                                                    </div>
                                                    <div className="detail-row">
                                                        <span className="detail-label">Order Status:</span>
                                                        <span className={`status-badge ${getStatusBadgeClass(orderDetails.order?.orderStatus)}`}>
                                                            {orderDetails.order?.orderStatus}
                                                        </span>
                                                    </div>
                                                    <div className="detail-row">
                                                        <span className="detail-label">Payment Status:</span>
                                                        <span className={`status-badge ${getStatusBadgeClass(orderDetails.order?.paymentStatus)}`}>
                                                            {orderDetails.order?.paymentStatus}
                                                        </span>
                                                    </div>
                                                </div>

                                                <h4 className="items-title">Order Items</h4>
                                                <div className="order-items-list">
                                                    {orderDetails.orderItems?.map((item, index) => {
                                                        const unitPrice = item.priceAtPurchase || item.unitPrice || 0;
                                                        return (
                                                            <div key={index} className="order-item-detail">
                                                                <div className="item-image">
                                                                    <img
                                                                        src={item.product?.image || item.image || 'https://via.placeholder.com/80?text=No+Image'}
                                                                        alt={item.product?.productName || item.productName || 'Product'}
                                                                        onError={(e) => { e.target.src = 'https://via.placeholder.com/80?text=No+Image'; }}
                                                                    />
                                                                </div>
                                                                <div className="item-details">
                                                                    <div className="item-name">
                                                                        {item.product?.productName || item.productName || 'Unknown Product'}
                                                                    </div>
                                                                    <div className="item-info">
                                                                        <span className="item-quantity">Qty: {item.quantity}</span>
                                                                        <span className="item-price">${unitPrice.toFixed(2)} each</span>
                                                                    </div>
                                                                </div>
                                                                <div className="item-subtotal">
                                                                    ${(unitPrice * item.quantity).toFixed(2)}
                                                                </div>
                                                            </div>
                                                        );
                                                    })}
                                                </div>

                                                <div className="order-summary">
                                                    <div className="summary-row subtotal-row">
                                                        <span className="summary-label">Subtotal:</span>
                                                        <span className="summary-value">
                                                            ${orderDetails.orderItems?.reduce((sum, item) => {
                                                                const unitPrice = item.priceAtPurchase || item.unitPrice || 0;
                                                                return sum + (unitPrice * item.quantity);
                                                            }, 0).toFixed(2)}
                                                        </span>
                                                    </div>
                                                    <div className="summary-row total-row">
                                                        <span className="summary-label">Total Amount:</span>
                                                        <span className="summary-value total-amount">
                                                            ${orderDetails.order?.totalAmount?.toFixed(2)}
                                                        </span>
                                                    </div>
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                )}
                            </div>
                        )}

                        {/* Profile Tab */}
                        {activeTab === 'profile' && (
                            <div className="profile-section">
                                <h2 className="section-title">Personal Information</h2>

                                {/* Profile Card */}
                                <div className="profile-card">
                                    {/* Messages */}
                                    {error && (
                                        <div className="message error-message">
                                            {error}
                                        </div>
                                    )}
                                    {success && (
                                        <div className="message success-message">
                                            {success}
                                        </div>
                                    )}

                                    {/* Profile Form */}
                                    <form onSubmit={handleSubmit} className="profile-form">
                                        <div className="form-section">
                                            <h2 className="section-title">Personal Information</h2>

                                            <div className="form-row">
                                                <div className="form-group">
                                                    <label htmlFor="firstName">First Name</label>
                                                    <input
                                                        type="text"
                                                        id="firstName"
                                                        name="firstName"
                                                        value={formData.firstName}
                                                        onChange={handleChange}
                                                        disabled={!isEditing}
                                                        required
                                                    />
                                                </div>

                                                <div className="form-group">
                                                    <label htmlFor="lastName">Last Name</label>
                                                    <input
                                                        type="text"
                                                        id="lastName"
                                                        name="lastName"
                                                        value={formData.lastName}
                                                        onChange={handleChange}
                                                        disabled={!isEditing}
                                                        required
                                                    />
                                                </div>
                                            </div>

                                            <div className="form-row">
                                                <div className="form-group">
                                                    <label htmlFor="email">Email Address</label>
                                                    <input
                                                        type="email"
                                                        id="email"
                                                        name="email"
                                                        value={formData.email}
                                                        onChange={handleChange}
                                                        disabled={true}
                                                        title="Email cannot be changed"
                                                    />
                                                    <span className="field-hint">Email cannot be changed</span>
                                                </div>

                                                <div className="form-group">
                                                    <label htmlFor="phone">Phone Number</label>
                                                    <input
                                                        type="tel"
                                                        id="phone"
                                                        name="phone"
                                                        value={formData.phone}
                                                        onChange={handleChange}
                                                        disabled={!isEditing}
                                                        placeholder="Enter phone number"
                                                    />
                                                </div>
                                            </div>

                                            <div className="form-row">
                                                <div className="form-group">
                                                    <label htmlFor="dateOfBirth">Date of Birth</label>
                                                    <input
                                                        type="date"
                                                        id="dateOfBirth"
                                                        name="dateOfBirth"
                                                        value={formData.dateOfBirth}
                                                        onChange={handleChange}
                                                        disabled={!isEditing}
                                                    />
                                                </div>

                                                <div className="form-group">
                                                    <label htmlFor="gender">Gender</label>
                                                    <select
                                                        id="gender"
                                                        name="gender"
                                                        value={formData.gender}
                                                        onChange={handleChange}
                                                        disabled={!isEditing}
                                                    >
                                                        <option value="">Select Gender</option>
                                                        <option value="Male">Male</option>
                                                        <option value="Female">Female</option>
                                                        <option value="Other">Other</option>
                                                        <option value="Prefer not to say">Prefer not to say</option>
                                                    </select>
                                                </div>
                                            </div>
                                        </div>

                                        {/* Action Buttons */}
                                        <div className="form-actions">
                                            {!isEditing ? (
                                                <button
                                                    type="button"
                                                    className="btn-edit"
                                                    onClick={handleEdit}
                                                >
                                                    Edit Profile
                                                </button>
                                            ) : (
                                                <>
                                                    <button
                                                        type="button"
                                                        className="btn-cancel"
                                                        onClick={handleCancel}
                                                        disabled={saving}
                                                    >
                                                        Cancel
                                                    </button>
                                                    <button
                                                        type="submit"
                                                        className="btn-save"
                                                        disabled={saving}
                                                    >
                                                        {saving ? 'Saving...' : 'Save Changes'}
                                                    </button>
                                                </>
                                            )}
                                        </div>
                                    </form>
                                </div>

                                {/* Account Info Card */}
                                {profile && (
                                    <div className="info-card">
                                        <h2 className="section-title">Account Information</h2>
                                        <div className="info-grid">
                                            <div className="info-item">
                                                <span className="info-label">Customer ID</span>
                                                <span className="info-value">{profile.customerId}</span>
                                            </div>
                                            <div className="info-item">
                                                <span className="info-label">Member Since</span>
                                                <span className="info-value">
                                                    {profile.createdAt ? new Date(profile.createdAt).toLocaleDateString() : 'N/A'}
                                                </span>
                                            </div>
                                            <div className="info-item">
                                                <span className="info-label">Last Updated</span>
                                                <span className="info-value">
                                                    {profile.updatedAt ? new Date(profile.updatedAt).toLocaleDateString() : 'N/A'}
                                                </span>
                                            </div>
                                        </div>
                                    </div>
                                )}
                            </div>
                        )}

                        {/* View History Tab */}
                        {activeTab === 'history' && (
                            <div className="history-section">
                                <h2 className="section-title">Browsing History</h2>

                                {loadingHistory ? (
                                    <div className="loading-state">
                                        <div className="spinner"></div>
                                        <p>Loading your browsing history...</p>
                                    </div>
                                ) : viewHistory.length === 0 ? (
                                    <div className="empty-state">
                                        <svg className="empty-icon" width="64" height="64" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.5">
                                            <circle cx="12" cy="12" r="10"></circle>
                                            <polyline points="12 6 12 12 16 14"></polyline>
                                        </svg>
                                        <h3>No Browsing History</h3>
                                        <p>Start exploring products to see your browsing history here</p>
                                        <button
                                            className="btn-primary"
                                            onClick={() => window.location.hash = '#products'}
                                        >
                                            Browse Products
                                        </button>
                                    </div>
                                ) : (
                                    <div className="history-grid">
                                        {viewHistory.map((item) => {
                                            const product = historyProducts[item.productId];
                                            return (
                                                <div key={item.id} className="history-card">
                                                    {product ? (
                                                        <>
                                                            <div className="history-image-container">
                                                                <img
                                                                    src={product.images && product.images.length > 0 ? product.images[0] : '/placeholder.png'}
                                                                    alt={product.name}
                                                                    className="history-image"
                                                                    onError={(e) => {
                                                                        e.target.src = '/placeholder.png';
                                                                    }}
                                                                />
                                                            </div>
                                                            <div className="history-content">
                                                                <h3 className="history-product-name">{product.name}</h3>
                                                                <p className="history-product-category">{product.category}</p>
                                                                <div className="history-product-price">
                                                                    ${product.price?.toFixed(2)}
                                                                </div>
                                                                <div className="history-meta">
                                                                    <span className="history-timestamp">
                                                                        Viewed: {new Date(item.timestamp).toLocaleString()}
                                                                    </span>
                                                                </div>
                                                                <button
                                                                    className="btn-view-product"
                                                                    onClick={() => window.location.hash = `#product/${item.productId}`}
                                                                >
                                                                    View Product
                                                                </button>
                                                            </div>
                                                        </>
                                                    ) : (
                                                        <div className="history-loading">
                                                            <div className="spinner-small"></div>
                                                            <p>Loading product...</p>
                                                        </div>
                                                    )}
                                                </div>
                                            );
                                        })}
                                    </div>
                                )}
                            </div>
                        )}
                    </main>
                </div>
            </div>
        </div>
    );
};

export default ProfilePage;
