package sg.com.aori.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import sg.com.aori.model.Payment;

/**
 * purchasehistory
 *
 * @author Jiayi
 * @date 2025-10-08
 * @version 1.0
 */

public interface PaymentRepository extends JpaRepository<Payment, String> {
    
    /**
     * 根据订单 ID 列表查询支付记录
     * @param orderIds 订单 ID 列表
     * @return 支付信息列表
     */
    List<Payment> findByOrderIdIn(List<String> orderIds);
}
