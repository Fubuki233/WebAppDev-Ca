/**
 * RegisterPage.jsx 
 * 
 * @author Yunhe
 * @date 2025-10-09
 * @version 1.0
 */
import React, { useState } from 'react';
import { register } from '../api/authApi';
import Navbar from './Navbar';
import '../styles/RegisterPage.css';

const RegisterPage = () => {
    const [formData, setFormData] = useState({
        firstName: '',
        lastName: '',
        email: '',
        password: '',
        confirmPassword: '',
        phone: '',
        acceptTerms: false
    });
    const [errors, setErrors] = useState({});
    const [loading, setLoading] = useState(false);

    const handleChange = (e) => {
        const { name, value, type, checked } = e.target;
        setFormData(prev => ({
            ...prev,
            [name]: type === 'checkbox' ? checked : value
        }));
        // Clear error for this field
        if (errors[name]) {
            setErrors(prev => ({ ...prev, [name]: '' }));
        }
    };

    const validateForm = () => {
        const newErrors = {};

        if (!formData.firstName.trim()) {
            newErrors.firstName = 'First name is required';
        }

        if (!formData.lastName.trim()) {
            newErrors.lastName = 'Last name is required';
        }

        if (!formData.email.trim()) {
            newErrors.email = 'Email is required';
        } else if (!/\S+@\S+\.\S+/.test(formData.email)) {
            newErrors.email = 'Email is invalid';
        }

        if (!formData.password) {
            newErrors.password = 'Password is required';
        } else if (formData.password.length < 6) {
            newErrors.password = 'Password must be at least 6 characters';
        }

        if (formData.password !== formData.confirmPassword) {
            newErrors.confirmPassword = 'Passwords do not match';
        }

        if (formData.phone && !/^\d{8,}$/.test(formData.phone.replace(/\s/g, ''))) {
            newErrors.phone = 'Please enter a valid phone number';
        }

        if (!formData.acceptTerms) {
            newErrors.acceptTerms = 'You must accept the terms and conditions';
        }

        setErrors(newErrors);
        return Object.keys(newErrors).length === 0;
    };

    const handleSubmit = async (e) => {
        e.preventDefault();

        if (!validateForm()) {
            return;
        }

        setLoading(true);

        try {
            const response = await register({
                firstName: formData.firstName,
                lastName: formData.lastName,
                email: formData.email,
                password: formData.password,
                phone: formData.phone
            });

            if (response.success) {
                // Navigate to login page after successful registration using hash
                window.location.hash = '#login';
            } else {
                setErrors({ general: response.message || 'Registration failed. Please try again.' });
            }
        } catch (err) {
            console.error('Registration error:', err);
            setErrors({ general: 'An error occurred. Please try again later.' });
        } finally {
            setLoading(false);
        }
    };

    const handleLoginRedirect = () => {
        window.location.hash = '#login';
    };

    return (
        <div className="register-page">
            <Navbar />
            <div className="register-container">
                <div className="register-card">
                    <div className="register-header">
                        <h1>Create Account</h1>
                        <p>Sign up to start shopping</p>
                    </div>

                    <form onSubmit={handleSubmit} className="register-form">
                        {errors.general && (
                            <div className="error-message">
                                {errors.general}
                            </div>
                        )}

                        <div className="form-row">
                            <div className="form-group">
                                <label htmlFor="firstName">First Name *</label>
                                <input
                                    type="text"
                                    id="firstName"
                                    name="firstName"
                                    value={formData.firstName}
                                    onChange={handleChange}
                                    placeholder="Enter first name"
                                    disabled={loading}
                                    className={errors.firstName ? 'error' : ''}
                                />
                                {errors.firstName && (
                                    <span className="field-error">{errors.firstName}</span>
                                )}
                            </div>

                            <div className="form-group">
                                <label htmlFor="lastName">Last Name *</label>
                                <input
                                    type="text"
                                    id="lastName"
                                    name="lastName"
                                    value={formData.lastName}
                                    onChange={handleChange}
                                    placeholder="Enter last name"
                                    disabled={loading}
                                    className={errors.lastName ? 'error' : ''}
                                />
                                {errors.lastName && (
                                    <span className="field-error">{errors.lastName}</span>
                                )}
                            </div>
                        </div>

                        <div className="form-group">
                            <label htmlFor="email">Email Address *</label>
                            <input
                                type="email"
                                id="email"
                                name="email"
                                value={formData.email}
                                onChange={handleChange}
                                placeholder="Enter your email"
                                disabled={loading}
                                className={errors.email ? 'error' : ''}
                            />
                            {errors.email && (
                                <span className="field-error">{errors.email}</span>
                            )}
                        </div>

                        <div className="form-group">
                            <label htmlFor="phone">Phone Number (Optional)</label>
                            <input
                                type="tel"
                                id="phone"
                                name="phone"
                                value={formData.phone}
                                onChange={handleChange}
                                placeholder="Enter your phone number"
                                disabled={loading}
                                className={errors.phone ? 'error' : ''}
                            />
                            {errors.phone && (
                                <span className="field-error">{errors.phone}</span>
                            )}
                        </div>

                        <div className="form-group">
                            <label htmlFor="password">Password *</label>
                            <input
                                type="password"
                                id="password"
                                name="password"
                                value={formData.password}
                                onChange={handleChange}
                                placeholder="Create a password"
                                disabled={loading}
                                className={errors.password ? 'error' : ''}
                            />
                            {errors.password && (
                                <span className="field-error">{errors.password}</span>
                            )}
                            <small className="password-hint">
                                Password must be at least 6 characters
                            </small>
                        </div>

                        <div className="form-group">
                            <label htmlFor="confirmPassword">Confirm Password *</label>
                            <input
                                type="password"
                                id="confirmPassword"
                                name="confirmPassword"
                                value={formData.confirmPassword}
                                onChange={handleChange}
                                placeholder="Confirm your password"
                                disabled={loading}
                                className={errors.confirmPassword ? 'error' : ''}
                            />
                            {errors.confirmPassword && (
                                <span className="field-error">{errors.confirmPassword}</span>
                            )}
                        </div>

                        <div className="form-group checkbox-group">
                            <label className="checkbox-label">
                                <input
                                    type="checkbox"
                                    name="acceptTerms"
                                    checked={formData.acceptTerms}
                                    onChange={handleChange}
                                    disabled={loading}
                                />
                                <span>
                                    I agree to the <a href="/terms">Terms and Conditions</a> and{' '}
                                    <a href="/privacy">Privacy Policy</a>
                                </span>
                            </label>
                            {errors.acceptTerms && (
                                <span className="field-error">{errors.acceptTerms}</span>
                            )}
                        </div>

                        <button
                            type="submit"
                            className="btn-register"
                            disabled={loading}
                        >
                            {loading ? 'Creating Account...' : 'Create Account'}
                        </button>
                    </form>

                    <div className="register-footer">
                        <p>Already have an account?</p>
                        <button
                            onClick={handleLoginRedirect}
                            className="btn-login-link"
                            disabled={loading}
                        >
                            Sign In
                        </button>
                    </div>
                </div>
            </div>
        </div>
    );
};

export default RegisterPage;
