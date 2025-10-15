package sg.com.aori.interfaces;

/**
 * Interface for sku
 *
 * @author Yunhe
 * @date 2025-10-14
 * @version 1.0
 */

public interface ISku {
    String createSku(String sku, int quantity);

    int getQuantity(String sku);

    int checkoutSku(String sku);

    int decreaseQuantity(String sku, int quantity);
}
