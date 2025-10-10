package sg.com.aori.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import sg.com.aori.model.ReturnRequest;

/**
 * @author Jiayi
 * @date 2025-10-09
 * @version 1.0
 */

@Repository
public interface ReturnRepository extends JpaRepository<ReturnRequest, String> {

    /**
     * 根据订单项 ID 列表查询退货记录
     * 
     * @param orderItemIds 订单项 ID 列表
     * @return 退货记录列表
     */
    List<Returns> findByOrderItemIdIn(List<String> orderItemIds);
}