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
 * @version 1.3
 * 
 * @author Yunhe
 * @version 2.0 - enhanced style and added a humbuger menu
 */
import React, { useEffect, useState } from 'react';
import '../styles/Sidebar.css';

const Sidebar = () => {
    const categories = ['UNISEX', 'MEN', 'WOMEN'];
    const [searchTerm, setSearchTerm] = useState('');

    useEffect(() => {
        const hash = window.location.hash;
        if (!hash.startsWith('#products')) return;
        const queryIndex = hash.indexOf('?');
        if (queryIndex === -1) return;
        const params = new URLSearchParams(hash.slice(queryIndex + 1));
        const existingSearch = params.get('search');
        if (existingSearch) {
            setSearchTerm(existingSearch);
        }
    }, []);

    const applyNavigation = (updates) => {
        const params = new URLSearchParams();
        const currentHash = window.location.hash;
        if (currentHash.startsWith('#products')) {
            const queryIndex = currentHash.indexOf('?');
            if (queryIndex !== -1) {
                const currentParams = new URLSearchParams(currentHash.slice(queryIndex + 1));
                currentParams.forEach((value, key) => params.set(key, value));
            }
        }

        Object.entries(updates).forEach(([key, value]) => {
            if (value === null || value === undefined || value === '') {
                params.delete(key);
            } else {
                params.set(key, value);
            }
        });

        const query = params.toString();
        window.location.hash = query ? `#products?${query}` : '#products';
    };

    const handleCategoryClick = (category) => {
        const normalized = category.toLowerCase();
        applyNavigation({ broad: normalized });
    };

    const handleSearchSubmit = (event) => {
        event.preventDefault();
        const trimmed = searchTerm.trim();
        applyNavigation({ search: trimmed || null });
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

            <form className="search-container" onSubmit={handleSearchSubmit}>
                <svg className="search-icon" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                    <circle cx="11" cy="11" r="8"></circle>
                    <path d="m21 21-4.35-4.35"></path>
                </svg>
                <input
                    type="text"
                    className="search-input"
                    placeholder="SEARCH"
                    value={searchTerm}
                    onChange={(event) => setSearchTerm(event.target.value)}
                />
            </form>
        </aside>
    );
};

export default Sidebar;
