package sg.com.aori.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import sg.com.aori.model.Payment;

/**
 * @author Jiayi
 * @date 2025-10-08
 * @version 1.0
 */

public interface PaymentRepository extends JpaRepository<Payment, String> {
    List<Payment> findByOrderIdIn(List<String> orderIds);
}
