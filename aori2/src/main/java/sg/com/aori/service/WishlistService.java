package sg.com.aori.service;

import java.util.List;
import java.util.Objects;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import sg.com.aori.interfaces.IWishlist;
import sg.com.aori.model.Wishlist;
import sg.com.aori.repository.WishlistRepository;

/**
 * WishlistService class for adding/removing products to wishlist.
 *
 * @author Derek
 * @date 2025-10-07
 * @version 1.0
 */

@Service
public class WishlistService implements IWishlist {

	private final WishlistRepository wishlistRepository;

	public WishlistService(WishlistRepository wishlistRepository) {
		this.wishlistRepository = wishlistRepository;
	}

	@Override
	@Transactional
	public boolean toggle(String customerId, String productId) {
		validate(customerId, productId);
		int deleted = wishlistRepository.deleteByCustomerIdAndProductId(customerId, productId);

		if (deleted > 0) {
			return false;
		}

		wishlistRepository.save(new Wishlist(customerId, productId));
		return true;
	}

	@Override
	@Transactional(readOnly = true)
	public boolean exists(String customerId, String productId) {
		validate(customerId, productId);
		return wishlistRepository.existsByCustomerIdAndProductId(customerId, productId);
	}

	@Override
	@Transactional(readOnly = true)
	public List<Wishlist> list(String customerId) {
		Objects.requireNonNull(customerId, "customerId must not be null");
		return wishlistRepository.findByCustomerIdOrderByCreatedAtDesc(customerId);
	}

	public static void validate(String customerId, String productId) {
		Objects.requireNonNull(customerId, "customerId must not be null");
		Objects.requireNonNull(productId, "productId must not be null");
	}

}
