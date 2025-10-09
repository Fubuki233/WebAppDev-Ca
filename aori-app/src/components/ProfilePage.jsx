/**
 * ProfilePage.jsx - User Profile Management
 * 
 * @author Yunhe
 * @date 2025-10-09
 * @version 1.0
 */
import React, { useState, useEffect } from 'react';
import Navbar from './Navbar';
import { getProfile, updateProfile } from '../api/profileApi';
import '../styles/ProfilePage.css';

const ProfilePage = () => {
    const [profile, setProfile] = useState(null);
    const [formData, setFormData] = useState({
        firstName: '',
        lastName: '',
        email: '',
        phone: '',
        dateOfBirth: '',
        gender: ''
    });
    const [isEditing, setIsEditing] = useState(false);
    const [loading, setLoading] = useState(true);
    const [saving, setSaving] = useState(false);
    const [error, setError] = useState('');
    const [success, setSuccess] = useState('');

    // Load profile on mount
    useEffect(() => {
        loadProfile();
    }, []);

    const loadProfile = async () => {
        setLoading(true);
        setError('');
        try {
            const response = await getProfile();
            console.log('Profile response:', response);

            if (response.success && response.profile) {
                setProfile(response.profile);
                setFormData({
                    firstName: response.profile.firstName || '',
                    lastName: response.profile.lastName || '',
                    email: response.profile.email || '',
                    phone: response.profile.phone || '',
                    dateOfBirth: response.profile.dateOfBirth || '',
                    gender: response.profile.gender || ''
                });
            } else {
                setError(response.message || 'Failed to load profile');
            }
        } catch (err) {
            console.error('Load profile error:', err);
            setError('Failed to load profile. Please try again.');
        } finally {
            setLoading(false);
        }
    };

    const handleChange = (e) => {
        const { name, value } = e.target;
        setFormData(prev => ({
            ...prev,
            [name]: value
        }));
        // Clear messages when user types
        if (error) setError('');
        if (success) setSuccess('');
    };

    const handleEdit = () => {
        setIsEditing(true);
        setError('');
        setSuccess('');
    };

    const handleCancel = () => {
        // Reset form data to original profile
        setFormData({
            firstName: profile.firstName || '',
            lastName: profile.lastName || '',
            email: profile.email || '',
            phone: profile.phone || '',
            dateOfBirth: profile.dateOfBirth || '',
            gender: profile.gender || ''
        });
        setIsEditing(false);
        setError('');
        setSuccess('');
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        setError('');
        setSuccess('');
        setSaving(true);

        try {
            const response = await updateProfile(formData);
            console.log('Update response:', response);

            if (response.success) {
                setProfile(response.profile);
                setSuccess('Profile updated successfully!');
                setIsEditing(false);
                // Reload to get latest data
                setTimeout(() => {
                    loadProfile();
                }, 1000);
            } else {
                setError(response.message || 'Failed to update profile');
            }
        } catch (err) {
            console.error('Update profile error:', err);
            setError('Failed to update profile. Please try again.');
        } finally {
            setSaving(false);
        }
    };

    if (loading) {
        return (
            <div className="profile-page">
                <Navbar />
                <div className="profile-container">
                    <div className="loading-container">
                        <div className="loading-spinner"></div>
                        <p>Loading profile...</p>
                    </div>
                </div>
            </div>
        );
    }

    return (
        <div className="profile-page">
            <Navbar />
            <div className="profile-container">
                {/* Breadcrumb */}
                <div className="breadcrumb">
                    <a href="#" onClick={(e) => { e.preventDefault(); window.location.hash = ''; }}>Home</a>
                    <span className="separator">/</span>
                    <span className="current">Profile</span>
                </div>

                {/* Page Title */}
                <h1 className="page-title">MY PROFILE</h1>

                {/* Profile Card */}
                <div className="profile-card">
                    {/* Messages */}
                    {error && (
                        <div className="message error-message">
                            {error}
                        </div>
                    )}
                    {success && (
                        <div className="message success-message">
                            {success}
                        </div>
                    )}

                    {/* Profile Form */}
                    <form onSubmit={handleSubmit} className="profile-form">
                        <div className="form-section">
                            <h2 className="section-title">Personal Information</h2>

                            <div className="form-row">
                                <div className="form-group">
                                    <label htmlFor="firstName">First Name</label>
                                    <input
                                        type="text"
                                        id="firstName"
                                        name="firstName"
                                        value={formData.firstName}
                                        onChange={handleChange}
                                        disabled={!isEditing}
                                        required
                                    />
                                </div>

                                <div className="form-group">
                                    <label htmlFor="lastName">Last Name</label>
                                    <input
                                        type="text"
                                        id="lastName"
                                        name="lastName"
                                        value={formData.lastName}
                                        onChange={handleChange}
                                        disabled={!isEditing}
                                        required
                                    />
                                </div>
                            </div>

                            <div className="form-row">
                                <div className="form-group">
                                    <label htmlFor="email">Email Address</label>
                                    <input
                                        type="email"
                                        id="email"
                                        name="email"
                                        value={formData.email}
                                        onChange={handleChange}
                                        disabled={true}
                                        title="Email cannot be changed"
                                    />
                                    <span className="field-hint">Email cannot be changed</span>
                                </div>

                                <div className="form-group">
                                    <label htmlFor="phone">Phone Number</label>
                                    <input
                                        type="tel"
                                        id="phone"
                                        name="phone"
                                        value={formData.phone}
                                        onChange={handleChange}
                                        disabled={!isEditing}
                                        placeholder="Enter phone number"
                                    />
                                </div>
                            </div>

                            <div className="form-row">
                                <div className="form-group">
                                    <label htmlFor="dateOfBirth">Date of Birth</label>
                                    <input
                                        type="date"
                                        id="dateOfBirth"
                                        name="dateOfBirth"
                                        value={formData.dateOfBirth}
                                        onChange={handleChange}
                                        disabled={!isEditing}
                                    />
                                </div>

                                <div className="form-group">
                                    <label htmlFor="gender">Gender</label>
                                    <select
                                        id="gender"
                                        name="gender"
                                        value={formData.gender}
                                        onChange={handleChange}
                                        disabled={!isEditing}
                                    >
                                        <option value="">Select Gender</option>
                                        <option value="Male">Male</option>
                                        <option value="Female">Female</option>
                                        <option value="Other">Other</option>
                                        <option value="Prefer not to say">Prefer not to say</option>
                                    </select>
                                </div>
                            </div>
                        </div>

                        {/* Action Buttons */}
                        <div className="form-actions">
                            {!isEditing ? (
                                <button
                                    type="button"
                                    className="btn-edit"
                                    onClick={handleEdit}
                                >
                                    Edit Profile
                                </button>
                            ) : (
                                <>
                                    <button
                                        type="button"
                                        className="btn-cancel"
                                        onClick={handleCancel}
                                        disabled={saving}
                                    >
                                        Cancel
                                    </button>
                                    <button
                                        type="submit"
                                        className="btn-save"
                                        disabled={saving}
                                    >
                                        {saving ? 'Saving...' : 'Save Changes'}
                                    </button>
                                </>
                            )}
                        </div>
                    </form>
                </div>

                {/* Account Info Card */}
                {profile && (
                    <div className="info-card">
                        <h2 className="section-title">Account Information</h2>
                        <div className="info-grid">
                            <div className="info-item">
                                <span className="info-label">Customer ID</span>
                                <span className="info-value">{profile.customerId}</span>
                            </div>
                            <div className="info-item">
                                <span className="info-label">Member Since</span>
                                <span className="info-value">
                                    {profile.createdAt ? new Date(profile.createdAt).toLocaleDateString() : 'N/A'}
                                </span>
                            </div>
                            <div className="info-item">
                                <span className="info-label">Last Updated</span>
                                <span className="info-value">
                                    {profile.updatedAt ? new Date(profile.updatedAt).toLocaleDateString() : 'N/A'}
                                </span>
                            </div>
                        </div>
                    </div>
                )}
            </div>
        </div>
    );
};

export default ProfilePage;
