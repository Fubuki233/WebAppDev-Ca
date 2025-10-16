package sg.com.aori.utils;

import java.util.Optional;

import org.json.JSONObject;

import sg.com.aori.service.CRUDProductService;
import sg.com.aori.model.Product;

/**
 * @author Yunhe
 * @date 10-13
 * @version 1.0
 * 
 * @author Yibai
 * @date 10-13
 * @version 1.1 - Added method getProductIdBySku
 */

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
        String productId = "";
        if (parts[0].length() == 36) {
            productId = parts[0];

        } else {
            productId = productService.findProductIdByProductCode(parts[0]);
        }
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

        String productId = "";
        if (parts[0].length() == 36) {
            productId = parts[0];
        } else {
            productId = productService.findProductIdByProductCode(parts[0]);
        }

        return productId;
    }

    public static String convertUUIDSkutoProductCodeSku(String sku, CRUDProductService productService) {
        String[] parts = sku.split("&");
        if (parts.length < 3) {
            return null;
        }
        String productId;
        if (parts[0].length() == 36) {
            System.out.println("[SkuTool] Converting UUID SKU to ProductCode SKU: " + sku);
            productId = parts[0];
            String productCode = productService.getProductById(productId).get().getProductCode();
            return productCode + "&" + parts[1] + "&" + parts[2];
        }
        return sku;
    }

}
