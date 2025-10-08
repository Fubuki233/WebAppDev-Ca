import React from 'react';
import '../styles/Sidebar.css';

const Sidebar = () => {
    const categories = ['UNISEX', 'MEN', 'WOMEN', 'BOYS', 'GIRLS'];

    return (
        <aside className="sidebar">
            <div className="category-list">
                {categories.map((category, index) => (
                    <a href={`#${category.toLowerCase()}`} key={index} className="category-link">
                        {category}
                    </a>
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
