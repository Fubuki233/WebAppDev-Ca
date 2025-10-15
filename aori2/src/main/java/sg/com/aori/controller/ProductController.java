package sg.com.aori.controller;

import sg.com.aori.model.Product;
import sg.com.aori.service.CRUDProductService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import java.util.List;
import java.util.Optional;

/**
 * Controller class for handling product-related requests
 * This class is coded using REST API to cater for future integration with
 * mobile app.
 * Now, all the methods had been tested
 * 
 * @author Yunhe
 * @date 2025-10-07
 * @version 1.1
 * 
 * @author Ying Chun
 * @date 2025-10-10
 * @version 2.0 - Refactored to adjust HTTP responses and tie in with changes
 *          made to CRUDProductService
 * 
 * @author Yunhe
 * @date 2025-10-13
 * @version 3.0 added recommendation APIs
 */

@CrossOrigin
@RestController
@RequestMapping("/api/products")
@Validated
public class ProductController {
    @Autowired
    private CRUDProductService crudProductService;

    @Autowired
    private sg.com.aori.service.ProductRecommendationService recommendationService;

    String collectionDisplay = "Shizen";

    @PostMapping("/admin/collectionDisplay")
    public String setCollect(@RequestParam("collectionDisplay") String collectionDisplay) {
        this.collectionDisplay = collectionDisplay;
        return this.collectionDisplay;
    }

    @GetMapping("/collectionDisplay")
    public String getCollect() {
        return this.collectionDisplay;
    }

