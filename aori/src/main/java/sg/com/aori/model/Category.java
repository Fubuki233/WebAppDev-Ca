//Code by Ying Chun
//need to crosscheck if PK should be UUID or AUTO

package sg.com.aori.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "category")
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    // category_id VARCHAR(36) PRIMARY KEY DEFAULT
    @Column(name = "category_id", length = 36)
    private String categoryId;
    
    // category_name VARCHAR(100) NOT NULL
    @Column(name = "category_name", length = 100, nullable = false)
    private String categoryName;
    
    // broad_category_id ENUM('Men', 'Women', 'Girls', 'Boys', 'Unisex') NOT NULL
    @Enumerated(EnumType.STRING)
    @Column(name = "broad_category_id", nullable = false)
    private BroadCategory broadCategoryId;
    
    // slug VARCHAR(100)
    @Column(name = "slug", length = 100)
    private String slug;

    public enum BroadCategory {
        Men, Women, Boys, Girls, Unisex
    }
    
    // Constructors
    
    public Category() {}

    public Category(String categoryName, BroadCategory broadCategoryId, String slug) {
    	this.categoryName = categoryName;
    	this.broadCategoryId = broadCategoryId;
    	this.slug = slug;
    }

    // Getters and Setters
        
	public String getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(String categoryId) {
		this.categoryId = categoryId;
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

	@Override
	public String toString() {
		return "Category [categoryId=" + categoryId + ", categoryName=" + categoryName + ", broadCategoryId="
				+ broadCategoryId + ", slug=" + slug + "]";
	}
    
}