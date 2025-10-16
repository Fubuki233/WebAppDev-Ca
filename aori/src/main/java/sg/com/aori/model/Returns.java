package sg.com.aori.model;

import java.time.LocalDateTime;
import java.util.UUID;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;

/**
 * Update on the Returns entity to add some validations.
 * 
 * @author Lei Nuozhen
 * @date 2025-10-11
 * @version 2.0
 */

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
        Requested,
        Approved,
        Denied,
        Refunded,
        Exchange
    }

    public interface OnCreate {
    }

    public interface OnUpdate {
    }

    @Id
    @Column(name = "return_id", length = 36, nullable = false)
    @Null(groups = OnCreate.class, message = "Return code must be null when creating a new return.")
    @NotBlank(groups = OnUpdate.class, message = "Return code is required when updating a return.")
    private String returnId;

    @Column(name = "return_code", length = 30, nullable = false, unique = true)
    @Null(groups = OnCreate.class, message = "Return code must be null when creating a new return.")
    @NotBlank(groups = OnUpdate.class, message = "Return code is required when updating a return.")
    private String returnCode;

    @Column(name = "order_item_id", length = 36, nullable = false)
    @NotBlank(message = "Order Item ID is required.")
    @Size(max = 36, message = "orderItemId too long")
    @Pattern(regexp = "^[a-fA-F0-9\\-]{36}$", message = "orderItemId must be a valid UUID format.")
    private String orderItemId;

    @Enumerated(EnumType.STRING)
    @Column(name = "reason", nullable = false)
    @NotNull(message = "Return reason is required.")
    private ReturnReason reason;

    @Enumerated(EnumType.STRING)
    @Column(name = "return_status", nullable = false)
    @NotNull(message = "returnStatus is required.")
    private ReturnStatus returnStatus;

    @Column(name = "created_at", updatable = false)
    @PastOrPresent(message = "createdAt must be in the past or present.")
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_item_id", insertable = false, updatable = false)
    private OrderItem orderItem;

    @PrePersist
    protected void onCreate() {
        if (this.returnId == null) {
            this.returnId = UUID.randomUUID().toString();
        }

        this.createdAt = LocalDateTime.now();

        if (this.returnStatus == null) {
            this.returnStatus = ReturnStatus.Requested;
        }
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