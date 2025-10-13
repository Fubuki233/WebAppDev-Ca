package sg.com.aori.interfaces;

import java.util.List;

import sg.com.aori.model.Wishlist;

/**
 * Wishlist class for adding/removing products to wishlist.
 *
 * @author Derek
 * @date 2025-10-07
 * @version 1.0
 */

public interface IWishlist {

	/**
	 * Toggle a product in customer's wishlist
	 * return true if added, false if removed
	 */
	boolean toggle(String customerId, String productId);

	// true - shows heart as filled, false - shows heart as empty). think instagram
	boolean exists(String customerId, String productId);

	// toggle already handles both cases -
	// this is for if we want to have a separate APIs or batch feature

	/*
	 * Explicitly add (no-operation if already present)
	 * true if added, false if already existed
	 * boolean add (String customerId, String productId);
	 * 
	 * Explicitly remove (no-operation if already present)
	 * true if removed, false if didn't exist
	 * boolean remove (String customerId, String productId);
	 */

	List<Wishlist> list(String customerId);

}
