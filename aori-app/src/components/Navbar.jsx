/**
 * Navbar.jsx
 * 
 * @author Yunhe
 * @date 2025-10-08
 * @version 1.1
 * 
 * @version 1.2 - Added hamburger menu with categories and search
 * @date 2025-10-11
 */
import React, { useState } from 'react';
import '../styles/Navbar.css';
import aoriLogo from '../aori.png';

const Navbar = () => {
    const [isMobileMenuOpen, setIsMobileMenuOpen] = useState(false);
    const [searchTerm, setSearchTerm] = useState('');

    const categories = ['UNISEX', 'MEN', 'WOMEN', 'BOYS', 'GIRLS'];

    const toggleMobileMenu = () => {
        setIsMobileMenuOpen(!isMobileMenuOpen);
    };

    const handleCategoryClick = (category) => {
        const normalized = category.toLowerCase();
        window.location.hash = `#products?broad=${normalized}`;
        setIsMobileMenuOpen(false);
    };

    const handleSearchSubmit = (e) => {
        e.preventDefault();
        const trimmed = searchTerm.trim();
        if (trimmed) {
            window.location.hash = `#products?search=${trimmed}`;
            setIsMobileMenuOpen(false);
            setSearchTerm('');
        }
    };

    return (
        <nav className="navbar">
            <div className="navbar-container">
                {/* Left Section: Hamburger + Menu Links */}
                <div className="navbar-left">
                    <button
                        className={`hamburger-button ${isMobileMenuOpen ? 'active' : ''}`}
                        onClick={toggleMobileMenu}
                        aria-label="Toggle menu"
                    >
                        <span></span>
                        <span></span>
                        <span></span>
                    </button>

                    <div className="navbar-menu">
                        <a href="#home" onClick={(e) => { e.preventDefault(); window.location.hash = ''; }} className="nav-link">Home</a>
                        <a href="#collections" className="nav-link">Collections</a>
                        <a href="#new" className="nav-link">New</a>
                        <a href="#products" className="nav-link">Products</a>
                    </div>
                </div>

                {/* Center Section: Logo */}
                <div className="navbar-logo">
                    <a href="#home" onClick={(e) => { e.preventDefault(); window.location.hash = ''; }} style={{ textDecoration: 'none', color: 'inherit', display: 'flex', alignItems: 'center', cursor: 'pointer' }}>
                        <img src={aoriLogo} alt="AORI" className="logo-image" />
                    </a>
                </div>

                {/* Right Section: Icons */}
                <div className="navbar-icons">
                    <button className="icon-button" onClick={() => window.location.hash = '#favourites'} title="Favourites">
                        <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                            <path d="M20.84 4.61a5.5 5.5 0 0 0-7.78 0L12 5.67l-1.06-1.06a5.5 5.5 0 0 0-7.78 7.78l1.06 1.06L12 21.23l7.78-7.78 1.06-1.06a5.5 5.5 0 0 0 0-7.78z"></path>
                        </svg>
                    </button>

                    <button className="icon-button cart-button" onClick={() => window.location.hash = '#cart'} title="Cart">
                        <span className="cart-text">Cart</span>
                        <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                            <circle cx="9" cy="21" r="1"></circle>
                            <circle cx="20" cy="21" r="1"></circle>
                            <path d="M1 1h4l2.68 13.39a2 2 0 0 0 2 1.61h9.72a2 2 0 0 0 2-1.61L23 6H6"></path>
                        </svg>
                    </button>

                    <button className="icon-button" onClick={() => window.location.hash = '#profile'} title="My Profile">
                        <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                            <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"></path>
                            <circle cx="12" cy="7" r="4"></circle>
                        </svg>
                    </button>
                </div>
            </div>

            {/* Slide-out Mobile Menu */}
            <div className={`mobile-menu ${isMobileMenuOpen ? 'open' : ''}`}>
                <div className="mobile-menu-content">
                    {/* Categories */}
                    <div className="mobile-menu-section">
                        <div className="category-list-mobile">
                            {categories.map((category, index) => (
                                <button
                                    key={index}
                                    className="category-link-mobile"
                                    onClick={() => handleCategoryClick(category)}
                                >
                                    {category}
                                </button>
                            ))}
                        </div>
                    </div>

                    {/* Search */}
                    <div className="mobile-menu-section">
                        <form className="search-container-mobile" onSubmit={handleSearchSubmit}>
                            <svg className="search-icon-mobile" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                                <circle cx="11" cy="11" r="8"></circle>
                                <path d="m21 21-4.35-4.35"></path>
                            </svg>
                            <input
                                type="text"
                                className="search-input-mobile"
                                placeholder="SEARCH"
                                value={searchTerm}
                                onChange={(e) => setSearchTerm(e.target.value)}
                            />
                        </form>
                    </div>
                </div>
            </div>

            {/* Overlay */}
            {isMobileMenuOpen && (
                <div className="mobile-menu-overlay" onClick={toggleMobileMenu}></div>
            )}
        </nav>
    );
};

export default Navbar;
