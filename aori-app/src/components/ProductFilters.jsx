/**
 *  ProductFilters.jsx
 * 
 * @author Yunhe
 * @date 2025-10-08
 * @version 1.1
 */
import React, { useState } from 'react';
import '../styles/ProductFilters.css';

const ProductFilters = ({ onFilterChange, categories, activeCategory, availableColors = [], availableCollections = [], availableTags = [] }) => {
    const [selectedSize, setSelectedSize] = useState('');
    const [showAvailable, setShowAvailable] = useState(false);
    const [showOutOfStock, setShowOutOfStock] = useState(false);
    const [selectedColors, setSelectedColors] = useState([]);
    const [priceMin, setPriceMin] = useState('');
    const [priceMax, setPriceMax] = useState('');
    const [selectedCollections, setSelectedCollections] = useState([]);
    const [selectedTags, setSelectedTags] = useState([]);
    const [selectedRating, setSelectedRating] = useState(0);
    const [expandedSections, setExpandedSections] = useState({
        availability: true,
        category: true,
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

    const handleCategoryChange = (categorySlug) => {
        // Toggle: if clicking the same category, clear it (set to 'all'); otherwise select it
        const newCategory = activeCategory === categorySlug ? '' : categorySlug;
        onFilterChange({ category: newCategory });
    };

    const handleColorChange = (color) => {
        const newColors = selectedColors.includes(color)
            ? selectedColors.filter(c => c !== color)
            : [...selectedColors, color];
        setSelectedColors(newColors);
        onFilterChange({ colors: newColors.length > 0 ? newColors : undefined });
    };

    const handlePriceChange = () => {
        const filters = {};
        if (priceMin && priceMin.trim() !== '') {
            filters.priceMin = parseFloat(priceMin);
        } else {
            filters.priceMin = undefined;
        }
        if (priceMax && priceMax.trim() !== '') {
            filters.priceMax = parseFloat(priceMax);
        } else {
            filters.priceMax = undefined;
        }
        onFilterChange(filters);
    };

    const handleCollectionChange = (collection) => {
        const newCollections = selectedCollections.includes(collection)
            ? selectedCollections.filter(c => c !== collection)
            : [...selectedCollections, collection];
        setSelectedCollections(newCollections);
        onFilterChange({ collections: newCollections.length > 0 ? newCollections : undefined });
    };

    const handleTagChange = (tag) => {
        const newTags = selectedTags.includes(tag)
            ? selectedTags.filter(t => t !== tag)
            : [...selectedTags, tag];
        setSelectedTags(newTags);
        onFilterChange({ tags: newTags.length > 0 ? newTags : undefined });
    };

    const handleRatingChange = (rating) => {
        const newRating = selectedRating === rating ? 0 : rating;
        setSelectedRating(newRating);
        onFilterChange({ rating: newRating > 0 ? newRating : undefined });
    };

    const toggleSection = (section) => {
        setExpandedSections(prev => ({
            ...prev,
            [section]: !prev[section]
        }));
    };

    const clearAllFilters = () => {
        setSelectedSize('');
        setShowAvailable(false);
        setShowOutOfStock(false);
        setSelectedColors([]);
        setPriceMin('');
        setPriceMax('');
        setSelectedCollections([]);
        setSelectedTags([]);
        setSelectedRating(0);
        onFilterChange({
            size: undefined,
            inStock: undefined,
            colors: undefined,
            priceMin: undefined,
            priceMax: undefined,
            collections: undefined,
            tags: undefined,
            rating: undefined,
            category: undefined
        });
    };

    const hasActiveFilters = selectedSize || showAvailable || showOutOfStock ||
        selectedColors.length > 0 || priceMin || priceMax ||
        selectedCollections.length > 0 || selectedTags.length > 0 || selectedRating > 0;

    return (
        <aside className="product-filters">
            <div className="filters-header">
                <h2>FILTERS</h2>
                {hasActiveFilters && (
                    <button className="clear-filters-btn" onClick={clearAllFilters}>
                        Clear All
                    </button>
                )}
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

                {expandedSections.category && (
                    <div className="filter-options">
                        {categories && categories.length > 0 ? (
                            categories.map((category) => {
                                const categorySlug = category.slug || category.category;
                                const isChecked = activeCategory === categorySlug;
                                return (
                                    <label
                                        key={category.categoryId || category.id}
                                        className="checkbox-label"
                                        style={{ cursor: 'pointer' }}
                                    >
                                        <input
                                            type="checkbox"
                                            checked={isChecked}
                                            onChange={() => handleCategoryChange(categorySlug)}
                                        />
                                        <span>
                                            {category.categoryName || category.name}
                                            {category.count !== undefined && <span className="count"> ({category.count})</span>}
                                        </span>
                                    </label>
                                );
                            })
                        ) : (
                            <p className="no-data">Loading categories...</p>
                        )}
                    </div>
                )}
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

                {expandedSections.colors && (
                    <div className="filter-options">
                        <div className="color-swatches">
                            {availableColors && availableColors.length > 0 ? (
                                availableColors.map((color) => (
                                    <div
                                        key={color}
                                        className={`color-swatch ${selectedColors.includes(color) ? 'selected' : ''}`}
                                        style={{ backgroundColor: color }}
                                        onClick={() => handleColorChange(color)}
                                        title={color}
                                    >
                                        {selectedColors.includes(color) && (
                                            <svg width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="white" strokeWidth="3">
                                                <polyline points="20 6 9 17 4 12"></polyline>
                                            </svg>
                                        )}
                                    </div>
                                ))
                            ) : (
                                <p className="no-data">No colors available</p>
                            )}
                        </div>
                    </div>
                )}
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

                {expandedSections.priceRange && (
                    <div className="filter-options">
                        <div className="price-range-inputs">
                            <input
                                type="number"
                                placeholder="Min"
                                value={priceMin}
                                onChange={(e) => setPriceMin(e.target.value)}
                                className="price-input"
                            />
                            <span className="price-separator">-</span>
                            <input
                                type="number"
                                placeholder="Max"
                                value={priceMax}
                                onChange={(e) => setPriceMax(e.target.value)}
                                className="price-input"
                            />
                        </div>
                        <button onClick={handlePriceChange} className="apply-price-btn">
                            Apply
                        </button>
                    </div>
                )}
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

                {expandedSections.collections && (
                    <div className="filter-options">
                        {availableCollections && availableCollections.length > 0 ? (
                            availableCollections.map((collection) => (
                                <label
                                    key={collection}
                                    className="checkbox-label"
                                    style={{ cursor: 'pointer' }}
                                >
                                    <input
                                        type="checkbox"
                                        checked={selectedCollections.includes(collection)}
                                        onChange={() => handleCollectionChange(collection)}
                                    />
                                    <span>{collection}</span>
                                </label>
                            ))
                        ) : (
                            <p className="no-data">No collections available</p>
                        )}
                    </div>
                )}
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

                {expandedSections.tags && (
                    <div className="filter-options">
                        {availableTags && availableTags.length > 0 ? (
                            availableTags.map((tag) => (
                                <label
                                    key={tag}
                                    className="checkbox-label tag-label"
                                    style={{ cursor: 'pointer' }}
                                >
                                    <input
                                        type="checkbox"
                                        checked={selectedTags.includes(tag)}
                                        onChange={() => handleTagChange(tag)}
                                    />
                                    <span className="tag-chip">{tag}</span>
                                </label>
                            ))
                        ) : (
                            <p className="no-data">No tags available</p>
                        )}
                    </div>
                )}
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

                {expandedSections.ratings && (
                    <div className="filter-options">
                        {[5, 4, 3, 2, 1].map((rating) => (
                            <label
                                key={rating}
                                className="checkbox-label rating-label"
                                style={{ cursor: 'pointer' }}
                            >
                                <input
                                    type="radio"
                                    name="rating"
                                    checked={selectedRating === rating}
                                    onChange={() => handleRatingChange(rating)}
                                />
                                <span className="rating-stars">
                                    {[...Array(5)].map((_, i) => (
                                        <span key={i} className={i < rating ? 'star filled' : 'star'}>
                                            ★
                                        </span>
                                    ))}
                                    <span className="rating-text"> & Up</span>
                                </span>
                            </label>
                        ))}
                    </div>
                )}
            </div>
        </aside>
    );
};

export default ProductFilters;
