package sg.com.aori.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import sg.com.aori.service.CRUDProductService;
import sg.com.aori.service.SkuService;
import sg.com.aori.utils.SkuTool;

/**
 * Controller class for handling SKU-related requests.
 * 
 * @author Yunhe
 * @date 2025-10-13
 * @version 1.0
 * 
 * @author Ying Chun
 * @date 2025-10-15
 * @version 1.1 - SKU operations now automatically update product stock quantity
 *          by calculating the sum of all SKU quantities for that product
 */

@RestController
@RequestMapping("/api/product")

public class SkuController {
    @Autowired
    SkuService skuService;

    @Autowired
    CRUDProductService productService;

    /**
     * Create or update a SKU with the specified quantity.
     * Automatically updates the product's total stock quantity after operation.
     * 
     * @param id       product UUID
     * @param colour   hex color code (without #)
     * @param size     product size
     * @param quantity quantity for this specific SKU
     * @return the created/updated SKU string
     */
    @PostMapping("admin/sku")
    public String setSku(@RequestParam String id, @RequestParam String colour, @RequestParam String size,
            @RequestParam int quantity) {
        String sku = SkuTool.createSku(id, colour, size, productService);
        System.out.println("[SkuController] Setting SKU: " + sku + " with quantity: " + quantity);

        return skuService.createSku(sku, quantity);
    }

    @GetMapping("sku")
    public int getSku(@RequestParam String id, @RequestParam String colour, @RequestParam String size) {
        if (productService.getProductById(id) == null) {
            return -1;
        }
        String sku = SkuTool.createSku(id, colour, size, productService);
        System.out.println("[SkuController] Getting quantity for SKU: " + sku);
        return skuService.getQuantity(sku);
    }

    /**
     * Checkout (decrease by 1) the quantity of a specific SKU.
     * Automatically updates the product's total stock quantity after operation.
     * 
     * @param id     product UUID
     * @param colour hex color code (without #)
     * @param size   product size
     * @return the remaining quantity of the SKU, or -1 if SKU not found or out of
     *         stock
     */
    @PostMapping("sku/checkout")
    public int checkoutSku(@RequestParam String id, @RequestParam String colour, @RequestParam String size) {
        if (productService.getProductById(id) == null) {
            return -1;
        }
        String sku = SkuTool.createSku(id, colour, size, productService);
        System.out.println("[SkuController] Checking out SKU: " + sku);

        return skuService.checkoutSku(sku);
    }

}
