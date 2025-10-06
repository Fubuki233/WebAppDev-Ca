//Code by Ying Chun
//need to crosscheck if PK should be UUID or AUTO

package sg.com.aori.model;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "product")
public class Product {
	
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    // product_id VARCHAR(36) PRIMARY KEY DEFAULT
    @Column(name = "product_id", length = 36)
    private String productId;
    
    // product_name VARCHAR(150) NOT NULL
    @Column(name = "product_name", length = 150, nullable = false)
    private String productName;
    
    // description TEXT
    @Column(name = "description")
    private String description;
    
    // ManyToOne relationship with the Category entity
    // category_id VARCHAR(36) NOT NULL
    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;
    
    // collection VARCHAR(100)
    @Column(name = "collection", length = 100)
    private String collection;

    // material VARCHAR(100)
    @Column(name = "material", length = 100)
    private String material;
    
    // season ENUM('Spring', 'Summer', 'Autumn', 'Winter', 'All_Season')
    @Enumerated(EnumType.STRING)
    @Column(name = "season")
    private Season season;
    
    public enum Season {
    	Spring, Summer, Autumn, Winter, All_Season
    }
    
    // care_instructions TEXT
    @Column(name = "care_instructions")
    private String careInstructions;
    
    // created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
    @CreationTimestamp
    @Column(name="created_at", updatable = false)
    private LocalDateTime createdAt;
    
    // updated_at TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
    @UpdateTimestamp
    @Column(name="updated_at", updatable = true)
    private LocalDateTime updatedAt;
    
    // Constructors
    
    public Product() {}
    
    public Product(String productName, String description, Category category,
    String collection, String material, Season season, String careInstructions) {
    	this.productName = productName;
    	this.description = description;
    	this.category = category;
    	this.collection = collection;
    	this.material = material;
    	this.season = season;
    	this.careInstructions = careInstructions;
    }

    // Getters and Setters
    
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

	public Category getCategory() {
		return category;
	}

	public void setCategory(Category category) {
		this.category = category;
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

    // Getters for Time Stamps

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public LocalDateTime getUpdatedAt() {
		return updatedAt;
	}
    
}
