package sg.com.aori.repository;

import java.util.List;

import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import sg.com.aori.model.Wishlist;
import sg.com.aori.model.Wishlist.WishlistId;

/**
 * Wishlist class for adding/removing products to wishlist.
 *
 * @author Derek
 * @date 2025-10-07
 * @version 1.0
 */

@Repository
public interface WishlistRepository extends JpaRepository<Wishlist, WishlistId> {

	// to view list of wishlist items
	List<Wishlist> findByCustomerIdOrderByCreatedAtDesc(String customerId);

	// true if exists in wishlist, false if doesnt exist
	boolean existsByCustomerIdAndProductId(String customerId, String productId);

	// returns deleted row count | 0 -> product not in wishlist, 1-> product existed
	// and was removed from wishlist
	@Modifying
	@Query("DELETE FROM Wishlist w WHERE w.customerId = :customerId AND w.productId = :productId")
	int deleteByCustomerIdAndProductId(@Param("customerId") String customerId, @Param("productId") String productId);

}
