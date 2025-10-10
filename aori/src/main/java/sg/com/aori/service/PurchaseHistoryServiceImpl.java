package sg.com.aori.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import sg.com.aori.dto.PurchaseHistoryDTO;
import sg.com.aori.dto.PurchaseOrderItemDTO;
import sg.com.aori.model.OrderItem;
import sg.com.aori.model.Orders;
import sg.com.aori.model.Payment;
import sg.com.aori.model.Returns;
import sg.com.aori.repository.OrderItemRepository;
import sg.com.aori.repository.PaymentRepository;
import sg.com.aori.repository.PurchaseHistoryRepository;
import sg.com.aori.repository.ReturnRepository;

/**
 * purchasehistory
 *
 * @author Jiayi
 * @date 2025-10-08
 * @version 1.0
 */
/**
 * 购买历史 Service 实现
 * 说明：
 * 1）与仓库接口协作完成“按客户 + 区间 + 状态”的订单查询。
 * 2）批量加载订单行（含商品）与支付记录，避免 N+1。
 * 3）根据退货记录推断“是否部分退款”。
 */
@Service
@Transactional(readOnly = true)
public abstract class PurchaseHistoryServiceImpl implements PurchaseHistoryService {

    private final PurchaseHistoryRepository purchaseHistoryRepository;
    private final OrderItemRepository orderItemRepository;
    private final PaymentRepository paymentRepository;
    private final ReturnRepository returnsRepository;

    @Autowired
    public PurchaseHistoryServiceImpl(PurchaseHistoryRepository purchaseHistoryRepository,
                                      OrderItemRepository orderItemRepository,
                                      PaymentRepository paymentRepository,
                                      ReturnRepository returnsRepository) {
        this.purchaseHistoryRepository = purchaseHistoryRepository;
        this.orderItemRepository = orderItemRepository;
        this.paymentRepository = paymentRepository;
        this.returnsRepository = returnsRepository;
    }

    @Override
    public sg.com.aori.service.Page getPurchaseHistory(String customerId, 
                                                       LocalDateTime startDate, 
                                                       LocalDateTime endDate, 
                                                       PageRequest pageRequest) {

        // 1）查询订单，确保返回的是 Page 类型
        Page<Orders> ordersPage = (Page<Orders>) purchaseHistoryRepository.findByCustomerIdAndCreatedAtBetween(
                customerId, startDate, endDate, pageRequest);

        if (ordersPage.isEmpty()) {
            return (sg.com.aori.service.Page) Page.empty();
        }

        // 2）批量加载订单项（含商品）
        List<String> orderIds = ordersPage.getContent().stream().map(Orders::getOrderId).collect(Collectors.toList());
        List<OrderItem> orderItems = orderItemRepository.findOrderItemsWithProductDetails(orderIds);

        // 3）批量加载支付记录
        List<Payment> payments = paymentRepository.findByOrderIdIn(orderIds);
        Map<String, List<Payment>> paymentsByOrderId = payments.stream()
                .collect(Collectors.groupingBy(Payment::getOrderId));

        // 4）批量加载退货记录
        List<String> orderItemIds = orderItems.stream().map(OrderItem::getOrderItemId).collect(Collectors.toList());
        Map<String, List<Returns>> returnsByOrderItemId = returnsRepository.findByOrderItemIdIn(orderItemIds)
                .stream()
                .collect(Collectors.groupingBy(Returns::getOrderItemId));

        // 5）将订单项按订单分组
        Map<String, List<OrderItem>> itemsByOrderId = orderItems.stream()
                .collect(Collectors.groupingBy(OrderItem::getOrderId));

        // 6）组装 DTO
        Map<String, Orders> orderMap = ordersPage.getContent().stream()
                .collect(Collectors.toMap(Orders::getOrderId, Function.identity()));

        List<PurchaseHistoryDTO> data = ordersPage.getContent().stream()
                .map(o -> assembleOrderDTO(o, itemsByOrderId.getOrDefault(o.getOrderId(), List.of()),
                        paymentsByOrderId.getOrDefault(o.getOrderId(), List.of()),
                        returnsByOrderItemId))
                .collect(Collectors.toList());

        // 返回分页结果，构造一个 PageImpl 对象
        Page<PurchaseHistoryDTO> page = new PageImpl<>(data, pageRequest, ordersPage.getTotalElements());
        return (sg.com.aori.service.Page) page;
    }


