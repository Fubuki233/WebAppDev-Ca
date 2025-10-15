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

	List<Wishlist> findByCustomerIdOrderByCreatedAtDesc(String customerId);

	boolean existsByCustomerIdAndProductId(String customerId, String productId);

	@Modifying
	@Query("DELETE FROM Wishlist w WHERE w.customerId = :customerId AND w.productId = :productId")
	int deleteByCustomerIdAndProductId(@Param("customerId") String customerId, @Param("productId") String productId);

}
