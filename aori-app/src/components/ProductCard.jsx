import React from 'react';
import '../styles/ProductCard.css';

const ProductCard = ({ product }) => {
    const handleClick = () => {
        window.location.hash = `#product/${product.id}`;
    };

    return (
        <div className="product-card" onClick={handleClick}>
            <div className="product-image-container">
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
