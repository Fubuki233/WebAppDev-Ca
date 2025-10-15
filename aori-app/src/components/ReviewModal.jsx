/**
 * ReviewModal.jsx - Product Review Modal Component
 * 
 * @author Yunhe
 * @date 2025-10-14
 * @version 1.0
 */
import React, { useState, useEffect } from 'react';
import { submitReview, getOwnReview } from '../api/reviewApi';
import '../styles/ReviewModal.css';

const ReviewModal = ({ isOpen, onClose, orderItem, orderId, customerId, onReviewSubmitted }) => {
    const [rating, setRating] = useState(0);
    const [hoverRating, setHoverRating] = useState(0);
    const [title, setTitle] = useState('');
    const [comment, setComment] = useState('');
    const [loading, setLoading] = useState(false);
    const [existingReview, setExistingReview] = useState(null);
    const [error, setError] = useState('');
    const [success, setSuccess] = useState('');

    useEffect(() => {
        if (isOpen && orderItem && orderId && customerId) {
            loadExistingReview();
        }
    }, [isOpen, orderItem, orderId, customerId]);

    const loadExistingReview = async () => {
        try {
            const review = await getOwnReview(customerId, orderId, orderItem.orderItemId);
            if (review && review.review) {
                setExistingReview(review.review);
                setRating(review.review.rating || 0);
                setTitle(review.review.title || '');
                setComment(review.review.comment || '');
            }
        } catch (error) {
            console.error('Error loading existing review:', error);
        }
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        setError('');
        setSuccess('');

        if (rating === 0) {
            setError('Please select a rating');
            return;
        }

        setLoading(true);
        try {
            const result = await submitReview(customerId, orderId, orderItem.productId, {
                rating,
                title,
                comment,
                images: []
            });

            if (result.success) {
                setSuccess(existingReview ? 'Review updated successfully!' : 'Review submitted successfully!');

                // Notify parent component about successful review submission
                if (onReviewSubmitted) {
                    onReviewSubmitted(orderItem.productId, orderId);
                }

                setTimeout(() => {
                    onClose();
                    // Reset form
                    setRating(0);
                    setTitle('');
                    setComment('');
                    setError('');
                    setSuccess('');
                }, 2000);
            } else {
                setError(result.message || 'Failed to submit review');
            }
        } catch (error) {
            setError('An error occurred while submitting your review');
        } finally {
            setLoading(false);
        }
    };

    const handleClose = () => {
        if (!loading) {
            onClose();
            // Reset form after a short delay
            setTimeout(() => {
                setRating(0);
                setTitle('');
                setComment('');
                setError('');
                setSuccess('');
                setExistingReview(null);
            }, 300);
        }
    };

    if (!isOpen) return null;

    return (
        <div className="review-modal-overlay" onClick={handleClose}>
            <div className="review-modal" onClick={(e) => e.stopPropagation()}>
                <div className="review-modal-header">
                    <h3>{existingReview ? 'Edit Your Review' : 'Write a Review'}</h3>
                    <button className="close-button" onClick={handleClose} disabled={loading}>
                        ×
                    </button>
                </div>

                <div className="review-modal-body">
                    {/* Product Info */}
                    <div className="review-product-info">
                        <img
                            src={orderItem.product?.image || orderItem.image || 'https://via.placeholder.com/80'}
                            alt={orderItem.product?.productName || orderItem.productName}
                            className="review-product-image"
                        />
                        <div className="review-product-details">
                            <div className="review-product-name">
                                {orderItem.product?.productName || orderItem.productName || 'Product'}
                            </div>
                        </div>
                    </div>

                    {/* Messages */}
                    {error && <div className="review-error-message">{error}</div>}
                    {success && <div className="review-success-message">{success}</div>}

                    {/* Review Form */}
                    <form onSubmit={handleSubmit} className="review-form">
                        {/* Rating */}
                        <div className="review-form-group">
                            <label className="review-label">Rating *</label>
                            <div className="star-rating">
                                {[1, 2, 3, 4, 5].map((star) => (
                                    <button
                                        key={star}
                                        type="button"
                                        className={`star-button ${star <= (hoverRating || rating) ? 'filled' : ''}`}
                                        onClick={() => setRating(star)}
                                        onMouseEnter={() => setHoverRating(star)}
                                        onMouseLeave={() => setHoverRating(0)}
                                        disabled={loading}
                                    >
                                        ★
                                    </button>
                                ))}
                            </div>
                        </div>

                        {/* Title */}
                        <div className="review-form-group">
                            <label className="review-label" htmlFor="review-title">
                                Review Title
                            </label>
                            <input
                                type="text"
                                id="review-title"
                                className="review-input"
                                placeholder="Sum up your experience"
                                value={title}
                                onChange={(e) => setTitle(e.target.value)}
                                disabled={loading}
                                maxLength={100}
                            />
                        </div>

                        {/* Comment */}
                        <div className="review-form-group">
                            <label className="review-label" htmlFor="review-comment">
                                Your Review
                            </label>
                            <textarea
                                id="review-comment"
                                className="review-textarea"
                                placeholder="Share your thoughts about this product..."
                                value={comment}
                                onChange={(e) => setComment(e.target.value)}
                                disabled={loading}
                                rows={5}
                                maxLength={1000}
                            />
                        </div>

                        {/* Submit Button */}
                        <div className="review-form-actions">
                            <button
                                type="button"
                                className="review-button review-button-cancel"
                                onClick={handleClose}
                                disabled={loading}
                            >
                                Cancel
                            </button>
                            <button
                                type="submit"
                                className="review-button review-button-submit"
                                disabled={loading || rating === 0}
                            >
                                {loading ? 'Submitting...' : existingReview ? 'Update Review' : 'Submit Review'}
                            </button>
                        </div>
                    </form>
                </div>
            </div>
        </div>
    );
};

export default ReviewModal;
