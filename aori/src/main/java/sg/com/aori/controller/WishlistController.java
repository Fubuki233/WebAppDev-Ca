package sg.com.aori.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import sg.com.aori.model.Wishlist;
import sg.com.aori.service.WishlistService;

/**
 * Controller class for handling wishlist.
 * 
 * @author Derek
 * @date 2025-10-08
 * @version 1.0
 * 
 * @author Yunhe
 * @date 2025-10-11
 * @version 1.1 - Improved the get method when user not logged in .
 */

@RestController
@RequestMapping("/api/wishlist")
public class WishlistController {

	private final WishlistService wishlistService;

	public WishlistController(WishlistService wishlistService) {
		this.wishlistService = wishlistService;
	}

	/**
	 * Toggles customer's wishlist.
	 * 
	 * @param customerId
	 * @param productId
	 * @return added:true if newly added, added:false if removed
	 */
	@PostMapping("")
	public ResponseEntity<Map<String, Boolean>> toggle(
			@RequestParam String customerId,
			@RequestParam String productId) {
		boolean added = wishlistService.toggle(customerId, productId);
		return ResponseEntity.ok(Map.of("added", added));
	}

	/**
	 * Checks if a product is already in a customer's wishlist.
	 * 
	 * @param customerId
	 * @param productId
	 * @return exists:true/false
	 */
	@GetMapping("/exists")
	public ResponseEntity<Map<String, Boolean>> exists(
			@RequestParam String customerId,
			@RequestParam String productId) {
		boolean exists = wishlistService.exists(customerId, productId);
		return ResponseEntity.ok(Map.of("exists", exists));
	}

	/**
	 * Lists all items in customer's wishlist
	 */
	@GetMapping
	public ResponseEntity<List<Wishlist>> list(
			@RequestParam String customerId) {
		if (customerId == null) {
			return ResponseEntity.ok(List.of());
		}
		return ResponseEntity.ok(wishlistService.list(customerId));
	}

}
