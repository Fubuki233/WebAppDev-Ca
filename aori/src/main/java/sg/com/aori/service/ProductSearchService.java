package sg.com.aori.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import jakarta.persistence.criteria.Predicate;
import java.math.BigDecimal;
import java.util.*;

import sg.com.aori.model.Category;
import sg.com.aori.model.Product;
import sg.com.aori.repository.CategoryRepository;
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

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    public Page<ProductSummaryVM> search(
            String q,
            String categoryKey,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            Boolean inStock,
            Pageable pageable) {

        Set<String> categoryIds = new HashSet<>();
        if (StringUtils.hasText(categoryKey)) {
            Category byName = categoryRepository.findByCategoryName(categoryKey);
            if (byName != null) categoryIds.add(byName.getCategoryId());
            Category byCode = categoryRepository.findByCategoryCode(categoryKey);
            if (byCode != null) categoryIds.add(byCode.getCategoryId());
            Category bySlug = categoryRepository.findBySlug(categoryKey);
            if (bySlug != null) categoryIds.add(bySlug.getCategoryId());
        }

        Specification<Product> spec = (root, query, cb) -> {
            List<Predicate> and = new ArrayList<>();

            if (StringUtils.hasText(q)) {
                and.add(cb.like(cb.lower(root.get("productName")), "%" + q.toLowerCase() + "%"));
            }
            if (!categoryIds.isEmpty()) {
                and.add(root.get("categoryId").in(categoryIds));
            }
            if (minPrice != null) and.add(cb.ge(root.get("price"), minPrice));
            if (maxPrice != null) and.add(cb.le(root.get("price"), maxPrice));
            if (Boolean.TRUE.equals(inStock)) {
                and.add(cb.greaterThan(root.get("stockQuantity"), 0));
            }
            return and.isEmpty() ? cb.conjunction() : cb.and(and.toArray(new Predicate[0]));
        };

        return productRepository.findAll(spec, pageable).map(this::toSummaryVM);
    }

    private ProductSummaryVM toSummaryVM(Product p) {
        ProductSummaryVM vm = new ProductSummaryVM();
        vm.setId(p.getProductId());
        vm.setProductCode(p.getProductCode());
        vm.setName(p.getProductName());
        vm.setImage(p.getImage());
        vm.setPrice(p.getPrice());
        vm.setStockQuantity(p.getStockQuantity());
        if (p.getCategoryId() != null) {
            categoryRepository.findById(p.getCategoryId())
                    .ifPresent(c -> vm.setCategoryName(c.getCategoryName()));
        }
        return vm;
    }
}
