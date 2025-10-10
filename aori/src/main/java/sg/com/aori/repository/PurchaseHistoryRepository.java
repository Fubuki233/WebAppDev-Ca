package sg.com.aori.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import sg.com.aori.model.Orders;

/**
 * purchasehistory
 *
 * @author Jiayi
 * @date 2025-10-08
 * @version 1.0
 */
public interface PurchaseHistoryRepository extends JpaRepository<Orders, String> {
    
    /**
     * 根据客户 ID 和订单创建时间区间查询订单列表，支持分页
     * @param customerId 客户 ID
     * @param startDate 开始时间
     * @param endDate 结束时间
     * @param pageable 分页参数
     * @return 客户在指定时间范围内的订单列表
     */
    Page<Orders> findByCustomerIdAndCreatedAtBetween(
            String customerId, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);
    
    /**
     * 根据客户 ID 和订单状态以及订单创建时间区间查询订单列表
     * @param customerId 客户 ID
     * @param orderStatus 订单状态
     * @param startDate 开始时间
     * @param endDate 结束时间
     * @param pageable 分页参数
     * @return 客户指定状态的订单列表
     */
    List<Orders> findByCustomerIdAndOrderStatusAndCreatedAtBetween(
            String customerId, Orders.OrderStatus orderStatus, LocalDateTime startDate, LocalDateTime endDate, 
            Pageable pageable);
}
