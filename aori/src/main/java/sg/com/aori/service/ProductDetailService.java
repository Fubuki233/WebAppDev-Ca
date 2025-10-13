package sg.com.aori.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.NoSuchElementException;
import sg.com.aori.model.Category;
import sg.com.aori.model.Product;
import sg.com.aori.repository.CategoryRepository;
import sg.com.aori.repository.ProductRepository;

/**
 * Service for fetching detailed product information along with review
 * statistics.
 *
 * @author Simon Lei
 * @date 2025-10-08
 * @version 1.0
 */
@Service
@RequiredArgsConstructor
public class ProductDetailService {

    private final ProductRepository productRepo;
    private final CategoryRepository categoryRepository;

    public ProductDetailVM getDetail(String productId) {
        Product p = productRepo.findById(productId)
                .orElseThrow(() -> new NoSuchElementException("Product not found"));

        ProductDetailVM vm = new ProductDetailVM();
        vm.setId(p.getProductId());
        vm.setProductCode(p.getProductCode());
        vm.setName(p.getProductName());
        vm.setDescription(p.getDescription());
        vm.setImage(p.getImage());
        vm.setPrice(p.getPrice());
        vm.setStockQuantity(p.getStockQuantity());

        vm.setCollection(p.getCollection());
        vm.setMaterial(p.getMaterial());
        vm.setSeason(p.getSeason() == null ? null : p.getSeason().name());
        vm.setCareInstructions(p.getCareInstructions());

        if (p.getCategoryId() != null) {
            Category c = categoryRepository.findById(p.getCategoryId()).orElse(null);
            if (c != null) {
                vm.setCategoryId(c.getCategoryId());
                vm.setCategoryName(c.getCategoryName());
            }
        }
        return vm;
    }
}
