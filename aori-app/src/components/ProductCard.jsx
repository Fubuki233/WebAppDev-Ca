/**
 *  ProductCard.jsx
 * 
 * @author Yunhe
 * @date 2025-10-08
 * @version 1.2 - Added SKU validation with color/size selection
 */
import React, { useState, useEffect } from 'react';
import { toggleFavourite, isInFavourites } from '../api/favouritesApi';
import { getSkuQuantity } from '../api/skuApi';
import { addToCart } from '../api/cartApi';
import '../styles/ProductCard.css';

const ProductCard = ({ product }) => {
    const [isFavorite, setIsFavorite] = useState(false);
    const [showQuickAdd, setShowQuickAdd] = useState(false);
    const [selectedColor, setSelectedColor] = useState('');
    const [selectedSize, setSelectedSize] = useState('');
    const [availableQuantity, setAvailableQuantity] = useState(null);
    const [isChecking, setIsChecking] = useState(false);

    useEffect(() => {
        const checkFavorite = async () => {
            const isFav = await isInFavourites(product.id);
            setIsFavorite(isFav);
        };
        checkFavorite();
    }, [product.id]);

    useEffect(() => {
        // Initialize with first color if available
        if (product.colors && product.colors.length > 0) {
            setSelectedColor(product.colors[0]);
        }
    }, [product]);

    useEffect(() => {
        // Check SKU quantity when both color and size are selected
        const checkSkuQuantity = async () => {
            if (selectedColor && selectedSize && product.id) {
                setIsChecking(true);
                try {
                    const quantity = await getSkuQuantity(product.id, selectedColor, selectedSize);
                    setAvailableQuantity(quantity);
                    console.log(`[ProductCard] SKU quantity for ${product.id}: ${quantity}`);
                } catch (error) {
                    console.error('[ProductCard] Error checking SKU quantity:', error);
                    setAvailableQuantity(0);
                } finally {
                    setIsChecking(false);
                }
            } else {
                setAvailableQuantity(null);
            }
        };

        checkSkuQuantity();
    }, [selectedColor, selectedSize, product.id]);

    const handleClick = () => {
        window.location.hash = `#product/${product.id}`;
    };

    const handleToggleFavorite = async (e) => {
        e.stopPropagation(); // Prevent card click

        const favouriteItem = {
            productId: product.id,
            name: product.name,
            price: product.price,
            image: product.image,
        };

        const result = await toggleFavourite(favouriteItem);

        if (result.requiresLogin) {
            // User is not logged in, redirect to login page
            console.log('Login required, redirecting to login page');
            window.location.hash = '#login';
        } else if (result.success) {
            setIsFavorite(result.added);
        }
    };

    const handleQuickAdd = (e) => {
        e.stopPropagation(); // Prevent card click
        setShowQuickAdd(!showQuickAdd);
    };

    const handleAddToCart = async (e) => {
        e.stopPropagation();

        if (!selectedSize) {
            alert('Please select a size');
            return;
        }

        if (availableQuantity <= 0) {
            alert('This item is out of stock');
            return;
        }

        const cartItem = {
            productId: product.id,
            name: product.name,
            price: product.price,
            color: selectedColor,
            size: selectedSize,
            quantity: 1,
            image: product.image,
            sku: `${product.id}&${selectedColor}&${selectedSize}`
        };

        const result = await addToCart(cartItem);

        if (result.requiresLogin) {
            window.location.hash = '#login';
        } else if (result.success) {
            alert('✓ Added to cart!');
            window.dispatchEvent(new Event('cartUpdated'));
            setShowQuickAdd(false);
        } else {
            alert('Failed to add to cart. Please try again.');
        }
    };

    const handleColorSelect = (e, color) => {
        e.stopPropagation();
        setSelectedColor(color);
    };

    const handleSizeSelect = (e, size) => {
        e.stopPropagation();
        setSelectedSize(size);
    };

    return (
        <div className="product-card" onClick={handleClick}>
            <div className="product-image-container">
                <button
                    className={`favorite-button-card ${isFavorite ? 'is-favorite' : ''}`}
                    onClick={handleToggleFavorite}
                    title={isFavorite ? 'Remove from favourites' : 'Add to favourites'}
                >
                    <svg
                        width="20"
                        height="20"
                        viewBox="0 0 24 24"
                        fill={isFavorite ? 'currentColor' : 'none'}
                        stroke="currentColor"
                        strokeWidth="2"
                    >
                        <path d="M20.84 4.61a5.5 5.5 0 0 0-7.78 0L12 5.67l-1.06-1.06a5.5 5.5 0 0 0-7.78 7.78l1.06 1.06L12 21.23l7.78-7.78 1.06-1.06a5.5 5.5 0 0 0 0-7.78z"></path>
                    </svg>
                </button>
                <img src={product.image} alt={product.name} className="product-image" />
                {!product.inStock && (
                    <div className="out-of-stock-badge">Out of Stock</div>
                )}

                {/* Quick Add Button */}
                <button
                    className="quick-add-button"
                    onClick={handleQuickAdd}
                    title="Quick add to cart"
                >
                    <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                        <circle cx="9" cy="21" r="1"></circle>
                        <circle cx="20" cy="21" r="1"></circle>
                        <path d="M1 1h4l2.68 13.39a2 2 0 0 0 2 1.61h9.72a2 2 0 0 0 2-1.61L23 6H6"></path>
                    </svg>
                </button>
            </div>

            <div className="product-info">
                <div className="product-type">
                    <span>{product.type}</span>
                    {product.availableColors > 1 && (
                        <span className="color-indicator">+{product.availableColors}</span>
                    )}
                </div>

                <h3 className="product-name">{product.name}</h3>

                {/* Quick Add Panel */}
                {showQuickAdd && (
                    <div className="quick-add-panel" onClick={(e) => e.stopPropagation()}>
                        {/* Color Selection */}
                        {product.colors && product.colors.length > 0 && (
                            <div className="quick-add-section">
                                <label className="quick-add-label">Color:</label>
                                <div className="color-options">
                                    {product.colors.map((color, index) => (
                                        <button
                                            key={index}
                                            className={`color-option ${selectedColor === color ? 'selected' : ''}`}
                                            style={{ backgroundColor: color }}
                                            onClick={(e) => handleColorSelect(e, color)}
                                            title={color}
                                        />
                                    ))}
                                </div>
                            </div>
                        )}

                        {/* Size Selection */}
                        {product.sizes && product.sizes.length > 0 && (
                            <div className="quick-add-section">
                                <label className="quick-add-label">Size:</label>
                                <div className="size-options">
                                    {product.sizes.map((size, index) => (
                                        <button
                                            key={index}
                                            className={`size-option ${selectedSize === size ? 'selected' : ''}`}
                                            onClick={(e) => handleSizeSelect(e, size)}
                                        >
                                            {size}
                                        </button>
                                    ))}
                                </div>
                            </div>
                        )}

                        {/* Stock Status */}
                        {selectedColor && selectedSize && (
                            <div className="stock-status">
                                {isChecking ? (
                                    <span className="checking">Checking stock...</span>
                                ) : availableQuantity !== null ? (
                                    availableQuantity > 0 ? (
                                        <span className="in-stock">in stock</span>
                                    ) : (
                                        <span className="out-of-stock">Out of stock</span>
                                    )
                                ) : null}
                            </div>
                        )}

                        {/* Add to Cart Button */}
                        <button
                            className={`add-to-cart-btn ${(!selectedSize || availableQuantity === 0) ? 'disabled' : ''}`}
                            onClick={handleAddToCart}
                            disabled={!selectedSize || availableQuantity === 0 || isChecking}
                        >
                            {!selectedSize ? 'Select Size' : availableQuantity === 0 ? 'Out of Stock' : 'Add to Cart'}
                        </button>
                    </div>
                )}

                <div className="product-colors">
                    {product.colors && product.colors.map((color, index) => (
                        <span
                            key={index}
                            className="color-dot"
                            style={{ backgroundColor: color }}
                            title={color}
                        />
                    ))}
                </div>

                {product.rating && (
                    <div className="product-rating">
                        <div className="stars">
                            {[...Array(5)].map((_, i) => (
                                <span key={i} className={i < Math.floor(product.rating) ? 'star filled' : 'star'}>
                                    ★
                                </span>
                            ))}
                        </div>
                        <span className="rating-value">{product.rating.toFixed(1)}</span>
                    </div>
                )}

                <div className="product-footer">
                    <span className="product-price">$ {product.price}</span>
                    {product.tags && product.tags.includes('new') && (
                        <span className="tag-badge new-badge">NEW</span>
                    )}
                    {product.tags && product.tags.includes('best-seller') && (
                        <span className="tag-badge bestseller-badge">BEST</span>
                    )}
                </div>
            </div>
        </div>
    );
};

export default ProductCard;
