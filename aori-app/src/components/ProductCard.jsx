/**
 *  ProductCard.jsx
 * 
 * @author Yunhe
 * @date 2025-10-08
 * @version 1.1
 */
import React, { useState, useEffect } from 'react';
import { toggleFavourite, isInFavourites } from '../api/favouritesApi';
import '../styles/ProductCard.css';

const ProductCard = ({ product }) => {
    const [isFavorite, setIsFavorite] = useState(false);

    useEffect(() => {
        const checkFavorite = async () => {
            const isFav = await isInFavourites(product.id);
            setIsFavorite(isFav);
        };
        checkFavorite();
    }, [product.id]);

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

        if (result.success) {
            setIsFavorite(result.added);
        }
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
            </div>

            <div className="product-info">
                <div className="product-type">
                    <span>{product.type}</span>
                    {product.availableColors > 1 && (
                        <span className="color-indicator">+{product.availableColors}</span>
                    )}
                </div>

                <h3 className="product-name">{product.name}</h3>

                <div className="product-colors">
                    {product.colors.map((color, index) => (
                        <span
                            key={index}
                            className="color-dot"
                            style={{ backgroundColor: color }}
                            title={color}
                        />
                    ))}
                </div>

                <div className="product-footer">
                    <span className="product-price">$ {product.price}</span>
                    {product.tags.includes('new') && (
                        <span className="tag-badge new-badge">NEW</span>
                    )}
                    {product.tags.includes('best-seller') && (
                        <span className="tag-badge bestseller-badge">BEST</span>
                    )}
                </div>
            </div>
        </div>
    );
};

export default ProductCard;
