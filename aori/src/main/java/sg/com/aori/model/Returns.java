package sg.com.aori.model;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

@Entity
@Table(name = "returns")
public class Returns {

    public enum ReturnReason {
        Size_issue,
        Defective,
        Changed_mind,
        Other
    }

    public enum ReturnStatus {
        Requested, // Initial status
        Approved,
        Denied,
        Refunded,
        Exchange
    }

    @Id
    @Column(name = "return_id", length = 36, nullable = false)
    private String returnId; // FIX: Removed direct initialization

    @Column(name = "return_code", length = 30, nullable = false, unique = true)
    private String returnCode;

    // Foreign Key string field
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

    // ManyToOne relationship to OrderItem
    // NOTE: This setup relies on the service/controller setting orderItemId string.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_item_id", insertable = false, updatable = false)
    private OrderItem orderItem;

    /**
     * JPA Callback method to set ID, creation timestamp, and initial status.
     */
    @PrePersist
    protected void onCreate() {
        // Centralize ID generation logic
        if (this.returnId == null) {
            this.returnId = UUID.randomUUID().toString();
        }

        this.createdAt = LocalDateTime.now();

        // Set initial status if not already set
        if (this.returnStatus == null) {
            this.returnStatus = ReturnStatus.Requested;
        }
    }

    // --- Constructors ---

    public Returns() {
    }

    public Returns(String returnCode, String orderItemId, ReturnReason reason, ReturnStatus returnStatus) {
        this.returnCode = returnCode;
        this.orderItemId = orderItemId;
        this.reason = reason;
        this.returnStatus = returnStatus;
    }

    // --- Getters and Setters ---

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
        // Optionally update the foreign key string if the object is set
        if (orderItem != null) {
            this.orderItemId = orderItem.getOrderItemId();
        }
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