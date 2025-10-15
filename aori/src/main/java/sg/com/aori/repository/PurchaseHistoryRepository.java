package sg.com.aori.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import sg.com.aori.model.Orders;

/**
 * @author Jiayi
 * @date 2025-10-08
 * @version 1.0
 */
public interface PurchaseHistoryRepository extends JpaRepository<Orders, String> {

        Page<Orders> findByCustomerIdAndCreatedAtBetween(
                        String customerId, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);

        List<Orders> findByCustomerIdAndOrderStatusAndCreatedAtBetween(
                        String customerId, Orders.OrderStatus orderStatus, LocalDateTime startDate,
                        LocalDateTime endDate,
                        Pageable pageable);
}
