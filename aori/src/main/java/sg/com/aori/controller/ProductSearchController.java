package sg.com.aori.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import sg.com.aori.model.Product;
import sg.com.aori.service.ProductSearchService;
import sg.com.aori.service.ProductDetailService;
import sg.com.aori.service.ProductDetailVM;

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
@RequiredArgsConstructor
public class ProductSearchController {

    private final ProductSearchService searchService;
    private final ProductDetailService detailService;

    @GetMapping("/search")
    public Page<Product> search(@RequestParam(required = false) String keyword,
                                @RequestParam(required = false, name = "category") List<String> categoryIds,
                                @RequestParam(defaultValue = "0") int page,
                                @RequestParam(defaultValue = "12") int size,
                                @RequestParam(required = false) String sort) {
        return searchService.search(keyword, categoryIds, page, size, sort);
    }

    @GetMapping("/{id}")
    public ProductDetailVM detail(@PathVariable String id) {
        return detailService.getDetail(id);
    }
}
