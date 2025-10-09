package sg.com.aori.controller;

import java.math.BigDecimal;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;
import sg.com.aori.service.ProductDetailService;
import sg.com.aori.service.ProductDetailVM;
import sg.com.aori.service.ProductSearchService;
import sg.com.aori.service.ProductSummaryVM;

/**
 * Controller for product search and detail endpoints.
 *
 * @author Simon Lei
 * @date 2025-10-08
 * @version 1.0
 */

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductSearchController {

    private final ProductSearchService productSearchService;
    private final ProductDetailService productDetailService;

    @GetMapping("/search")
    public Page<ProductSummaryVM> search(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) Boolean inStock,
            @PageableDefault(size = 12, sort = "createdAt") Pageable pageable) {
        return productSearchService.search(q, category, minPrice, maxPrice, inStock, pageable);
    }

    // has been implemented in ProductController
    // @GetMapping("/{productId}")
    // public ProductDetailVM getDetail(@PathVariable String productId) {
    // return productDetailService.getDetail(productId);
    // }
}
