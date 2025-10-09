package sg.com.aori.service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
public class PurchaseHistoryServiceImpl implements PurchaseHistoryService {

    private final PurchaseHistoryRepository purchaseHistoryRepository;
    private final OrderItemRepository orderItemRepository;
    private final PaymentRepository paymentRepository;
    private final ReturnRepository returnsRepository;

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
    public PageResult<PurchaseOrderDTO> getPurchaseHistory(PurchaseHistoryFilter filter, int page, int size) {
        validateFilter(filter);

        // 为了支持 hasNext，这里用 size+1 拉取一条“探针”记录
        int pageSizePlusOne = Math.max(1, size) + 1;
        Pageable pageable = PageRequest.of(Math.max(0, page), pageSizePlusOne);

        // 1）根据是否包含 orderStatus 条件，选择不同查询
        final LocalDateTime from = Optional.ofNullable(filter.getFrom()).orElse(LocalDateTime.MIN);
        final LocalDateTime to   = Optional.ofNullable(filter.getTo()).orElse(LocalDateTime.MAX);

        List<Orders> orders;
        if (filter.getOrderStatus() != null) {
            orders = purchaseHistoryRepository.findByCustomerIdAndOrderStatusAndCreatedAtBetween(
                    filter.getCustomerId(), filter.getOrderStatus(), from, to, pageable);
        } else {
            orders = purchaseHistoryRepository.findByCustomerIdAndCreatedAtBetween(
                    filter.getCustomerId(), from, to, pageable);
        }

        // 2）计算 hasNext，并裁剪到期望 size
        boolean hasNext = orders.size() == pageSizePlusOne;
        if (hasNext) {
            orders = orders.subList(0, size);
        }
        if (orders.isEmpty()) {
            return PageResult.of(Collections.emptyList(), page, size, hasNext);
        }

        // 3）批量加载订单项（含商品）
        List<String> orderIds = orders.stream().map(Orders::getOrderId).toList();
        List<OrderItem> orderItems = orderItemRepository.findOrderItemsWithProductDetails(orderIds);

        // 4）批量加载支付记录
        List<Payment> payments = paymentRepository.findByOrderIdIn(orderIds);
        Map<String, List<Payment>> paymentsByOrderId = payments.stream()
                .collect(Collectors.groupingBy(Payment::getOrderId));

        // 5）批量加载退货记录（用于标记行级退款/部分退款）
        List<String> orderItemIds = orderItems.stream().map(OrderItem::getOrderItemId).toList();
        Map<String, List<Returns>> returnsByOrderItemId = returnsRepository.findByOrderItemIdIn(orderItemIds)
                .stream()
                .collect(Collectors.groupingBy(Returns::getOrderItemId));

        // 6）将订单项按订单分组
        Map<String, List<OrderItem>> itemsByOrderId = orderItems.stream()
                .collect(Collectors.groupingBy(OrderItem::getOrderId));

        // 7）组装 DTO
        Map<String, Orders> orderMap = orders.stream()
                .collect(Collectors.toMap(Orders::getOrderId, Function.identity()));

        List<PurchaseOrderDTO> data = orders.stream()
                .map(o -> assembleOrderDTO(o, itemsByOrderId.getOrDefault(o.getOrderId(), List.of()),
                        paymentsByOrderId.getOrDefault(o.getOrderId(), List.of()),
                        returnsByOrderItemId))
                .toList();

        // 8）支付状态筛选（如果 filter.paymentStatus 不为 null）
        if (filter.getPaymentStatus() != null) {
            List<PurchaseOrderDTO> filtered = data.stream()
                    .filter(d -> d.getPaymentStatus() == filter.getPaymentStatus())
                    .toList();
            return PageResult.of(filtered, page, size, hasNext && filtered.size() == size);
        }

        return PageResult.of(data, page, size, hasNext);
    }

    @Override
    public PurchaseOrderDTO getOrderDetail(String customerId, String orderId) {
        // 1）基于 customer + orderId 定位订单（避免跨用户越权）
        //   由于仓库层未提供该签名方法，这里采用区间最大化方案：
        List<Orders> orders = purchaseHistoryRepository.findByCustomerIdAndCreatedAtBetween(
                customerId, LocalDateTime.MIN, LocalDateTime.MAX, PageRequest.of(0, Integer.MAX_VALUE));

        Orders target = orders.stream()
                .filter(o -> Objects.equals(o.getOrderId(), orderId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("订单不存在或不属于该客户"));

        // 2）加载本订单的订单项（含商品）
        List<OrderItem> items = orderItemRepository.findOrderItemsWithProductDetails(List.of(orderId));

        // 3）加载支付记录
        List<Payment> payments = paymentRepository.findByOrderIdIn(List.of(orderId));
        Map<String, List<Payment>> paymentsByOrderId = payments.stream()
                .collect(Collectors.groupingBy(Payment::getOrderId));

        // 4）加载退货记录
        List<String> itemIds = items.stream().map(OrderItem::getOrderItemId).toList();
        Map<String, List<Returns>> returnsByOrderItemId = returnsRepository.findByOrderItemIdIn(itemIds)
                .stream()
                .collect(Collectors.groupingBy(Returns::getOrderItemId));

        // 5）组装
        return assembleOrderDTO(target, items, paymentsByOrderId.getOrDefault(orderId, List.of()), returnsByOrderItemId);
    }

    // ---------------- 辅助方法 ----------------

    /** 基础校验 */
    private void validateFilter(PurchaseHistoryFilter filter) {
        if (filter == null || filter.getCustomerId() == null || filter.getCustomerId().isBlank()) {
            throw new IllegalArgumentException("customerId 不能为空");
        }
        if (filter.getFrom() != null && filter.getTo() != null && filter.getFrom().isAfter(filter.getTo())) {
            throw new IllegalArgumentException("起止时间不合法：from 不能晚于 to");
        }
    }

    /**
     * 组装订单 DTO（含订单行、商品详情、支付概要、部分退款标记）
     */
    private PurchaseOrderDTO assembleOrderDTO(
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
        }).toList();

        // 是否“部分退款”：存在至少一行退款，但并未所有行都退款
        long refundedLines = itemDTOs.stream().filter(PurchaseOrderItemDTO::isRefunded).count();
        boolean partiallyRefunded = refundedLines > 0 && refundedLines < Math.max(1, itemDTOs.size());

        // 支付状态：若订单实体已有 paymentStatus，则以订单为准；否则从支付记录映射一个“概要”
        Orders.PaymentStatus paymentStatus = order.getPaymentStatus();
        if (paymentStatus == null) {
            paymentStatus = summarizePaymentStatus(payments);
        }

        return PurchaseOrderDTO.from(order, itemDTOs, payments, partiallyRefunded, paymentStatus);
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
