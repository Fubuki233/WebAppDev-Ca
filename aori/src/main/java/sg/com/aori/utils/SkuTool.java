/**
 * v1.0(Yunhe)
 * v1.1(Jiang): Added method getProductIdBySku
 * 
 * @author Yunhe
 * @date 10-13
 * @version 1.1
 */

package sg.com.aori.utils;

import java.util.Optional;

import org.json.JSONObject;
import sg.com.aori.service.CRUDProductService;
import sg.com.aori.model.Product;

public class SkuTool {

    public static String createSku(String uuid, String colour, String size,
            CRUDProductService productService) {
        Optional<Product> product = productService.getProductById(uuid);
        String sku = product.get().getProductCode() + "&" + colour + "&" + size;
        return sku;
    }

    public static Product getProductBySku(String sku, CRUDProductService productService) {
        String[] parts = sku.split("&");
        if (parts.length < 3) {
            return null;
        }
        String productId = parts[0];
        Optional<Product> product = productService.getProductById(productId);
        return product.orElse(null);
    }

    public static String SkuDecode(String sku, CRUDProductService productService) {
        String[] parts = sku.split("&");
        if (parts.length < 3) {
            return null;
        }
        String productId = productService.findProductIdByProductCode(parts[0]);
        JSONObject json = new JSONObject();
        json.put("productId", productId);
        json.put("colour", parts[1]);
        json.put("size", parts[2]);
        return json.toString();
    }

    public static String getProductIdBySku(String sku, CRUDProductService productService) {
        String[] parts = sku.split("&");
        if (parts.length < 3) {
            return null;
        }
        // parts[0] could be either productCode or productId (UUID)
        // First try as productCode
        String productId = productService.findProductIdByProductCode(parts[0]);

        // If not found and parts[0] looks like a UUID, use it directly as productId
        if (productId == null
                && parts[0].matches("[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}")) {
            productId = parts[0];
        }

        return productId;
    }
}
