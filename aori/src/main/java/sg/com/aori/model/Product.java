package sg.com.aori.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Entity representing a product in the system.
 * Updated entity
 *
 * @author Yunhe
 * @date 2025-10-08
 * @version 2.1
 */
@Entity
@Table(name = "product")
public class Product {

    @Id
    @Column(name = "product_id", length = 36, nullable = false)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String productId;

    @Column(name = "product_code", length = 20, nullable = false, unique = true)
    private String productCode;

    @Column(name = "product_name", length = 150, nullable = false)
    private String productName;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "category_id", length = 36, nullable = false)
    private String categoryId;

    @Column(name = "collection", length = 100, nullable = false)
    private String collection;

    @Column(name = "material", length = 100)
    private String material;

    @Enumerated(EnumType.STRING)
    @Column(name = "season")
    private Season season;

    @Column(name = "care_instructions", columnDefinition = "TEXT")
    private String careInstructions;

    @Column(name = "created_at", updatable = false)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDateTime updatedAt;

    @Column(name = "colors", columnDefinition = "JSON", nullable = false)
    private String colors; // JSON array: ["#000000", "#FFFFFF"]

    @Column(name = "image", length = 255)
    private String image;

    @Column(name = "price", nullable = false)
    private Short price;

    @Column(name = "inStock", length = 255)
    private String inStock = "true";

    @Column(name = "size", columnDefinition = "JSON")
    private String size; // JSON array: ["XS", "S", "M", "L", "XL"]

    @Column(name = "rating")
    private Float rating = 5.0f;

    @Column(name = "tags", length = 255)
    private String tags;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", insertable = false, updatable = false)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Category category;

    public enum Season {
        Spring,
        Summer,
        Autumn,
        Winter,
        All_Season
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (this.productId == null || this.productId.isEmpty()) {
            this.productId = UUID.randomUUID().toString();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public Product() {
    }

    public Product(String productName, String categoryId) {
        this.productName = productName;
        this.categoryId = categoryId;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    public String getCollection() {
        return collection;
    }

    public void setCollection(String collection) {
        this.collection = collection;
    }

    public String getMaterial() {
        return material;
    }

    public void setMaterial(String material) {
        this.material = material;
    }

    public Season getSeason() {
        return season;
    }

    public void setSeason(Season season) {
        this.season = season;
    }

    public String getCareInstructions() {
        return careInstructions;
    }

    public void setCareInstructions(String careInstructions) {
        this.careInstructions = careInstructions;
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

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public String getColors() {
        return colors;
    }

    public void setColors(String colors) {
        this.colors = colors;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Short getPrice() {
        return price;
    }

    public void setPrice(Short price) {
        this.price = price;
    }

    public String getInStock() {
        return inStock;
    }

    public void setInStock(String inStock) {
        this.inStock = inStock;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public Float getRating() {
        return rating;
    }

    public void setRating(Float rating) {
        this.rating = rating;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    @Override
    public String toString() {
        return "{" +
                "\"productId\":\"" + productId + "\"" +
                ", \"productCode\":\"" + productCode + "\"" +
                ", \"productName\":\"" + productName + "\"" +
                ", \"description\":\"" + description + "\"" +
                ", \"categoryId\":\"" + categoryId + "\"" +
                ", \"collection\":\"" + collection + "\"" +
                ", \"material\":\"" + material + "\"" +
                ", \"season\":\"" + season + "\"" +
                ", \"careInstructions\":\"" + careInstructions + "\"" +
                ", \"colors\":" + colors +
                ", \"image\":\"" + image + "\"" +
                ", \"price\":" + price +
                ", \"inStock\":\"" + inStock + "\"" +
                ", \"size\":" + size +
                ", \"rating\":" + rating +
                ", \"tags\":\"" + tags + "\"" +
                ", \"createdAt\":\"" + createdAt + "\"" +
                ", \"updatedAt\":\"" + updatedAt + "\"" +
                '}';
    }
}
