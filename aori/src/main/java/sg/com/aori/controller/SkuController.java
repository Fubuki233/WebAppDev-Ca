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
 */

@RestController
@RequestMapping("/api/product")

public class SkuController {
    @Autowired
    SkuService skuService;

    @Autowired
    CRUDProductService productService;

    @PostMapping("admin/sku")
    public String setSku(@RequestParam String id, @RequestParam String colour, @RequestParam String size,
            @RequestParam int quantity) {
        String sku = SkuTool.createSku(id, colour, size, productService);

        return skuService.createSku(sku, quantity);
    }

    @GetMapping("sku")
    public int getSku(@RequestParam String id, @RequestParam String colour, @RequestParam String size) {
        if (productService.getProductById(id) == null) {
            return -1;
        }
        String sku = SkuTool.createSku(id, colour, size, productService);
        System.out.println("Getting quantity for SKU: " + sku);
        return skuService.getQuantity(sku);
    }

    @PostMapping("sku/checkout")
    public int checkoutSku(@RequestParam String id, @RequestParam String colour, @RequestParam String size) {
        if (productService.getProductById(id) == null) {
            return -1;
        }
        String sku = SkuTool.createSku(id, colour, size, productService);

        return skuService.checkoutSku(sku);
    }

}
