 /** 
 * @author Yunhe & Ying Chun
 * @date 2025-10-08 (v1.0, v2.0)
 * @date 2025-10-10 (v2.1, 2.2)
 * @version 1.0 - initial version
 * @version 2.0 - added new fields to map to database changes
 * @version 2.1 - removed inStock field, added stockQuantity field
 * @version 2.2 - added a way to convert colors JSON string and sizes JSON string to List of Maps for easier frontend handling
 */

package sg.com.aori.model;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import java.util.Collections;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import java.util.List;

/**
 * Entity representing a product in the system.
 * Updated entity (see changes)
 * 
 * JSON example:
 * {
 * "productCode": "AORI-KSA-0001",
 * "productName": "Classic Polo Shirt",
 * "description": "High quality polo shirt",
 * "categoryId": "00c41711-68b0-4d03-a00b-67c6fba6ad87",
 * "collection": "Summer 2025",
 * "material": "Cotton",
 * "season": "Summer",
 * "careInstructions": "Machine wash cold",
 * "colors": "[\"#000000\", \"#FFFFFF\", \"#000080\"]",
 * "image":
 * "https://images.unsplash.com/photo-1583743814966-8936f5b7be1a?w=400&h=600&fit=crop",
 * "price": 249,
 * "stockQuantity": 20,
 * "size": "[\"XS\", \"S\", \"M\", \"L\", \"XL\"]",
 * "rating": 4.7,
 * "tags": "best-seller"
 * }

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
    // private Short price;
    private BigDecimal price;

    /* To remove
    / @Column(name = "inStock", length = 255)
    / private String inStock = "true"
    */ 

    // added by YC
    @Column(name = "stock_quantity", nullable = false)
    private Integer stockQuantity = 0;

    @Column(name = "size", columnDefinition = "JSON")
    private String size; // JSON array: ["XS", "S", "M", "L", "XL"]

    @Column(name = "rating")
    @DecimalMin(value = "0.0", message = "Rating must be at least 0.0.")
    @DecimalMax(value = "5.0", message = "Rating must not exceed 5.0.")
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

    // public Short getPrice() {
    public BigDecimal getPrice() {
        return price;
    }

    // public void setPrice(Short price) {
    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    /* remove
     public String getInStock() {
        return inStock;
    }

    public void setInStock(String inStock) {
        this.inStock = inStock;
    }
    */

    public Integer getStockQuantity() {
        return stockQuantity;
    }

    public void setStockQuantity(Integer stockQuantity) {
        this.stockQuantity = stockQuantity;
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
		return "Product [productId=" + productId + ", productCode=" + productCode + ", productName=" + productName
				+ ", description=" + description + ", categoryId=" + categoryId + ", collection=" + collection
				+ ", material=" + material + ", season=" + season + ", careInstructions=" + careInstructions
				+ ", createdAt=" + createdAt + ", updatedAt=" + updatedAt + ", colors=" + colors + ", image=" + image
				+ ", price=" + price + ", stockQuantity=" + stockQuantity + ", size=" + size + ", rating=" + rating
				+ ", tags=" + tags + ", category=" + category + "]";
	}

    // UTILITY METHODS TO DEAL WITH JSON FIELDS

    // Utility method to convert colors JSON string to a List of Strings
    @Transient
    @JsonIgnore
    public List<String> getColorsAsList() {
        if (this.colors == null || this.colors.isBlank()) {
            return Collections.emptyList();
        }
        try {
            ObjectMapper mapper = new ObjectMapper();
            // Parse into a simple List of Strings
            return mapper.readValue(this.colors, new TypeReference<List<String>>() {});
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    // Utility method to convert size JSON string to a List of Strings
    @Transient
    @JsonIgnore
    public List<String> getSizesAsList() {
        if (this.size == null || this.size.isBlank()) {
            return Collections.emptyList();
        }
        try {
            ObjectMapper mapper = new ObjectMapper();
            // Parse into a simple List of Strings
            return mapper.readValue(this.size, new TypeReference<List<String>>() {});
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

}
