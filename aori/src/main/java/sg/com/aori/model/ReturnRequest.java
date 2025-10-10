package sg.com.aori.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entity representing a customer's request to return an order item.
 * This record is persisted when a return request is initiated.
 */

/**
 * @author Simon Lei
 * @date 2025-10-10
 * @version 1.0
 */

/**
 * @author Xiaobo
 * @date 2025-10-15
 * @version 1.1 
 */
@Entity
@Table(name = "return_request")
public class ReturnRequest {

    // --- Enumerations for Status Tracking ---
    public enum ReturnStatus {
        PENDING_APPROVAL, // Initial status after submission
        APPROVED, // Approved by policy, awaiting item
        REJECTED, // Rejected due to policy violation
        RECEIVED_INSPECTION, // Item received, under quality check
        COMPLETED_REFUND, // Refund processed
        CANCELLED // Customer cancelled return
    }

    // --- Entity Fields ---

    @Id
    @Column(name = "return_id", length = 36, nullable = false)
    private String returnId;

    @Column(name = "order_id", length = 36, nullable = false)
    private String orderId;

    @Column(name = "product_id", length = 36, nullable = false)
    private String productId;

    @Column(name = "customer_id", length = 36, nullable = false)
    private String customerId;

    @Column(name = "request_reason", columnDefinition = "TEXT", nullable = false)
    private String requestReason;

    @Enumerated(EnumType.STRING)
    @Column(name = "return_status", nullable = false)
    private ReturnStatus returnStatus;

    @Column(name = "refund_amount")
    private BigDecimal refundAmount; // The amount to be refunded (can be less than total price)

    @Column(name = "processing_notes", columnDefinition = "TEXT")
    private String processingNotes; // Internal notes added during processing/inspection

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // --- Relationships (Optional but Recommended) ---

    // Links to the Order entity (ManyToOne relationship is implied via orderId FK)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", insertable = false, updatable = false)
    private Orders order;

    // --- JPA Lifecycle Callbacks (Auditing) ---

    @PrePersist
    protected void onCreate() {
        if (this.returnId == null) {
            this.returnId = UUID.randomUUID().toString();
        }
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        // Set initial status upon creation
        if (this.returnStatus == null) {
            this.returnStatus = ReturnStatus.PENDING_APPROVAL;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // --- Constructors ---

    public ReturnRequest() {
        // Default no-arg constructor required by JPA
    }

    /**
     * Constructor for creating a new return request from a DTO.
     */
    public ReturnRequest(String orderId, String productId, String customerId, String requestReason) {
        this.orderId = orderId;
        this.productId = productId;
        this.customerId = customerId;
        this.requestReason = requestReason;
        // Status and ID will be set in @PrePersist
    }

    // --- Getters and Setters (Omitted for brevity, use Lombok in production) ---

    public String getReturnId() {
        return returnId;
    }

    public void setReturnId(String returnId) {
        this.returnId = returnId;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getRequestReason() {
        return requestReason;
    }

    public void setRequestReason(String requestReason) {
        this.requestReason = requestReason;
    }

    public ReturnStatus getReturnStatus() {
        return returnStatus;
    }

    public void setReturnStatus(ReturnStatus returnStatus) {
        this.returnStatus = returnStatus;
    }

    public BigDecimal getRefundAmount() {
        return refundAmount;
    }

    public void setRefundAmount(BigDecimal refundAmount) {
        this.refundAmount = refundAmount;
    }

    public String getProcessingNotes() {
        return processingNotes;
    }

    public void setProcessingNotes(String processingNotes) {
        this.processingNotes = processingNotes;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Orders getOrder() {
        return order;
    }

    public void setOrder(Orders order) {
        this.order = order;
    }

    @Override
    public String toString() {
        return "ReturnRequest{" +
                "returnId='" + returnId + '\'' +
                ", orderId='" + orderId + '\'' +
                ", productId='" + productId + '\'' +
                ", customerId='" + customerId + '\'' +
                ", requestReason='" + requestReason + '\'' +
                ", returnStatus=" + returnStatus +
                ", refundAmount=" + refundAmount +
                ", processingNotes='"
                + (processingNotes != null
                        ? processingNotes.substring(0, Math.min(processingNotes.length(), 50)) + "..."
                        : "null")
                + '\'' + // Truncate long text
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }

}