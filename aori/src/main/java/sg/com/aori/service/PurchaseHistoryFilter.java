package sg.com.aori.service;

import java.time.LocalDateTime;

import sg.com.aori.model.Orders;
/**
 * purchasehistory
 *
 * @author Jiayi
 * @date 2025-10-08
 * @version 1.0
 */
/**
 * 购买历史查询过滤条件
 * - customerId：必须
 * - from/to：可选的时间区间（创建时间）
 * - orderStatus/paymentStatus：可选状态筛选
 */
public class PurchaseHistoryFilter {

    private String customerId;                 // 必填
    private LocalDateTime from;               // 可选
    private LocalDateTime to;                 // 可选
    private Orders.OrderStatus orderStatus;   // 可选：包含取消、未支付等
    private Orders.PaymentStatus paymentStatus; // 可选：进一步按支付状态过滤结果（在 Service 层完成）

    public String getCustomerId() { return customerId; }
    public void setCustomerId(String customerId) { this.customerId = customerId; }

    public LocalDateTime getFrom() { return from; }
    public void setFrom(LocalDateTime from) { this.from = from; }

    public LocalDateTime getTo() { return to; }
    public void setTo(LocalDateTime to) { this.to = to; }

    public Orders.OrderStatus getOrderStatus() { return orderStatus; }
    public void setOrderStatus(Orders.OrderStatus orderStatus) { this.orderStatus = orderStatus; }

    public Orders.PaymentStatus getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(Orders.PaymentStatus paymentStatus) { this.paymentStatus = paymentStatus; }
}
