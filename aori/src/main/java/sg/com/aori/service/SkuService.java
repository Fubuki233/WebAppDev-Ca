package sg.com.aori.service;

/**
 * SKU Service for managing stock keeping units (SKUs).
 * @author Yunehe
 * @date 2025-10-13
 * @version 1.0
 * 
 * @author Jiang
 * @date 2025-10-13
 * @version 1.1 - Debug
 */
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sg.com.aori.interfaces.ISku;
import sg.com.aori.model.Sku;
import sg.com.aori.repository.SkuRepository;
import sg.com.aori.utils.SkuTool;

@Service
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
        return sku;
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
        return existingSku.getQuantity();
    }

}
