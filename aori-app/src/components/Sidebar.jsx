/**
 * Sidebar.jsx
 * 
 * @author Yunhe
 * @date 2025-10-08
 * @version 1.1
 * 
 * Changed Sidebar categories to buttons/event handlers instead of pure anchors.
 * 
 * @author Sun Rui
 * @date 2025-10-11
 * @version 1.2
 */
import React from 'react';
import '../styles/Sidebar.css';

const Sidebar = () => {
    const categories = ['UNISEX', 'MEN', 'WOMEN', 'BOYS', 'GIRLS'];

    const handleCategoryClick = (category) => {
        const normalized = category.toLowerCase();
        window.location.hash = `#products?broad=${encodeURIComponent(normalized)}`;
    };

    return (
        <aside className="sidebar">
            <div className="category-list">
                {categories.map((category, index) => (
                    <button
                        type="button"
                        key={index}
                        className="category-link"
                        onClick={() => handleCategoryClick(category)}
                    >
                        {category}
                    </button>
                ))}
            </div>

            <div className="search-container">
                <svg className="search-icon" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                    <circle cx="11" cy="11" r="8"></circle>
                    <path d="m21 21-4.35-4.35"></path>
                </svg>
                <input
                    type="text"
                    className="search-input"
                    placeholder="SEARCH"
                />
            </div>
        </aside>
    );
};

export default Sidebar;
