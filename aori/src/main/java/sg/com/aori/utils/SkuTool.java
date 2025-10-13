package sg.com.aori.utils;

import java.util.Optional;

import sg.com.aori.service.CRUDProductService;
import sg.com.aori.model.Product;

public class SkuTool {

    public static String createSku(String uuid, String colour, String size,
            CRUDProductService productService) {
        Optional<Product> product = productService.getProductById(uuid);
        String sku = product.get().getProductName() + "-" + colour + "-" + size;
        return sku;
    }

    public static Product getProductBySku(String sku, CRUDProductService productService) {
        String[] parts = sku.split("-");
        if (parts.length < 3) {
            return null;
        }
        String productId = parts[0];
        Optional<Product> product = productService.getProductById(productId);
        return product.orElse(null);
    }

    public static String SkuDecode(String sku) {
        String[] parts = sku.split("-");
        if (parts.length < 3) {
            return null;
        }
        return parts[0];
    }

}
