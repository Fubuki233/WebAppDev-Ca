/**
 *  ProductFilters.jsx
 * 
 * @author Yunhe
 * @date 2025-10-08
 * @version 1.1
 */
import React, { useState } from 'react';
import '../styles/ProductFilters.css';

const ProductFilters = ({ onFilterChange, categories }) => {
    const [selectedSize, setSelectedSize] = useState('');
    const [showAvailable, setShowAvailable] = useState(false);
    const [showOutOfStock, setShowOutOfStock] = useState(false);
    const [expandedSections, setExpandedSections] = useState({
        availability: true,
        category: false,
        colors: false,
        priceRange: false,
        collections: false,
        tags: false,
        ratings: false,
    });

    const sizes = ['XS', 'S', 'M', 'L', 'XL', '2X'];

    const handleSizeClick = (size) => {
        const newSize = selectedSize === size ? '' : size;
        setSelectedSize(newSize);
        onFilterChange({ size: newSize });
    };

    const handleAvailabilityChange = (type) => {
        let inStock = undefined;

        if (type === 'available') {
            const newValue = !showAvailable;
            setShowAvailable(newValue);
            setShowOutOfStock(false);
            inStock = newValue ? true : undefined;
        } else if (type === 'outOfStock') {
            const newValue = !showOutOfStock;
            setShowOutOfStock(newValue);
            setShowAvailable(false);
            inStock = newValue ? false : undefined;
        }

        onFilterChange({ inStock });
    };

    const toggleSection = (section) => {
        setExpandedSections(prev => ({
            ...prev,
            [section]: !prev[section]
        }));
    };

    return (
        <aside className="product-filters">
            <div className="filters-header">
                <h2>FILTERS</h2>
            </div>

            {/* SIZE */}
            <div className="filter-section">
                <h3 className="filter-title">SIZE</h3>
                <div className="size-buttons">
                    {sizes.map((size) => (
                        <button
                            key={size}
                            className={`size-button ${selectedSize === size ? 'active' : ''}`}
                            onClick={() => handleSizeClick(size)}
                        >
                            {size}
                        </button>
                    ))}
                </div>
            </div>

            <div className="filter-section">
                <div
                    className="filter-title-clickable"
                    onClick={() => toggleSection('availability')}
                >
                    <h3 className="filter-title">AVAILABILITY</h3>
                    <svg
                        className={`arrow-icon ${expandedSections.availability ? 'expanded' : ''}`}
                        width="16"
                        height="16"
                        viewBox="0 0 24 24"
                        fill="none"
                        stroke="currentColor"
                        strokeWidth="2"
                    >
                        <polyline points="6 9 12 15 18 9"></polyline>
                    </svg>
                </div>

                {expandedSections.availability && (
                    <div className="filter-options">
                        <label className="checkbox-label">
                            <input
                                type="checkbox"
                                checked={showAvailable}
                                onChange={() => handleAvailabilityChange('available')}
                            />
                            <span>Availability <span className="count">(450)</span></span>
                        </label>
                        <label className="checkbox-label">
                            <input
                                type="checkbox"
                                checked={showOutOfStock}
                                onChange={() => handleAvailabilityChange('outOfStock')}
                            />
                            <span>Out Of Stack <span className="count">(18)</span></span>
                        </label>
                    </div>
                )}
            </div>

            <div className="filter-section">
                <div
                    className="filter-title-clickable"
                    onClick={() => toggleSection('category')}
                >
                    <h3 className="filter-title">CATEGORY</h3>
                    <svg
                        className={`arrow-icon ${expandedSections.category ? 'expanded' : ''}`}
                        width="16"
                        height="16"
                        viewBox="0 0 24 24"
                        fill="none"
                        stroke="currentColor"
                        strokeWidth="2"
                    >
                        <polyline points="6 9 12 15 18 9"></polyline>
                    </svg>
                </div>
            </div>

            <div className="filter-section">
                <div
                    className="filter-title-clickable"
                    onClick={() => toggleSection('colors')}
                >
                    <h3 className="filter-title">COLORS</h3>
                    <svg
                        className={`arrow-icon ${expandedSections.colors ? 'expanded' : ''}`}
                        width="16"
                        height="16"
                        viewBox="0 0 24 24"
                        fill="none"
                        stroke="currentColor"
                        strokeWidth="2"
                    >
                        <polyline points="6 9 12 15 18 9"></polyline>
                    </svg>
                </div>
            </div>

            <div className="filter-section">
                <div
                    className="filter-title-clickable"
                    onClick={() => toggleSection('priceRange')}
                >
                    <h3 className="filter-title">PRICE RANGE</h3>
                    <svg
                        className={`arrow-icon ${expandedSections.priceRange ? 'expanded' : ''}`}
                        width="16"
                        height="16"
                        viewBox="0 0 24 24"
                        fill="none"
                        stroke="currentColor"
                        strokeWidth="2"
                    >
                        <polyline points="6 9 12 15 18 9"></polyline>
                    </svg>
                </div>
            </div>

            <div className="filter-section">
                <div
                    className="filter-title-clickable"
                    onClick={() => toggleSection('collections')}
                >
                    <h3 className="filter-title">COLLECTIONS</h3>
                    <svg
                        className={`arrow-icon ${expandedSections.collections ? 'expanded' : ''}`}
                        width="16"
                        height="16"
                        viewBox="0 0 24 24"
                        fill="none"
                        stroke="currentColor"
                        strokeWidth="2"
                    >
                        <polyline points="6 9 12 15 18 9"></polyline>
                    </svg>
                </div>
            </div>

            {/* TAGS */}
            <div className="filter-section">
                <div
                    className="filter-title-clickable"
                    onClick={() => toggleSection('tags')}
                >
                    <h3 className="filter-title">TAGS</h3>
                    <svg
                        className={`arrow-icon ${expandedSections.tags ? 'expanded' : ''}`}
                        width="16"
                        height="16"
                        viewBox="0 0 24 24"
                        fill="none"
                        stroke="currentColor"
                        strokeWidth="2"
                    >
                        <polyline points="6 9 12 15 18 9"></polyline>
                    </svg>
                </div>
            </div>

            {/* RATINGS */}
            <div className="filter-section">
                <div
                    className="filter-title-clickable"
                    onClick={() => toggleSection('ratings')}
                >
                    <h3 className="filter-title">RATINGS</h3>
                    <svg
                        className={`arrow-icon ${expandedSections.ratings ? 'expanded' : ''}`}
                        width="16"
                        height="16"
                        viewBox="0 0 24 24"
                        fill="none"
                        stroke="currentColor"
                        strokeWidth="2"
                    >
                        <polyline points="6 9 12 15 18 9"></polyline>
                    </svg>
                </div>
            </div>
        </aside>
    );
};

export default ProductFilters;
