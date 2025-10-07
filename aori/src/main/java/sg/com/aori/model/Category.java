package sg.com.aori.model;

import jakarta.persistence.*;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "Category")
public class Category {

    public enum BroadCategory {
        Men,
        Women,
        Girls,
        Boys,
        Unisex
    }

    @Id
    @Column(name = "category_id", length = 36, nullable = false)
    private String categoryId;

    @Column(name = "category_code", length = 20, nullable = false, unique = true)
    private String categoryCode;

    @Column(name = "category_name", length = 100, nullable = false)
    private String categoryName;

    @Enumerated(EnumType.STRING)
    @Column(name = "broad_category_id", nullable = false)
    private BroadCategory broadCategoryId;

    @Column(name = "slug", length = 100)
    private String slug;

    @OneToMany(mappedBy = "category", fetch = FetchType.LAZY)

    @JsonIgnore // to avoid serialization issues, otherwise will
                // generate infinite recursion
    private List<Product> products;

    public Category() {
    }

    public Category(String categoryId, String categoryCode, String categoryName, BroadCategory broadCategoryId) {
        this.categoryId = categoryId;
        this.categoryCode = categoryCode;
        this.categoryName = categoryName;
        this.broadCategoryId = broadCategoryId;
    }

    @PrePersist
    protected void onCreate() {
        if (this.categoryId == null || this.categoryId.isEmpty()) {
            this.categoryId = java.util.UUID.randomUUID().toString();
        }
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryCode() {
        return categoryCode;
    }

    public void setCategoryCode(String categoryCode) {
        this.categoryCode = categoryCode;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public BroadCategory getBroadCategoryId() {
        return broadCategoryId;
    }

    public void setBroadCategoryId(BroadCategory broadCategoryId) {
        this.broadCategoryId = broadCategoryId;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }

    @Override
    public String toString() {
        return "{" +
                "categoryId='" + categoryId + '\'' +
                ", categoryCode='" + categoryCode + '\'' +
                ", categoryName='" + categoryName + '\'' +
                ", broadCategoryId=" + broadCategoryId +
                ", slug='" + slug + '\'' +
                '}';
    }
}
