package sg.com.aori.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "Payment")
public class Payment {

    public enum PaymentStatus {
        Success,
        Failed,
        Pending,
        Refunded
    }

    @Id
    @Column(name = "payment_id", length = 36, nullable = false)
    private String paymentId = UUID.randomUUID().toString();

    @Column(name = "payment_code", length = 30, unique = true)
    private String paymentCode;

    @Column(name = "order_id", length = 36, nullable = false)
    private String orderId;

    @Column(name = "payment_method", length = 50, nullable = false)
    private String paymentMethod;

    @Column(name = "amount", precision = 10, scale = 2, nullable = false)
    private BigDecimal amount;

    @Column(name = "currency", length = 3, nullable = false)
    private String currency;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status", nullable = false)
    private PaymentStatus paymentStatus;

    @Column(name = "transaction_reference", length = 255, nullable = false, unique = true)
    private String transactionReference;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", insertable = false, updatable = false)
    private Orders order;

    public Payment() {
    }

    public Payment(String orderId, String paymentMethod, BigDecimal amount, String currency,
            PaymentStatus paymentStatus, String transactionReference) {
        this.orderId = orderId;
        this.paymentMethod = paymentMethod;
        this.amount = amount;
        this.currency = currency;
        this.paymentStatus = paymentStatus;
        this.transactionReference = transactionReference;
    }

    public String getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }

    public String getPaymentCode() {
        return paymentCode;
    }

    public void setPaymentCode(String paymentCode) {
        this.paymentCode = paymentCode;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public PaymentStatus getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(PaymentStatus paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public String getTransactionReference() {
        return transactionReference;
    }

    public void setTransactionReference(String transactionReference) {
        this.transactionReference = transactionReference;
    }

    public Orders getOrder() {
        return order;
    }

    public void setOrder(Orders order) {
        this.order = order;
    }

    @Override
    public String toString() {
        return "{" +
                "paymentId='" + paymentId + '\'' +
                ", paymentCode='" + paymentCode + '\'' +
                ", orderId='" + orderId + '\'' +
                ", paymentMethod='" + paymentMethod + '\'' +
                ", amount=" + amount +
                ", currency='" + currency + '\'' +
                ", paymentStatus=" + paymentStatus +
                ", transactionReference='" + transactionReference + '\'' +
                '}';
    }
}
