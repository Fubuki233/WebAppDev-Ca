/**
 * Repository Interface for CustomerAddress Entity
 *
 * @author SunRui
 * @date 2025-10-07
 * @version 1.0
 * 
 * @author Ying Chun
 * @date 2025-10-08
 * @version 1.1 - added two more methods
 */

package sg.com.aori.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import sg.com.aori.model.CustomerAddress;

public interface CustomerAddressRepository extends JpaRepository<CustomerAddress, String> {

    // 某个用户的全部地址（创建时间近在前）
    List<CustomerAddress> findByCustomerIdOrderByCreatedAtDesc(String customerId);

    // 某个用户的“默认地址”
    Optional<CustomerAddress> findFirstByCustomerIdAndIsDefaultTrue(String customerId);

    // 某个用户的“账单地址们”（若一个或多个）
    List<CustomerAddress> findByCustomerIdAndIsBillingTrue(String customerId);

    // 统计用户地址数量（控制上限/提示用）
    long countByCustomerId(String customerId);
    
    // for use when updating customer address in customer account
    // find all addresses for a given customer
    List<CustomerAddress> findByCustomerId(String customerId);
    
    // for use when updating customer address in customer account
    // to find a specific address by its ID and match to the correct customer
    
    Optional<CustomerAddress> findByAddressIdAndCustomerId(String addressId, String customerId);



}
