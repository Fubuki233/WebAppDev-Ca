/**
 * Controller class for handling product-related requests
 * This class is coded using REST API to cater for future integration with mobile app.
 * Now, all the methods had been tested
 * 
 * @author Yunhe
 * @date 2025-10-07
 * @version 1.1
 * 
 * @author Ying Chun
 * @date 2025-10-10 (v2.0)
 * @version 2.0 - Refactored to adjust HTTP responses and tie in with changes made to CRUDProductService
 * 
 */
package sg.com.aori.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
// import org.springframework.web.bind.annotation.RequestParam; - used in old method
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import sg.com.aori.model.Product;
import sg.com.aori.service.CRUDProductService;

@CrossOrigin
@RestController
@RequestMapping("/api/products")
@Validated
public class ProductController {
    @Autowired
    private CRUDProductService crudProductService;

    String collectionDisplay = "Shizen"; // default collection display

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
     * 
     *         Examples:
     *         - GET /api/products - returns all products
     *         - GET /api/products?category=women-loungewear - returns products in
     *         women-loungewear category
     *         - GET /api/products?category=9c3b9b6a-2319-52ba-ac5a-68f9345d64fa -
     *         returns products by categoryId
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
     * 
     * sample output:
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
     * @param product The product to create.
     * @return The created product.
     */

    /*
     * Previous method by Yunhe
     * 
     * @PutMapping("/admin")
     * 
     * public ResponseEntity<Product>
     * updateProduct(@RequestParam("id") @NotBlank(message =
     * "Product Id cannot be empty") String id, @Valid @RequestBody Product product)
     * {
     * 
     * Product updatedProduct = crudProductService.updateProduct(id, product);
     * System.out.println("[ProductController] Updated product: " + updatedProduct);
     * return ResponseEntity.status(HttpStatus.OK).body(updatedProduct);
     * }
     * 
     * @DeleteMapping("/admin")
     * public ResponseEntity<Product>
     * deleteProduct(@RequestParam("id") @NotBlank(message =
     * "Product Id annot be empty") String productId) {
     * System.out.println("[ProductController] Deleting product with ID: " +
     * productId);
     * Product deletedProduct = crudProductService.deleteProduct(productId);
     * return ResponseEntity.status(HttpStatus.OK).body(deletedProduct);
     * }
     */

    /**
     * REFACTORED: Now uses @PathVariable for a more standard RESTful URL.
     */
    @PutMapping("/admin/{id}")
    public ResponseEntity<Product> updateProduct(@PathVariable String id, @RequestBody Product product) {
        // This method is kept simple as per your request.
        Product updatedProduct = crudProductService.updateProduct(id, product);
        return ResponseEntity.ok(updatedProduct);
    }

    /**
     * REFACTORED: Now uses @PathVariable and handles all errors from the service
     * by returning specific HTTP status codes.
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

}
