package sg.com.aori.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "ShoppingCart")
public class ShoppingCart {

    @Id
    @Column(name = "cart_id", length = 36, nullable = false)
    private String cartId = UUID.randomUUID().toString();

    @Column(name = "customer_id", length = 36, nullable = false)
    private String customerId;

    @Column(name = "variant_id", length = 36, nullable = false)
    private String variantId;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @Column(name = "added_at", updatable = false)
    private LocalDateTime addedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", insertable = false, updatable = false)
    private Customer customer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "variant_id", insertable = false, updatable = false)
    private ProductVariant variant;

    @PrePersist
    protected void onCreate() {
        addedAt = LocalDateTime.now();
    }

    public ShoppingCart() {
    }

    public ShoppingCart(String customerId, String variantId, Integer quantity) {
        this.customerId = customerId;
        this.variantId = variantId;
        this.quantity = quantity;
    }

    public String getCartId() {
        return cartId;
    }

    public void setCartId(String cartId) {
        this.cartId = cartId;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getVariantId() {
        return variantId;
    }

    public void setVariantId(String variantId) {
        this.variantId = variantId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public LocalDateTime getAddedAt() {
        return addedAt;
    }

    public void setAddedAt(LocalDateTime addedAt) {
        this.addedAt = addedAt;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public ProductVariant getVariant() {
        return variant;
    }

    public void setVariant(ProductVariant variant) {
        this.variant = variant;
    }

    @Override
    public String toString() {
        return "{" +
                "cartId='" + cartId + '\'' +
                ", customerId='" + customerId + '\'' +
                ", variantId='" + variantId + '\'' +
                ", quantity=" + quantity +
                ", addedAt=" + addedAt +
                '}';
    }
}
