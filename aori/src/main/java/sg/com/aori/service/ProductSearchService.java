package sg.com.aori.service;

import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import sg.com.aori.model.Product;
import sg.com.aori.repository.ProductRepository;

/**
 * Service for searching products with pagination and sorting.
 *
 * @author Simon Lei
 * @date 2025-10-08
 * @version 1.0
 */
@Service
@RequiredArgsConstructor
public class ProductSearchService {

    private final ProductRepository productRepo;

    public Page<Product> search(String keyword,
                                List<String> categoryIds,
                                int page, int size, String sort) {

        Pageable pageable = PageRequest.of(
                Math.max(page, 0),
                Math.min(Math.max(size, 1), 50),
                ("productName,asc".equalsIgnoreCase(sort)
                    ? Sort.by("productName").ascending()
                    : Sort.by("updatedAt").descending())
        );

        String kw = (keyword == null || keyword.isBlank()) ? "" : keyword.trim();

        if (categoryIds == null || categoryIds.isEmpty()) {
            return productRepo.findByProductNameContainingIgnoreCase(kw, pageable);
        } else {
            return productRepo.findByProductNameContainingIgnoreCaseAndCategoryIdIn(kw, categoryIds, pageable);
        }
    }
}
