package sg.com.aori.controller;

import jakarta.validation.constraints.Size;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import sg.com.aori.model.Product;
import sg.com.aori.service.ProductSearchService;

import java.util.List;


/**
 * Controller for product search and detail endpoints.
 *
 * @author Simon Lei
 * @date 2025-10-08
 * @version 1.0
 */

@RestController
@RequestMapping("/api/products")
@Validated
public class ProductSearchController {

    private final ProductSearchService productSearchService;

    public ProductSearchController(ProductSearchService productSearchService) {
        this.productSearchService = productSearchService;
    }

    @GetMapping("/search")
    public List<Product> search(
            @RequestParam(name = "query", required = false)
            @Size(max = 120, message = "query too long") String query
    ) {
        return productSearchService.search(query);
    }
}