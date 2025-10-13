/**
    * ProductDetailPage.jsx 
 * 
 * @author Yunhe
 * @date 2025-10-08
 * @version 1.1
 * 
 * @author Yunhe
 * @date 2025-10-13
 * @version 1.2 - Added SKU validation
 */
import React, { useState, useEffect } from 'react';
import Navbar from './Navbar';
import { fetchProductById } from '../api/productApi';
import { addToCart } from '../api/cartApi';
import { toggleFavourite, isInFavourites } from '../api/favouritesApi';
import { addViewHistory } from '../api/viewHistoryApi';
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
    const [availableQuantity, setAvailableQuantity] = useState(null);
    const [isCheckingStock, setIsCheckingStock] = useState(false);

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

    // Check SKU availability when color or size changes
    useEffect(() => {
        const checkSkuAvailability = async () => {
            if (product && selectedColor && selectedSize) {
                setIsCheckingStock(true);
                try {
                    const qty = await getSkuQuantity(product.id, selectedColor, selectedSize);
                    setAvailableQuantity(qty);
                    console.log(`[ProductDetailPage] Available quantity: ${qty}`);
                } catch (error) {
                    console.error('[ProductDetailPage] Error checking SKU:', error);
                    setAvailableQuantity(0);
                } finally {
                    setIsCheckingStock(false);
                }
            } else {
                setAvailableQuantity(null);
            }
        };

        checkSkuAvailability();
    }, [product, selectedColor, selectedSize]);

    const handleAddToCart = async () => {
        if (!selectedSize) {
            alert('Please select a size');
            return;
        }

        if (availableQuantity !== null && availableQuantity < quantity) {
            alert(`Only ${availableQuantity} items available in stock`);
            return;
        }

        if (availableQuantity === 0) {
            alert('This item is currently out of stock');
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
            sku: `${product.id}-${selectedColor}-${selectedSize}`
        };

        const result = await addToCart(cartItem);

        if (result.success) {
            alert(`✓ Added to cart!\n\n${product.name}\nSize: ${selectedSize}\nQuantity: ${quantity}`);

            window.dispatchEvent(new Event('cartUpdated'));
        } else {
            alert('Failed to add to cart. Please try again.');
        }
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
            console.log('Login required, redirecting to login page');
            window.location.hash = '#login';
        } else if (result.success) {
            // Use the 'added' field from backend to set the correct state
            setIsFavorite(result.added);
            // No alert - silent toggle
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

                        {/* Stock Status Display */}
                        {selectedColor && selectedSize && (
                            <div className="stock-status-detail">
                                {isCheckingStock ? (
                                    <span className="checking-stock">Checking availability...</span>
                                ) : availableQuantity !== null ? (
                                    availableQuantity > 0 ? (
                                        <span className="in-stock-detail">
                                            In Stock
                                        </span>
                                    ) : (
                                        <span className="out-of-stock-detail">
                                            Out of Stock
                                        </span>
                                    )
                                ) : null}
                            </div>
                        )}

                        <button
                            className="add-to-cart-button"
                            onClick={handleAddToCart}
                            disabled={!selectedSize || availableQuantity === 0 || isCheckingStock}
                        >
                            {!selectedSize ? 'SELECT SIZE' : availableQuantity === 0 ? 'OUT OF STOCK' : 'ADD'}
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
            </div>
        </div>
    );
};

export default ProductDetailPage;
