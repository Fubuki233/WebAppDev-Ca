package sg.com.aori.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "Order_Promotion")
public class OrderPromotion {
    @Id
    @Column(name = "order_promotion_id", nullable = false, length = 36)
    private String id;
    
    @ManyToOne
    @JoinColumn(name = "order_id", nullable = false)
    private Orders order;
    
    @ManyToOne
    @JoinColumn(name = "promotion_id", nullable = false)
    private PromotionDiscount promotion;
    
    @Column(name = "discount_amount_applied", nullable = false, precision = 18, scale = 2)
    private BigDecimal discountAmountApplied;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    public OrderPromotion() {
        // this.id = UUID.randomUUID().toString();
        // this.createdAt = LocalDateTime.now();
    }
    
    // Getters and Setters
    public String getId() { return id; }
    public void setId() { this.id = UUID.randomUUID().toString(); }
    public void setId(String id) { this.id = id; }
    
    public Orders getOrder() { return order; }
    public void setOrder(Orders order) { this.order = order; }
    
    public PromotionDiscount getPromotion() { return promotion; }
    public void setPromotion(PromotionDiscount promotion) { this.promotion = promotion; }
    
    public BigDecimal getDiscountAmountApplied() { return discountAmountApplied; }
    public void setDiscountAmountApplied(BigDecimal discountAmountApplied) { this.discountAmountApplied = discountAmountApplied; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}