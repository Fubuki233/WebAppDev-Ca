package sg.com.aori.controller;

import sg.com.aori.model.Product;
import sg.com.aori.service.CRUDProductService;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Controller class for handling product-related requests.
 * Now, all the methods had been tested
 * 
 * @author Yunhe
 * @date 2025-10-07
 * @version 1.1
 */
@RestController
public class ProductController {
    @Autowired
    private CRUDProductService crudProductService;

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

    @PostMapping("/products")
    public ResponseEntity<Product> createProduct(@RequestBody Product product) {
        if (product.getProductId() == null || product.getProductId().isEmpty()) {
            product.setProductId(java.util.UUID.randomUUID().toString());
        }

        Product createdProduct = crudProductService.createProduct(product);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdProduct);
    }

    @GetMapping("products")
    public Optional<List<Product>> getAllProducts() {
        return crudProductService.getAllProducts();
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
    @PutMapping("/products/{id}")

    public ResponseEntity<Product> updateProduct(@PathVariable("id") String id, @RequestBody Product product) {

        Product updatedProduct = crudProductService.updateProduct(id, product);
        return ResponseEntity.status(HttpStatus.OK).body(updatedProduct);
    }

    @DeleteMapping("/products/{productId}")
    public ResponseEntity<Product> deleteProduct(@PathVariable String productId) {
        Product deletedProduct = crudProductService.deleteProduct(productId);
        return ResponseEntity.status(HttpStatus.OK).body(deletedProduct);
    }

}
