package sg.com.aori.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "Returns")
public class Returns {

    public enum ReturnReason {
        Size_issue,
        Defective,
        Changed_mind,
        Other
    }

    public enum ReturnStatus {
        Requested,
        Approved,
        Denied,
        Refunded,
        Exchange
    }

    @Id
    @Column(name = "return_id", length = 36, nullable = false)
    private String returnId = UUID.randomUUID().toString();

    @Column(name = "return_code", length = 30, nullable = false, unique = true)
    private String returnCode;

    @Column(name = "order_item_id", length = 36, nullable = false)
    private String orderItemId;

    @Enumerated(EnumType.STRING)
    @Column(name = "reason", nullable = false)
    private ReturnReason reason;

    @Enumerated(EnumType.STRING)
    @Column(name = "return_status", nullable = false)
    private ReturnStatus returnStatus;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_item_id", insertable = false, updatable = false)
    private OrderItem orderItem;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    public Returns() {
    }

    public Returns(String returnCode, String orderItemId, ReturnReason reason, ReturnStatus returnStatus) {
        this.returnCode = returnCode;
        this.orderItemId = orderItemId;
        this.reason = reason;
        this.returnStatus = returnStatus;
    }

    public String getReturnId() {
        return returnId;
    }

    public void setReturnId(String returnId) {
        this.returnId = returnId;
    }

    public String getReturnCode() {
        return returnCode;
    }

    public void setReturnCode(String returnCode) {
        this.returnCode = returnCode;
    }

    public String getOrderItemId() {
        return orderItemId;
    }

    public void setOrderItemId(String orderItemId) {
        this.orderItemId = orderItemId;
    }

    public ReturnReason getReason() {
        return reason;
    }

    public void setReason(ReturnReason reason) {
        this.reason = reason;
    }

    public ReturnStatus getReturnStatus() {
        return returnStatus;
    }

    public void setReturnStatus(ReturnStatus returnStatus) {
        this.returnStatus = returnStatus;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public OrderItem getOrderItem() {
        return orderItem;
    }

    public void setOrderItem(OrderItem orderItem) {
        this.orderItem = orderItem;
    }

    @Override
    public String toString() {
        return "{" +
                "returnId='" + returnId + '\'' +
                ", returnCode='" + returnCode + '\'' +
                ", orderItemId='" + orderItemId + '\'' +
                ", reason=" + reason +
                ", returnStatus=" + returnStatus +
                ", createdAt=" + createdAt +
                '}';
    }
}
