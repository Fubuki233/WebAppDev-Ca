package sg.com.aori.controller;

import sg.com.aori.model.Product;
import sg.com.aori.service.CreateProductService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ProductController {
    @Autowired
    private CreateProductService createProductService;

    @PostMapping("/products")
    public ResponseEntity<Product> createProduct(@RequestBody Product product) {

        Product createdProduct = createProductService.createProduct(product);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdProduct);
    }

}
