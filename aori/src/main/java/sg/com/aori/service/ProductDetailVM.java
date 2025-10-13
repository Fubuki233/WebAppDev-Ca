package sg.com.aori.service;

import java.math.BigDecimal;

import lombok.Data;

// View Model for Product Details
// Combines product info with review stats
// @author Simon Lei
// @date 2025-10-08
// @version 1.0
/*
 * @author Simon Lei
// @date 2025-10-09
// @version 1.1
 */
@Data
public class ProductDetailVM {
    private String id;
    private String productCode;
    private String name;
    private String description;
    private String image;
    private BigDecimal price;
    private Integer stockQuantity;

    private String collection;
    private String material;
    private String season;
    private String careInstructions;

    private String categoryId;
    private String categoryName;
    private String broadCategory;
    private String categorySlug;

    private Double ratingAvg;
    private Long reviewCount;
}
