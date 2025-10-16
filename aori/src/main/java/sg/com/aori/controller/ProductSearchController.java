package sg.com.aori.controller;

import java.math.BigDecimal;
import java.util.List;
import jakarta.validation.constraints.Size;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import sg.com.aori.model.Product;
import sg.com.aori.service.ProductSearchService;

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
            @RequestParam(name = "q", required = false) @Size(max = 120) String q,
            @RequestParam(name = "category", required = false) String category,
            @RequestParam(name = "color", required = false) String color,
            @RequestParam(name = "size", required = false) String size,
            @RequestParam(name = "priceMin", required = false) BigDecimal priceMin,
            @RequestParam(name = "priceMax", required = false) BigDecimal priceMax) {
        return productSearchService.search(q, category, color, size, priceMin, priceMax);
    }
}