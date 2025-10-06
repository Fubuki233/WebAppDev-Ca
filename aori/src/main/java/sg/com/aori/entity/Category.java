// This 'entity' folder is added by JYB, Including all entitis in 'fashion' database
// It should be 'model' but I dont want to overwrite the existing files...
// *** Check details, especilly the name of databases' tables
// *** All statement in main methods are annotated, check if we need them


package sg.com.aori.entity;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "Category")
public class Category {
    @Id
    @Column(name = "category_id", nullable = false, length = 36)
    private String id;
    
    @Column(name = "category_name", nullable = false, length = 100)
    private String categoryName;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "broad_category_id", nullable = false)
    private BroadCategory broadCategory;
    
    @Column(name = "slug", length = 100)
    private String slug;
    
    public Category() {
        // this.id = UUID.randomUUID().toString();
    }
    
    public enum BroadCategory {
        Men, Women, Girls, Boys, Unisex
    }
    
    // Getters and Setters
    public String getId() { return id; }
    public void setId() { this.id = UUID.randomUUID().toString(); }
    public void setId(String id) { this.id = id; }
    
    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }
    
    public BroadCategory getBroadCategory() { return broadCategory; }
    public void setBroadCategory(BroadCategory broadCategory) { this.broadCategory = broadCategory; }
    
    public String getSlug() { return slug; }
    public void setSlug(String slug) { this.slug = slug; }
}