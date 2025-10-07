package sg.com.aori.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import sg.com.aori.model.Customer;

/**
 * Repository interface for Customer entity.
 *
 * @author Yunhe
 * @date 2025-10-07
 * @version 1.0
 */
public interface LoginRepository extends JpaRepository<Customer, String> {
    @Query("SELECT c FROM Customer c WHERE c.email = :email")
    Optional<Customer> findCustomerByEmail(@Param("email") String email);

}