/**
 * ProductDetailPage.jsx 
 * 
 * @author Yunhe
 * @date 2025-10-15
 * @version 1.3 - Added SKU stock checking and inventory validation
 */
import React, { useState, useEffect } from 'react';
import Navbar from './Navbar';
import { fetchProductById } from '../api/productApi';
import { addToCart } from '../api/cartApi';
import { toggleFavourite, isInFavourites } from '../api/favouritesApi';
import { addViewHistory } from '../api/viewHistoryApi';
import { getProductReviews } from '../api/reviewApi';
import { getUserUuid } from '../api/apiUtils';
import { getSkuQuantity } from '../api/skuApi';
import '../styles/ProductDetailPage.css';

const ProductDetailPage = ({ productId }) => {
    const [product, setProduct] = useState(null);
    const [loading, setLoading] = useState(true);
    const [selectedImage, setSelectedImage] = useState(0);
    const [selectedColor, setSelectedColor] = useState('');
    const [selectedSize, setSelectedSize] = useState('');
    const [quantity, setQuantity] = useState(1);
    const [isFavorite, setIsFavorite] = useState(false);

    // Stock states
    const [availableStock, setAvailableStock] = useState(-1);
    const [checkingStock, setCheckingStock] = useState(false);

    // Notification states
    const [notification, setNotification] = useState({ show: false, message: '', type: '' });

    // Review states
    const [reviews, setReviews] = useState([]);
    const [loadingReviews, setLoadingReviews] = useState(false);

    useEffect(() => {
        const loadProduct = async () => {
            try {
                const data = await fetchProductById(productId);
                console.log('[ProductDetailPage]Fetched product data:', data);
                setProduct(data);
                if (data.colors && data.colors.length > 0) {
                    setSelectedColor(data.colors[0]);
                }
                setLoading(false);

                // Add to view history
                try {
                    const userId = await getUserUuid(true); // stayAsGuest=true, don't redirect if not logged in
                    if (userId) {
                        await addViewHistory(userId, productId);
                        console.log('[ProductDetailPage] Added to view history');
                    }
                } catch (historyError) {
                    console.error('Error adding to view history:', historyError);
                    // Don't block page load if history fails
                }
            } catch (error) {
                console.error('Error loading product:', error);
                setLoading(false);
            }
        };

        loadProduct();
    }, [productId]);

    useEffect(() => {
        const checkFavorite = async () => {
            if (product) {
                // Check if product is in wishlist (no need for size/color in backend wishlist)
                const isFav = await isInFavourites(product.id);
                setIsFavorite(isFav);
            }
        };
        checkFavorite();
    }, [product]); // Only check when product changes, not size/color

    // Check stock availability when color or size changes
    useEffect(() => {
        const checkStock = async () => {
            if (product && selectedColor && selectedSize) {
                setCheckingStock(true);
                try {
                    const stock = await getSkuQuantity(product.id, selectedColor, selectedSize);
                    setAvailableStock(stock);
                    console.log('[ProductDetailPage] Available stock for', {
                        color: selectedColor,
                        size: selectedSize,
                        stock
                    });
                } catch (error) {
                    console.error('[ProductDetailPage] Error checking stock:', error);
                    setAvailableStock(-1);
                } finally {
                    setCheckingStock(false);
                }
            } else {
                setAvailableStock(-1);
            }
        };
        checkStock();
    }, [product, selectedColor, selectedSize]);

    useEffect(() => {
        const loadReviews = async () => {
            if (productId) {
                setLoadingReviews(true);
                try {
                    console.log('[ProductDetailPage] Loading reviews for product:', productId);
                    const reviewsData = await getProductReviews(productId);
                    console.log('[ProductDetailPage] Reviews data received:', reviewsData);
                    setReviews(reviewsData.content || []);
                } catch (error) {
                    console.error('[ProductDetailPage] Error loading reviews:', error);
                } finally {
                    setLoadingReviews(false);
                }
            }
        };
        loadReviews();
    }, [productId]);

    const handleAddToCart = async () => {
        const userUuid = await getUserUuid(true);


        if (!selectedSize) {
            showNotification('Please select a size', 'error');
            return;
        }

        if (availableStock < 1) {
            showNotification('This item is currently out of stock', 'error');
            return;
        }
        if (!userUuid) {
            window.location.hash = '#login';
            return;
        }


        const cartItem = {
            productId: product.id,
            name: product.name,
            price: product.price,
            color: selectedColor,
            size: selectedSize,
            quantity: quantity,
            image: product.images ? product.images[0] : product.image,
        };

        const result = await addToCart(cartItem);

        // Refresh stock after adding to cart
        if (result.success) {
            const newStock = await getSkuQuantity(product.id, selectedColor, selectedSize);
            setAvailableStock(newStock);
            showNotification(`Added ${quantity} item(s) to cart successfully!`, 'success');
        } else {
            showNotification('Failed to add item to cart', 'error');
        }
    };

    const showNotification = (message, type) => {
        setNotification({ show: true, message, type });
        setTimeout(() => {
            setNotification({ show: false, message: '', type: '' });
        }, 3000);
    };

    const handleToggleFavorite = async () => {
        const favouriteItem = {
            productId: product.id,
            name: product.name,
            price: product.price,
            color: selectedColor,
            size: selectedSize,
            image: product.images ? product.images[0] : product.image,
        };

        const result = await toggleFavourite(favouriteItem);

        if (result.requiresLogin) {
            // User is not logged in, redirect to login page
            showNotification('Please login to add favorites', 'error');
            setTimeout(() => {
                window.location.hash = '#login';
            }, 1000);
        } else if (result.success) {
            // Use the 'added' field from backend to set the correct state
            setIsFavorite(result.added);
            if (result.added) {
                showNotification('Added to favorites!', 'success');
            } else {
                showNotification('Removed from favorites', 'success');
            }
        }
    };

    if (loading) {
        return (
            <div className="product-detail-page">
                <Navbar />
                <div className="loading-container">Loading...</div>
            </div>
        );
    }

    if (!product) {
        return (
            <div className="product-detail-page">
                <Navbar />
                <div className="error-container">Product not found</div>
            </div>
        );
    }

    const images = product.images || [product.image];
    const availableColors = product.colors || ['#000000'];
    const availableSizes = product.size || ['S', 'M', 'L', 'XL'];

    return (
        <div className="product-detail-page">
            <Navbar />

            {/* Notification Toast */}
            {notification.show && (
                <div className={`notification-toast ${notification.type}`}>
                    <div className="notification-content">
                        {notification.type === 'success' && (
                            <svg width="20" height="20" viewBox="0 0 24 24" fill="none">
                                <path d="M22 11.08V12a10 10 0 1 1-5.93-9.14" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" />
                                <polyline points="22 4 12 14.01 9 11.01" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" />
                            </svg>
                        )}
                        {notification.type === 'error' && (
                            <svg width="20" height="20" viewBox="0 0 24 24" fill="none">
                                <circle cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" />
                                <line x1="15" y1="9" x2="9" y2="15" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" />
                                <line x1="9" y1="9" x2="15" y2="15" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" />
                            </svg>
                        )}
                        <span>{notification.message}</span>
                    </div>
                </div>
            )}

            <div className="detail-container">
                <div className="breadcrumb">
                    <a href="#home" onClick={(e) => { e.preventDefault(); window.location.hash = ''; }}>Home</a>
                    <span className="separator">/</span>
                    <a href="#products">Products</a>
                    <span className="separator">/</span>
                    <span className="current">{product.name}</span>
                </div>

                <div className="product-detail-content">
                    <div className="product-images-section">
                        <div className="thumbnails">
                            {images.map((img, index) => (
                                <div
                                    key={index}
                                    className={`thumbnail ${selectedImage === index ? 'active' : ''}`}
                                    onClick={() => setSelectedImage(index)}
                                >
                                    <img src={img} alt={`${product.name} ${index + 1}`} />
                                </div>
                            ))}
                        </div>

                        <div className="main-image">
                            <button
                                className={`favorite-button ${isFavorite ? 'is-favorite' : ''}`}
                                onClick={handleToggleFavorite}
                                title={isFavorite ? 'Remove from favourites' : 'Add to favourites'}
                            >
                                <svg
                                    width="24"
                                    height="24"
                                    viewBox="0 0 24 24"
                                    fill={isFavorite ? 'currentColor' : 'none'}
                                    stroke="currentColor"
                                    strokeWidth="2"
                                >
                                    <path d="M20.84 4.61a5.5 5.5 0 0 0-7.78 0L12 5.67l-1.06-1.06a5.5 5.5 0 0 0-7.78 7.78l1.06 1.06L12 21.23l7.78-7.78 1.06-1.06a5.5 5.5 0 0 0 0-7.78z"></path>
                                </svg>
                            </button>
                            <img src={images[selectedImage]} alt={product.name} />
                        </div>
                    </div>

                    <div className="product-info-section">
                        <h1 className="product-title">{product.name}</h1>

                        <div className="price-rating-container">
                            <div className="product-price">${product.price}</div>
                            {product.rating && (
                                <div className="product-rating-detail">
                                    <div className="stars">
                                        {[...Array(5)].map((_, i) => (
                                            <span key={i} className={i < Math.floor(product.rating) ? 'star filled' : 'star'}>
                                                ★
                                            </span>
                                        ))}
                                    </div>
                                    <span className="rating-value">{product.rating.toFixed(1)}/5</span>
                                </div>
                            )}
                        </div>

                        <p className="tax-info">MRP incl. of all taxes</p>

                        <div className="product-description">
                            {product.description || `Relaxed-fit shirt. Camp collar and short sleeves. Button-up front.`}
                        </div>

                        <div className="selection-group">
                            <label className="selection-label">COLOR</label>
                            <div className="color-options">
                                {availableColors.map((color, index) => (
                                    <button
                                        key={index}
                                        className={`color-option ${selectedColor === color ? 'selected' : ''}`}
                                        style={{ backgroundColor: color }}
                                        onClick={() => setSelectedColor(color)}
                                        title={color}
                                    />
                                ))}
                            </div>
                        </div>

                        <div className="selection-group">
                            <label className="selection-label">SIZE</label>
                            <div className="size-options">
                                {availableSizes.map((size) => (
                                    <button
                                        key={size}
                                        className={`size-option ${selectedSize === size ? 'selected' : ''}`}
                                        onClick={() => setSelectedSize(size)}
                                    >
                                        {size}
                                    </button>
                                ))}
                            </div>
                            <div className="size-links">
                                <a href="#size-guide" className="size-link">FIND YOUR SIZE</a>
                                <span className="separator">|</span>
                                <a href="#measurement-guide" className="size-link">MEASUREMENT GUIDE</a>
                            </div>
                        </div>

                        {/* Stock availability indicator */}
                        {selectedColor && selectedSize && (
                            <div className={`stock-status ${availableStock > 0 ? 'in-stock' : 'out-of-stock'}`}>
                                {checkingStock ? (
                                    <span className="checking">Checking availability...</span>
                                ) : availableStock > 0 ? (
                                    <span className="available">
                                        In Stock
                                    </span>
                                ) : availableStock === 0 ? (
                                    <span className="unavailable">Out of Stock</span>
                                ) : (
                                    <span className="unavailable">Not Available</span>
                                )}
                            </div>
                        )}

                        <button
                            className="add-to-cart-button"
                            onClick={handleAddToCart}
                            disabled={!selectedSize || availableStock < 1 || checkingStock}
                        >
                            {checkingStock ? 'Checking Stock...' : availableStock < 1 && selectedSize ? 'OUT OF STOCK' : 'ADD'}
                        </button>

                        {product.details && (
                            <div className="product-details">
                                <h3>Product Details</h3>
                                <ul>
                                    {product.details.map((detail, index) => (
                                        <li key={index}>{detail}</li>
                                    ))}
                                </ul>
                            </div>
                        )}
                    </div>
                </div>

                {/* Reviews Section */}
                <div className="reviews-section">
                    <h2 className="reviews-title">Customer Reviews</h2>

                    {/* Reviews List */}
                    <div className="reviews-list">
                        {loadingReviews ? (
                            <div className="loading-reviews">
                                <div className="spinner"></div>
                                <p>Loading reviews...</p>
                            </div>
                        ) : reviews.length === 0 ? (
                            <div className="no-reviews">
                                <p>No reviews yet. Be the first to review this product!</p>
                            </div>
                        ) : (
                            <>
                                <div className="reviews-summary">
                                    <p>{reviews.length} {reviews.length === 1 ? 'review' : 'reviews'}</p>
                                </div>
                                {reviews.map((review, index) => (
                                    <div key={review.reviewId || index} className="review-card">
                                        <div className="review-header">
                                            <div className="review-author">
                                                <div className="author-avatar">
                                                    {review.customerName ? review.customerName.charAt(0).toUpperCase() : 'A'}
                                                </div>
                                                <div className="author-info">
                                                    <span className="author-name">{review.customerName || 'Anonymous'}</span>
                                                    <div className="review-stars">
                                                        {[...Array(5)].map((_, i) => (
                                                            <span key={i} className={i < review.rating ? 'star filled' : 'star'}>
                                                                ★
                                                            </span>
                                                        ))}
                                                    </div>
                                                </div>
                                            </div>
                                            <span className="review-date">
                                                {new Date(review.createdAt).toLocaleDateString('en-US', {
                                                    year: 'numeric',
                                                    month: 'long',
                                                    day: 'numeric'
                                                })}
                                            </span>
                                        </div>

                                        {review.title && (
                                            <h4 className="review-title">{review.title}</h4>
                                        )}

                                        {review.comment && (
                                            <p className="review-comment">{review.comment}</p>
                                        )}
                                    </div>
                                ))}
                            </>
                        )}
                    </div>
                </div>
            </div>
        </div>
    );
};

export default ProductDetailPage;
