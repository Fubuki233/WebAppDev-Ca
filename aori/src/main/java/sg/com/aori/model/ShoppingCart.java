package sg.com.aori.model;

import java.time.LocalDateTime;
import java.util.UUID;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;

/**
 * @author Yibai
 * @version 1.0
 * @version 1.1 - Modified variant related code, Add constraints to entities
 * @version 1.2 - Added sku
 * 
 * @author Sun Rui
 * @date 2025-10-15
 * @version 1.4 - Added unique constraint on customer_id + product_id + sku
 */

@Entity
@Table(name = "shopping_cart", uniqueConstraints = @UniqueConstraint(columnNames = { "customer_id", "product_id",
        "sku" }))
public class ShoppingCart {

    @Id
    @Column(name = "cart_id", length = 36, nullable = false)
    private String cartId = UUID.randomUUID().toString();

    @NotBlank(message = "customerId is required")
    @Column(name = "customer_id", length = 36, nullable = false)
    private String customerId;

    @NotBlank(message = "productId is required")
    @Column(name = "product_id", length = 36, nullable = false)
    private String productId;

    @Positive(message = "quantity must be greater than zero")
    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @Column(name = "added_at", updatable = false)
    private LocalDateTime addedAt;

    @NotBlank(message = "sku is required")
    @Column(name = "sku", length = 50, nullable = false)
    private String sku;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", insertable = false, updatable = false)
    private Customer customer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", insertable = false, updatable = false)
    private Product product;

    @PrePersist
    protected void onCreate() {
        addedAt = LocalDateTime.now();
    }

    public ShoppingCart() {
    }

    public ShoppingCart(String customerId, String productId, Integer quantity, String sku) {
        this.customerId = customerId;
        this.productId = productId;
        this.quantity = quantity;
        this.sku = sku;
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

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    @Override
    public String toString() {
        return "{" +
                "cartId='" + cartId + '\'' +
                ", customerId='" + customerId + '\'' +
                ", productId='" + productId + '\'' +
                ", quantity=" + quantity +
                ", addedAt=" + addedAt +
                '}';
    }
}
