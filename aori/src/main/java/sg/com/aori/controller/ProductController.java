package sg.com.aori.controller;

import sg.com.aori.model.Product;
import sg.com.aori.service.CreateProductService;
import sg.com.aori.service.UpdateProductService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ProductController {
    @Autowired
    private CreateProductService createProductService;

    @Autowired
    private UpdateProductService updatedProductService;

    @PostMapping("/products")
    public ResponseEntity<Product> createProduct(@RequestBody Product product) {

        Product createdProduct = createProductService.createProduct(product);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdProduct);
    }

    @PostMapping("/products")
    public String postMethodName(@PathVariable("id") String id, @RequestBody String entity) {

        Product temp =
        Product product = updatedProductService.updateProduct(id, );
        return entity;
    }

    @DeleteMapping("/products/{productId}")
    public ResponseEntity<Void> deleteProduct(@PathVariable String productId) {
        deleteProductService.deleteProduct(productId);
        return ResponseEntity.noContent().build();
    }

}
