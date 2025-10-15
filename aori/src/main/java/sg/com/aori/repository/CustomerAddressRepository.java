package sg.com.aori.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import sg.com.aori.model.CustomerAddress;

/**
 * Repository Interface for CustomerAddress Entity
 *
 * @author SunRui
 * @date 2025-10-07
 * @version 1.0
 * 
 * @author Ying Chun
 * @date 2025-10-08
 * @version 1.1 - Added two more methods
 * 
 * @author Jiayi
 * @date 2025-10-09
 * @version 1.2
 */

public interface CustomerAddressRepository extends JpaRepository<CustomerAddress, String> {

    List<CustomerAddress> findByCustomerIdOrderByCreatedAtDesc(String customerId);

    Optional<CustomerAddress> findFirstByCustomerIdAndIsDefaultTrue(String customerId);

    long countByCustomerId(String customerId);

    List<CustomerAddress> findByCustomerId(String customerId);

    Optional<CustomerAddress> findByAddressIdAndCustomerId(String addressId, String customerId);

}
