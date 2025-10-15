package sg.com.aori.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;
import jakarta.persistence.*;

@Entity
@Table(name = "PromotionDiscount")
public class PromotionDiscount {

    public enum DiscountType {
        Percentage,
        Fixed_Amount,
        Free_Shipping,
        Staff_Discount
    }

    @Id
    @Column(name = "promotion_id", length = 36, nullable = false)
    private String promotionId = UUID.randomUUID().toString();

    @Column(name = "promo_code", length = 50, nullable = false, unique = true)
    private String promoCode;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "discount_type", nullable = false)
    private DiscountType discountType;

    @Column(name = "discount_value", precision = 10, scale = 2, nullable = false)
    private BigDecimal discountValue;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    public PromotionDiscount() {
    }

    public PromotionDiscount(String promoCode, DiscountType discountType, BigDecimal discountValue,
            LocalDate startDate) {
        this.promoCode = promoCode;
        this.discountType = discountType;
        this.discountValue = discountValue;
        this.startDate = startDate;
    }

    public String getPromotionId() {
        return promotionId;
    }

    public void setPromotionId(String promotionId) {
        this.promotionId = promotionId;
    }

    public String getPromoCode() {
        return promoCode;
    }

    public void setPromoCode(String promoCode) {
        this.promoCode = promoCode;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public DiscountType getDiscountType() {
        return discountType;
    }

    public void setDiscountType(DiscountType discountType) {
        this.discountType = discountType;
    }

    public BigDecimal getDiscountValue() {
        return discountValue;
    }

    public void setDiscountValue(BigDecimal discountValue) {
        this.discountValue = discountValue;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    @Override
    public String toString() {
        return "{" +
                "promotionId='" + promotionId + '\'' +
                ", promoCode='" + promoCode + '\'' +
                ", description='" + description + '\'' +
                ", discountType=" + discountType +
                ", discountValue=" + discountValue +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                '}';
    }
}
