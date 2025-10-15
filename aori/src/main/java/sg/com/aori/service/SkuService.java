package sg.com.aori.service;

/**
 * SKU Service for managing stock keeping units (SKUs).
 * @author Yunhe
 * @date 2025-10-13
 * @version 1.0
 * 
 * @author Jiang
 * @date 2025-10-13
 * @version 1.1 - Debug
 * 
 * @author Yunhe
 * @date 2025-10-15
 * @version 1.2 - Added automatic product stock quantity calculation after SKU operations
 */
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import sg.com.aori.interfaces.ISku;
import sg.com.aori.model.Product;
import sg.com.aori.model.Sku;
import sg.com.aori.repository.SkuRepository;
import sg.com.aori.utils.SkuTool;

@Service
@Transactional
public class SkuService implements ISku {
    @Autowired
    private SkuRepository skuRepository;

    @Autowired
    private CRUDProductService productService;

    @Override
    public String createSku(String sku, int quantity) {
        sku = SkuTool.convertUUIDSkutoProductCodeSku(sku, productService);

        Sku newSku = new Sku();
        newSku.setSku(sku);
        newSku.setQuantity(quantity);
        skuRepository.save(newSku);
        System.out.println("Created SKU: " + sku + " with quantity: " + quantity);

        // Update product stock quantity after creating SKU
        updateProductStockQuantity(sku);

        return sku;
    }

    /**
     * Update the product's total stock quantity by summing all its SKU quantities
     * 
     * @param sku the SKU string (format: PRODUCTCODE&COLOR&SIZE)
     */
    private void updateProductStockQuantity(String sku) {
        try {
            // Extract product code from SKU
            String[] parts = sku.split("&");
            if (parts.length < 3) {
                System.out.println("[SkuService] Invalid SKU format, cannot update product stock: " + sku);
                return;
            }

            String productCode = parts[0];

            // Get product ID from product code
            String productId = productService.findProductIdByProductCode(productCode);
            if (productId == null) {
                System.out.println("[SkuService] Product not found for code: " + productCode);
                return;
            }

            // Calculate total quantity of all SKUs for this product
            Integer totalQuantity = skuRepository.getTotalQuantityByProductCode(productCode);

            // Update product stock quantity
            Product product = productService.getProductById(productId).orElse(null);
            if (product != null) {
                product.setStockQuantity(totalQuantity);
                productService.saveProduct(product);
                System.out.println(
                        "[SkuService] Updated product " + productCode + " stock quantity to: " + totalQuantity);
            }
        } catch (Exception e) {
            System.err.println("[SkuService] Error updating product stock quantity: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public int getQuantity(String sku) {
        sku = SkuTool.convertUUIDSkutoProductCodeSku(sku, productService);

        Sku existingSku = skuRepository.findById(sku).orElse(null);
        if (existingSku == null) {
            System.out.println("[SkuService] SKU not found: " + sku + ", returning quantity 0");
            createSku(sku, 0);
            return 0;

        }
        return existingSku.getQuantity();
    }

    @Override
    public int checkoutSku(String sku) {
        sku = SkuTool.convertUUIDSkutoProductCodeSku(sku, productService);
        Sku existingSku = skuRepository.findById(sku).orElse(null);
        if (existingSku == null || existingSku.getQuantity() <= 0) {
            return -1;
        }
        existingSku.setQuantity(existingSku.getQuantity() - 1);
        skuRepository.save(existingSku);

        // Update product stock quantity after checkout
        updateProductStockQuantity(sku);

        return existingSku.getQuantity();
    }

}
