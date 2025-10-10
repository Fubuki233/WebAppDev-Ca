package sg.com.aori.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import sg.com.aori.dto.PurchaseHistoryControllerDTO;
import sg.com.aori.model.OrderItem;
import sg.com.aori.model.Payment;
import sg.com.aori.model.Returns;

/**
 * 购买历史 Service 接口
 * 职责：对外提供基于 Customer 的购买历史查询能力（包含取消、未支付、部分退款，时间区间筛选，展开订单行与商品详情）
 * 
 * @author Jiayi
 * @date 2025-10-08
 * @version 1.0
 */
@Service
public interface PurchaseHistoryService {

    /**
     * 分页查询指定客户的购买历史
     * 支持按订单状态/支付状态筛选
     * 支持按创建时间区间筛选
     * 结果展开到订单行（含商品详情）
     *
     * @param customerId  客户 ID
     * @param startDate   查询的开始时间
     * @param endDate     查询的结束时间
     * @param pageRequest 分页请求
     * @return 分页结果（不保证提供 total，总是提供 hasNext 以支持前端“加载更多/下一页”）
     */
    sg.com.aori.service.Page<sg.com.aori.dto.PurchaseHistoryDTO> getPurchaseHistory(String customerId,
            LocalDateTime startDate,
            LocalDateTime endDate,
            PageRequest pageRequest);

    /**
     * 查询指定订单（需归属于该客户）的详细信息
     * - 展开订单行（含商品详情/退货标记/部分退款标记）
     *
     * @param orderId 订单 ID
     * @return 订单详情 DTO（若订单不存在或不属于该客户，抛出业务异常）
     */
    PurchaseHistoryControllerDTO getOrderDetails(String orderId);

    /**
     * 获取订单支付信息
     * 
     * @param orderId 订单 ID
     * @return 支付信息列表
     */
    List<Payment> getPaymentDetails(String orderId);

    /**
     * 获取订单的退货信息
     * 
     * @param orderId 订单 ID
     * @return 退货记录列表
     */
    List<Returns> getReturns(String orderId);

    /**
     * 获取订单项（商品详情）
     * 
     * @param orderId 订单 ID
     * @return 订单项列表
     */
    List<OrderItem> getOrderItems(String orderId);
}
