/**
 * Repository interface for Customer entity.
 *
 * @author Sun Rui & Yunhe
 * @date 2025-10-07
 * @version 1.1
 */

package sg.com.aori.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import sg.com.aori.model.Customer;

public interface CustomerRepository extends JpaRepository<Customer, String> {

    @Query("SELECT c FROM Customer c WHERE c.email = :email")
    Optional<Customer> findCustomerByEmail(@Param("email") String email);

    @Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END " +
            "FROM Customer c WHERE c.email = :email")
    boolean existsByEmail(@Param("email") String email);

}