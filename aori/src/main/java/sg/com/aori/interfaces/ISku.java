package sg.com.aori.interfaces;

public interface ISku {
    String createSku(String sku, int quantity);

    int getQuantity(String sku);

    int checkoutSku(String sku);

    int decreaseQuantity(String sku, int quantity);
}