    @Override
    public PurchaseHistoryDTO getOrderDetails(String orderId) {
        // 1）查询订单
        Orders order = purchaseHistoryRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("订单不存在"));

        // 2）加载本订单的订单项（含商品）
        List<OrderItem> items = orderItemRepository.findOrderItemsWithProductDetails(List.of(orderId));

        // 3）加载支付记录
        List<Payment> payments = paymentRepository.findByOrderIdIn(List.of(orderId));

        // 4）加载退货记录
        List<String> itemIds = items.stream().map(OrderItem::getOrderItemId).collect(Collectors.toList());
        Map<String, List<Returns>> returnsByOrderItemId = returnsRepository.findByOrderItemIdIn(itemIds)
                .stream()
                .collect(Collectors.groupingBy(Returns::getOrderItemId));

        // 5）组装订单详情 DTO
        return assembleOrderDTO(order, items, payments, returnsByOrderItemId);
    }


    /**
     * 组装订单 DTO（含订单行、商品详情、支付概要、部分退款标记）
     */
    private PurchaseHistoryDTO assembleOrderDTO(
            Orders order,
            List<OrderItem> items,
            List<Payment> payments,
            Map<String, List<Returns>> returnsByOrderItemId
    ) {
        // 行级 DTO
        List<PurchaseOrderItemDTO> itemDTOs = items.stream().map(oi -> {
            boolean refundedThisLine = returnsByOrderItemId.getOrDefault(oi.getOrderItemId(), List.of())
                    .stream()
                    .anyMatch(r -> r.getReturnStatus() == Returns.ReturnStatus.Refunded || r.getReturnStatus() == Returns.ReturnStatus.Exchange);

            return PurchaseOrderItemDTO.from(oi, refundedThisLine);
        }).collect(Collectors.toList());

        // 是否“部分退款”：存在至少一行退款，但并未所有行都退款
        long refundedLines = itemDTOs.stream().filter(PurchaseOrderItemDTO::isRefunded).count();
        boolean partiallyRefunded = refundedLines > 0 && refundedLines < Math.max(1, itemDTOs.size());

        // 支付状态：若订单实体已有 paymentStatus，则以订单为准；否则从支付记录映射一个“概要”
        Orders.PaymentStatus paymentStatus = order.getPaymentStatus();
        if (paymentStatus == null) {
            paymentStatus = summarizePaymentStatus(payments);
        }

        return new PurchaseHistoryDTO(order, itemDTOs, payments, partiallyRefunded, paymentStatus);
    }

    /**
     * 将多条支付记录汇总为一个“概要状态”
     * 简化规则（可按业务需要调整）：
     * - 存在 Refunded 则视为 Refunded
     * - 否则存在 Success/Paid 则视为 Paid
     * - 否则存在 Pending 则视为 Pending
     * - 否则存在 Failed 则视为 Failed
     * - 否则回退为订单上的状态或 Pending
     */
    private Orders.PaymentStatus summarizePaymentStatus(List<Payment> payments) {
        if (payments == null || payments.isEmpty()) return Orders.PaymentStatus.Pending;

        boolean hasRefunded = payments.stream().anyMatch(p -> p.getPaymentStatus() == Payment.PaymentStatus.Refunded);
        if (hasRefunded) return Orders.PaymentStatus.Refunded;

        boolean hasPaid = payments.stream().anyMatch(p -> p.getPaymentStatus() == Payment.PaymentStatus.Success);
        if (hasPaid) return Orders.PaymentStatus.Paid;

        boolean hasPending = payments.stream().anyMatch(p -> p.getPaymentStatus() == Payment.PaymentStatus.Pending);
        if (hasPending) return Orders.PaymentStatus.Pending;

        boolean hasFailed = payments.stream().anyMatch(p -> p.getPaymentStatus() == Payment.PaymentStatus.Failed);
        if (hasFailed) return Orders.PaymentStatus.Failed;

        return Orders.PaymentStatus.Pending;
    }
}
