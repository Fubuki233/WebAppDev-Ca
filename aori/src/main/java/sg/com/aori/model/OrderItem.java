/**
 * Jiang: Modfied variant into product
 * Sun Rui: add validation constraint
 * @date: 10-09
 */

package sg.com.aori.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.UUID;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

@Entity
@Table(name = "order_item")
public class OrderItem {

    @Id
    @Column(name = "order_item_id", length = 36, nullable = false)
    private String orderItemId = UUID.randomUUID().toString();

    @NotBlank(message = "orderId is required")
    @Column(name = "order_id", length = 36, nullable = false)
    private String orderId;

    @NotBlank(message = "productId is required")
    @Column(name = "product_id", length = 36, nullable = false)
    private String productId;

    @Positive(message = "quantity must be greater than zero")
    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @NotNull(message = "priceAtPurchase is required")
    @DecimalMin(value = "0.00", message = "priceAtPurchase must be >= 0")
    @Column(name = "price_at_purchase", precision = 10, scale = 2, nullable = false)
    private BigDecimal priceAtPurchase;

    @Column(name = "discount_applied", precision = 10, scale = 2)
    private BigDecimal discountApplied = BigDecimal.ZERO;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", insertable = false, updatable = false)
    private Orders order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", insertable = false, updatable = false)
    private Product product;

    public OrderItem() {
    }

    public OrderItem(String orderId, String productId, Integer quantity, BigDecimal priceAtPurchase) {
        this.orderId = orderId;
        this.productId = productId;
        this.quantity = quantity;
        this.priceAtPurchase = priceAtPurchase;
    }

    public String getOrderItemId() {
        return orderItemId;
    }

    public void setOrderItemId(String orderItemId) {
        this.orderItemId = orderItemId;
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

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getPriceAtPurchase() {
        return priceAtPurchase;
    }

    public void setPriceAtPurchase(BigDecimal priceAtPurchase) {
        this.priceAtPurchase = priceAtPurchase;
    }

    public BigDecimal getDiscountApplied() {
        return discountApplied;
    }

    public void setDiscountApplied(BigDecimal discountApplied) {
        this.discountApplied = discountApplied;
    }

    public Orders getOrder() {
        return order;
    }

    public void setOrder(Orders order) {
        this.order = order;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    /**
     * xiaobo
     * 2025-10-09
     * Calculates and returns the net total price paid for this line item (used for
     * refund calculation).
     * Calculation: (priceAtPurchase - discountApplied) * quantity
     * * @return The BigDecimal amount paid for this specific line item.
     */
    public BigDecimal getPrice() {
        // 1. Calculate the final price per unit: (priceAtPurchase - discountApplied)
        BigDecimal netPricePerUnit = this.priceAtPurchase.subtract(
                this.discountApplied != null ? this.discountApplied : BigDecimal.ZERO);

        // 2. Multiply by the quantity to get the total paid amount for the line item
        // Note: We use the Integer value of quantity converted to BigDecimal for the
        // math.
        BigDecimal quantityBigDecimal = new BigDecimal(this.quantity);

        return netPricePerUnit.multiply(quantityBigDecimal);
    }

    @Override
    public String toString() {
        return "{" +
                "orderItemId='" + orderItemId + '\'' +
                ", orderId='" + orderId + '\'' +
                ", productId='" + productId + '\'' +
                ", quantity=" + quantity +
                ", priceAtPurchase=" + priceAtPurchase +
                ", discountApplied=" + discountApplied +
                '}';
    }
}
