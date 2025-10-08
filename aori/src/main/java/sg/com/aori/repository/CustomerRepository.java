package sg.com.aori.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import sg.com.aori.model.Customer;

/**
 * Repository interface for Customer entity.
 *
 * @author SunRui&Yunhe
 * @date 2025-10-07
 * @version 1.1
 */
public interface CustomerRepository extends JpaRepository<Customer, String> {

    /** 按邮箱查找用户（用于登录） */
    @Query("SELECT c FROM Customer c WHERE c.email = :email")
    Optional<Customer> findCustomerByEmail(@Param("email") String email);

    /** 邮箱是否存在（用于登录前置判断/注册查重时也可复用） */
    @Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END " +
            "FROM Customer c WHERE c.email = :email")
    boolean existsByEmail(@Param("email") String email);

}