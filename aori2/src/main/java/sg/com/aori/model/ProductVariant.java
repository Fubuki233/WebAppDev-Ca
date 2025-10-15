package sg.com.aori.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "ProductVariant")
public class ProductVariant {

    @Id
    @Column(name = "variant_id", length = 36, nullable = false)
    private String variantId = UUID.randomUUID().toString();

    @Column(name = "product_id", length = 36, nullable = false)
    private String productId;

    @Column(name = "sku", length = 50, nullable = false, unique = true)
    private String sku;

    @Column(name = "size", length = 50, nullable = false)
    private String size;

    @Column(name = "color", length = 50)
    private String color;

    @Column(name = "price", precision = 10, scale = 2, nullable = false)
    private BigDecimal price;

    @Column(name = "discount_price", precision = 10, scale = 2)
    private BigDecimal discountPrice;

    @Column(name = "stock_quantity", nullable = false)
    private Integer stockQuantity = 0;

    @Column(name = "weight", precision = 6, scale = 2)
    private BigDecimal weight;

    @Column(name = "dimensions", length = 50)
    private String dimensions;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", insertable = false, updatable = false)
    private Product product;

    public ProductVariant() {
    }

    public ProductVariant(String productId, String sku, String size, BigDecimal price) {
        this.productId = productId;
        this.sku = sku;
        this.size = size;
        this.price = price;
    }

    public String getVariantId() {
        return variantId;
    }

    public void setVariantId(String variantId) {
        this.variantId = variantId;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public BigDecimal getDiscountPrice() {
        return discountPrice;
    }

    public void setDiscountPrice(BigDecimal discountPrice) {
        this.discountPrice = discountPrice;
    }

    public Integer getStockQuantity() {
        return stockQuantity;
    }

    public void setStockQuantity(Integer stockQuantity) {
        this.stockQuantity = stockQuantity;
    }

    public BigDecimal getWeight() {
        return weight;
    }

    public void setWeight(BigDecimal weight) {
        this.weight = weight;
    }

    public String getDimensions() {
        return dimensions;
    }

    public void setDimensions(String dimensions) {
        this.dimensions = dimensions;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    @Override
    public String toString() {
        return "{" +
                "variantId='" + variantId + '\'' +
                ", productId='" + productId + '\'' +
                ", sku='" + sku + '\'' +
                ", size='" + size + '\'' +
                ", color='" + color + '\'' +
                ", price=" + price +
                ", discountPrice=" + discountPrice +
                ", stockQuantity=" + stockQuantity +
                ", weight=" + weight +
                ", dimensions='" + dimensions + '\'' +
                '}';
    }
}
