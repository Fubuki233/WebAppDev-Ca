package sg.com.aori.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "Order_Promotion")
public class OrderPromotion {

    @Id
    @Column(name = "order_promotion_id", length = 36, nullable = false)
    private String orderPromotionId = UUID.randomUUID().toString();

    @Column(name = "order_id", length = 36, nullable = false)
    private String orderId;

    @Column(name = "promotion_id", length = 36, nullable = false)
    private String promotionId;

    @Column(name = "discount_amount_applied", precision = 10, scale = 2, nullable = false)
    private BigDecimal discountAmountApplied;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", insertable = false, updatable = false)
    private Orders order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "promotion_id", insertable = false, updatable = false)
    private PromotionDiscount promotion;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    public OrderPromotion() {
    }

    public OrderPromotion(String orderId, String promotionId, BigDecimal discountAmountApplied) {
        this.orderId = orderId;
        this.promotionId = promotionId;
        this.discountAmountApplied = discountAmountApplied;
    }

    public String getOrderPromotionId() {
        return orderPromotionId;
    }

    public void setOrderPromotionId(String orderPromotionId) {
        this.orderPromotionId = orderPromotionId;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getPromotionId() {
        return promotionId;
    }

    public void setPromotionId(String promotionId) {
        this.promotionId = promotionId;
    }

    public BigDecimal getDiscountAmountApplied() {
        return discountAmountApplied;
    }

    public void setDiscountAmountApplied(BigDecimal discountAmountApplied) {
        this.discountAmountApplied = discountAmountApplied;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Orders getOrder() {
        return order;
    }

    public void setOrder(Orders order) {
        this.order = order;
    }

    public PromotionDiscount getPromotion() {
        return promotion;
    }

    public void setPromotion(PromotionDiscount promotion) {
        this.promotion = promotion;
    }

    @Override
    public String toString() {
        return "{" +
                "orderPromotionId='" + orderPromotionId + '\'' +
                ", orderId='" + orderId + '\'' +
                ", promotionId='" + promotionId + '\'' +
                ", discountAmountApplied=" + discountAmountApplied +
                ", createdAt=" + createdAt +
                '}';
    }
}
