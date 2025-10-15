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

	boolean toggle(String customerId, String productId);

	boolean exists(String customerId, String productId);

	List<Wishlist> list(String customerId);

}
