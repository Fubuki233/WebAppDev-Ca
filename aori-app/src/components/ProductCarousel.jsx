/**
 *  ProductCarousel.jsx
 * 
 * @author Yunhe
 * @date 2025-10-08
 * @version 1.1
 */
import React, { useState, useEffect } from 'react';
import '../styles/ProductCarousel.css';

const ProductCarousel = ({ products }) => {
    const [currentIndex, setCurrentIndex] = useState(0);

    const nextSlide = () => {
        setCurrentIndex((prevIndex) =>
            prevIndex === products.length - 1 ? 0 : prevIndex + 1
        );
    };

    const prevSlide = () => {
        setCurrentIndex((prevIndex) =>
            prevIndex === 0 ? products.length - 1 : prevIndex - 1
        );
    };

    return (
        <div className="carousel-container">
            <div className="carousel-content">
                <div className="carousel-text">
                    <h2 className="carousel-title">NEW</h2>
                    <h2 className="carousel-title">COLLECTION</h2>
                    <p className="carousel-season">FALL</p>
                    <p className="carousel-year">2025</p>

                    <a href="#products" className="shop-button">
                        Go To Shop
                        <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                            <line x1="5" y1="12" x2="19" y2="12"></line>
                            <polyline points="12 5 19 12 12 19"></polyline>
                        </svg>
                    </a>
                </div>

                <div className="carousel-images">
                    <div className="carousel-track" style={{ transform: `translateX(-${currentIndex * 370}px)` }}>
                        {products.map((product, index) => (
                            <div
                                key={index}
                                className="carousel-slide"
                                onClick={() => window.location.hash = `#product/${product.id}`}
                                style={{ cursor: 'pointer' }}
                                title={`View ${product.name}`}
                            >
                                <img src={product.image} alt={product.name} />
                                <div className="carousel-slide-overlay">
                                    <span className="view-details">View Details</span>
                                </div>
                            </div>
                        ))}
                    </div>
                </div>

                <div className="carousel-controls">
                    <button className="carousel-button" onClick={prevSlide}>
                        <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                            <polyline points="15 18 9 12 15 6"></polyline>
                        </svg>
                    </button>
                    <button className="carousel-button" onClick={nextSlide}>
                        <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                            <polyline points="9 18 15 12 9 6"></polyline>
                        </svg>
                    </button>
                </div>
            </div>
        </div>
    );
};

export default ProductCarousel;
