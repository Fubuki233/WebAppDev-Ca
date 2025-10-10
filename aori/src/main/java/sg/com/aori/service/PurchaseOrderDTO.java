package sg.com.aori.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import sg.com.aori.model.Orders;
import sg.com.aori.model.Payment;
/**
 * purchasehistory
 *
 * @author Jiayi
 * @date 2025-10-08
 * @version 1.0
 */
/**
 * 订单头 DTO（含订单行 + 支付概要 + 部分退款标记）
 */
public class PurchaseOrderDTO {

    private String orderId;
    private String orderNumber;
    private String customerId;

    private Orders.OrderStatus orderStatus;       // 订单状态：Pending/Paid/Cancelled/Delivered/Returned ...
    private Orders.PaymentStatus paymentStatus;   // 支付概要：Pending/Paid/Refunded/Failed
    private BigDecimal totalAmount;               // 订单总额
    private LocalDateTime createdAt;

    private boolean partiallyRefunded;            // 是否“部分退款”
    private List<PurchaseOrderItemDTO> items;     // 订单行（含商品详情）

    // 这里不直接透出具体支付记录，可按需要追加支付流水 DTO
    public static PurchaseOrderDTO from(Orders o,
                                        List<PurchaseOrderItemDTO> items,
                                        List<Payment> payments,
                                        boolean partiallyRefunded,
                                        Orders.PaymentStatus paymentStatus) {
        PurchaseOrderDTO d = new PurchaseOrderDTO();
        d.orderId = o.getOrderId();
        d.orderNumber = o.getOrderNumber();
        d.customerId = o.getCustomerId();
        d.orderStatus = o.getOrderStatus();
        d.paymentStatus = paymentStatus != null ? paymentStatus : o.getPaymentStatus();
        d.totalAmount = o.getTotalAmount();
        d.createdAt = o.getCreatedAt();
        d.items = items;
        d.partiallyRefunded = partiallyRefunded;
        return d;
    }

    // getter/setter
    public String getOrderId() { return orderId; }
    public String getOrderNumber() { return orderNumber; }
    public String getCustomerId() { return customerId; }
    public Orders.OrderStatus getOrderStatus() { return orderStatus; }
    public Orders.PaymentStatus getPaymentStatus() { return paymentStatus; }
    public BigDecimal getTotalAmount() { return totalAmount; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public boolean isPartiallyRefunded() { return partiallyRefunded; }
    public List<PurchaseOrderItemDTO> getItems() { return items; }

    public void setOrderId(String orderId) { this.orderId = orderId; }
    public void setOrderNumber(String orderNumber) { this.orderNumber = orderNumber; }
    public void setCustomerId(String customerId) { this.customerId = customerId; }
    public void setOrderStatus(Orders.OrderStatus orderStatus) { this.orderStatus = orderStatus; }
    public void setPaymentStatus(Orders.PaymentStatus paymentStatus) { this.paymentStatus = paymentStatus; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public void setPartiallyRefunded(boolean partiallyRefunded) { this.partiallyRefunded = partiallyRefunded; }
    public void setItems(List<PurchaseOrderItemDTO> items) { this.items = items; }
}

