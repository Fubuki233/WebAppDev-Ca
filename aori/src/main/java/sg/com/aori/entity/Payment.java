package sg.com.aori.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "Payment")
public class Payment {
    @Id
    @Column(name = "payment_id", nullable = false, length = 36)
    private String id;
    
    @ManyToOne
    @JoinColumn(name = "order_id", nullable = false)
    private Orders order;
    
    @Column(name = "payment_method", nullable = false, length = 50)
    private String paymentMethod;
    
    @Column(name = "amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;
    
    @Column(name = "currency", nullable = false, length = 3)
    private String currency;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status", nullable = false)
    private PaymentStatus paymentStatus;
    
    @Column(name = "transaction_reference", unique = true, nullable = false, length = 255)
    private String transactionReference;
    
    public Payment() {
        // this.id = UUID.randomUUID().toString();
    }
    
    public enum PaymentStatus {
        Success, Failed, Pending, Refunded
    }
    
    // Getters and Setters
    public String getId() { return id; }
    public void setId() { this.id = UUID.randomUUID().toString(); }
    public void setId(String id) { this.id = id; }
    
    public Orders getOrder() { return order; }
    public void setOrder(Orders order) { this.order = order; }
    
    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
    
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    
    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }
    
    public PaymentStatus getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(PaymentStatus paymentStatus) { this.paymentStatus = paymentStatus; }
    
    public String getTransactionReference() { return transactionReference; }
    public void setTransactionReference(String transactionReference) { this.transactionReference = transactionReference; }
}