    /**
     * Create a new product.
     * The foreign key is categoryId,but for frontend creating product,
     * staff only need to select a category,
     * then the backend will find the corresponding categoryId and construct the
     * request body.
     *
     * @param product The product to create.
     * @return The created product.
     */
    @PostMapping("/admin")
    public ResponseEntity<Product> createProduct(@Valid @RequestBody Product product) {
        if (product.getProductId() == null || product.getProductId().isEmpty()) {
            product.setProductId(java.util.UUID.randomUUID().toString());
        }

        Product createdProduct = crudProductService.createProduct(product);
        System.out.println("[ProductController] Created product: " + createdProduct);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdProduct);
    }

    /**
     * Get all products or filter by category.
     * 
     * @param category Optional category slug or categoryId to filter products
     * @return List of products
     */
    @GetMapping()
    public Optional<List<Product>> getAllProducts(
            @org.springframework.web.bind.annotation.RequestParam(required = false) String category) {

        if (category != null && !category.trim().isEmpty()) {
            System.out.println("[ProductController] Fetching products by category: " + category);
            Optional<List<Product>> products = crudProductService.getProductsByCategorySlugOrId(category);
            System.out.println("[ProductController] Found " + products.map(List::size).orElse(0) + " products");
            return products;
        }

        Optional<List<Product>> products = crudProductService.getAllProducts();
        System.out.println(
                "[ProductController] Fetching all products: " + products.map(List::size).orElse(0) + " products");
        return products;
    }

    /**
     * Get a product by ID.
     * Example response:
     * 
     * {
     * "productId": "6a5f02fe-5f52-45c8-9d2d-b50f47e13a38",
     * "productCode": "PROD-000001",
     * "productName": "Classic Polo Shirt",
     * "description": "High quality polo shirt",
     * "categoryId": "00c41711-68b0-4d03-a00b-67c6fba6ad87",
     * "collection": "Summer 2025",
     * "material": "Cotton",
     * "season": "Summer",
     * "careInstructions": "Machine wash cold",
     * "createdAt": "2025-10-08T16:47:48.406149",
     * "updatedAt": "2025-10-08T16:47:48.406149",
     * "colors": "[\"#000000\", \"#FFFFFF\", \"#000080\"]",
     * "image":
     * "https://images.unsplash.com/photo-1583743814966-8936f5b7be1a?w=400&h=600&fit=crop",
     * "price": 249,
     * "inStock": "true",
     * "size": "[\"XS\", \"S\", \"M\", \"L\", \"XL\"]",
     * "rating": 4.7,
     * "tags": "best-seller",
     * "category": {
     * "categoryId": "00c41711-68b0-4d03-a00b-67c6fba6ad87",
     * "categoryCode": "CAT-M-001",
     * "categoryName": "Shirts-updated",
     * "broadCategoryId": "Men",
     * "slug": "mens-shirts",
     * "hibernateLazyInitializer": {}
     * }
     * }
     * 
     * @param id The product ID.
     * @return The product with the specified ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(
            @PathVariable @NotBlank(message = "Product ID Cannot be empty") String id) {
        System.out.println("[ProductController] Fetching product with ID: " + id);
        Optional<Product> product = crudProductService.getProductById(id);

        return product.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Update an existing product.
     * For updating the product, u dont need to provide productId in the request
     * body(but u can for convenience), and category is optional(nothing will
     * happend weather u provide it or not,
     * it always returns a null value, ingore it).
     * So for frontend developing, I suggest you to fetch the product by id first,
     * then display and edit it.
     * finally send the whole product object back to backend for updating.
     * 
     * Here is an example of request body:
     * {
     * "productCode": "PROD-000002",
     * "productName": "test-update",
     * "description": "WOW-update",
     * "categoryId": "00c41711-68b0-4d03-a00b-67c6fba6ad87",
     * "collection": "Summer Breeze",
     * "material": "Linen",
     * "season": "Summer",
     * "careInstructions": "Machine wash cold, hang dry.",
     * "createdAt": "2025-10-07T16:10:11.693456"
     * }
     * 
     * @param id      The product's id
     * @param product The product to create.
     * @return The created product.
     */
    @PutMapping("/admin/{id}")
    public ResponseEntity<Product> updateProduct(@PathVariable String id, @RequestBody Product product) {
        // This method is kept simple as per your request.
        Product updatedProduct = crudProductService.updateProduct(id, product);
        return ResponseEntity.ok(updatedProduct);
    }

    /**
     * Delete product
     */
    @DeleteMapping("/admin/{id}")
    public ResponseEntity<?> deleteProduct(@PathVariable String id) {
        try {
            Product deletedProduct = crudProductService.deleteProduct(id);
            // Success: Return 200 OK with the data of the deleted product.
            return ResponseEntity.ok(deletedProduct);
        } catch (IllegalStateException e) {
            // Failure 1: Product has existing orders. Return 409 Conflict.
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (RuntimeException e) {
            // Failure 2: Product not found. Return 404 Not Found.
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    /**
     * Get personalized product recommendations based on user purchase history
     * 
     * Algorithm:
     * 1. Analyzes user's past orders to identify preferred categories
     * 2. Recommends products from frequently purchased categories
     * 3. Filters out products already purchased by the user
     * 4. Sorts by product rating (highest first)
     * 5. Only includes products that are in stock (stockQuantity > 0)
     * 
     * If no purchase history exists, returns popular products (highest rated)
     *
     * @param customerId The customer UUID from session or query parameter
     * @param limit      Maximum number of recommendations (default: 10, max: 50)
     * @param session    HTTP session to get customer ID if not provided
     * @return List of recommended products
     */
    @GetMapping("/recommendations")
    public ResponseEntity<List<Product>> getRecommendations(
            @RequestParam(required = false) String customerId,
            @RequestParam(defaultValue = "10") int limit,
            jakarta.servlet.http.HttpSession session) {

        if (limit < 1)
            limit = 10;
        if (limit > 50)
            limit = 50;

        if (customerId == null || customerId.trim().isEmpty()) {
            customerId = (String) session.getAttribute("id");
        }

        if (customerId == null || customerId.trim().isEmpty()) {
            System.out.println("[ProductController] No customer ID provided, returning popular products");
            List<Product> recommendations = recommendationService.getRecommendations("", limit);
            return ResponseEntity.ok(recommendations);
        }

        System.out.println(
                "[ProductController] Getting recommendations for customer: " + customerId + ", limit: " + limit);
        List<Product> recommendations = recommendationService.getRecommendations(customerId, limit);
        System.out.println("[ProductController] Found " + recommendations.size() + " recommendations");

        return ResponseEntity.ok(recommendations);
    }

    /**
     * Get product recommendations based on cart contents
     * 
     * @param customerId Optional customer ID (will use session if not provided)
     * @param limit      Maximum number of recommendations (default: 10, max: 50)
     * @param session    HTTP session
     * @return List of recommended products based on cart
     */
    @GetMapping("/recommendations/cart")
    public ResponseEntity<List<Product>> getCartRecommendations(
            @RequestParam(required = false) String customerId,
            @RequestParam(defaultValue = "10") int limit,
            jakarta.servlet.http.HttpSession session) {

        if (limit < 1)
            limit = 10;
        if (limit > 50)
            limit = 50;

        if (customerId == null || customerId.trim().isEmpty()) {
            customerId = (String) session.getAttribute("id");
        }

        if (customerId == null || customerId.trim().isEmpty()) {
            System.out
                    .println("[ProductController] No customer ID for cart recommendations, returning popular products");
            List<Product> recommendations = recommendationService.getPopularProducts(limit);
            return ResponseEntity.ok(recommendations);
        }

        System.out.println(
                "[ProductController] Getting cart-based recommendations for customer: " + customerId + ", limit: "
                        + limit);
        List<Product> recommendations = recommendationService.getRecommendationsFromCart(customerId, limit);
        System.out.println("[ProductController] Found " + recommendations.size() + " cart-based recommendations");

        return ResponseEntity.ok(recommendations);
    }

    /**
     * Get product recommendations within a specific category
     * 
     * 
     * @param categoryIdOrSlug Category ID or slug
     * @param customerId       Customer UUID
     * @param limit            Maximum number of recommendations
     * @param session          HTTP session
     * @return List of recommended products from the category
     */
    @GetMapping("/recommendations/category/{categoryIdOrSlug}")
    public ResponseEntity<List<Product>> getRecommendationsByCategory(
            @PathVariable String categoryIdOrSlug,
            @RequestParam(required = false) String customerId,
            @RequestParam(defaultValue = "10") int limit,
            jakarta.servlet.http.HttpSession session) {

        if (limit < 1)
            limit = 10;
        if (limit > 50)
            limit = 50;

        if (customerId == null || customerId.trim().isEmpty()) {
            customerId = (String) session.getAttribute("id");
            if (customerId == null)
                customerId = "";
        }

        System.out.println("[ProductController] Getting category recommendations: " + categoryIdOrSlug);
        List<Product> recommendations = recommendationService.getRecommendationsByCategory(
                customerId, categoryIdOrSlug, limit);

        return ResponseEntity.ok(recommendations);
    }

    /**
     * Get similar products based on a specific product
     * Useful for "You may also like" sections on product detail pages
     * 
     * 
     * @param productId  The product ID to find similar products for
     * @param customerId Customer UUID (optional, used to exclude already purchased)
     * @param limit      Maximum number of similar products
     * @param session    HTTP session
     * @return List of similar products
     */
    @GetMapping("/{productId}/similar")
    public ResponseEntity<List<Product>> getSimilarProducts(
            @PathVariable String productId,
            @RequestParam(required = false) String customerId,
            @RequestParam(defaultValue = "8") int limit,
            jakarta.servlet.http.HttpSession session) {

        if (limit < 1)
            limit = 8;
        if (limit > 20)
            limit = 20;

        if (customerId == null || customerId.trim().isEmpty()) {
            customerId = (String) session.getAttribute("id");
            if (customerId == null)
                customerId = "";
        }

        System.out.println("[ProductController] Getting similar products for: " + productId);
        List<Product> similarProducts = recommendationService.getSimilarProducts(
                productId, customerId, limit);

        return ResponseEntity.ok(similarProducts);
    }

    /**
     * Get product recommendations based on user's browsing/view history
     * 
     * Algorithm:
     * 1. Analyzes products the user has recently viewed
     * 2. Identifies categories from viewed products
     * 3. Recommends products from frequently viewed categories
     * 4. Prioritizes by view frequency and recency
     * 5. Excludes products already viewed or purchased
     * 6. Sorts by product rating (highest first)
     * 7. Only includes in-stock products
     * 
     * @param customerId Optional customer ID (will use session if not provided)
     * @param limit      Maximum number of recommendations (default: 10, max: 50)
     * @param session    HTTP session
     * @return List of recommended products based on browsing history
     */
    @GetMapping("/recommendations/history")
    public ResponseEntity<List<Product>> getViewHistoryRecommendations(
            @RequestParam(required = false) String customerId,
            @RequestParam(defaultValue = "10") int limit,
            jakarta.servlet.http.HttpSession session) {

        if (limit < 1)
            limit = 10;
        if (limit > 50)
            limit = 50;

        if (customerId == null || customerId.trim().isEmpty()) {
            customerId = (String) session.getAttribute("id");
        }

        if (customerId == null || customerId.trim().isEmpty()) {
            System.out.println(
                    "[ProductController] No customer ID for history recommendations, returning popular products");
            List<Product> recommendations = recommendationService.getPopularProducts(limit);
            return ResponseEntity.ok(recommendations);
        }

        System.out.println(
                "[ProductController] Getting view history recommendations for customer: " + customerId + ", limit: "
                        + limit);
        List<Product> recommendations = recommendationService.getRecommendationsFromViewHistory(customerId, limit);
        System.out.println("[ProductController] Found " + recommendations.size() + " history-based recommendations");

        return ResponseEntity.ok(recommendations);
    }

}
