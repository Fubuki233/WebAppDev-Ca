package sg.com.aori.interfaces;

import sg.com.aori.model.ShoppingCart;
import java.math.BigDecimal;
import java.util.List;

/**
 * @author Jiang
 * @date 2025-10-13
 * @version 1.0
 * @version 1.1 - Changed variant into product
 * @version 1.2 - Changed to cooperate with CartService
 */

public interface ICart {
    List<ShoppingCart> findCartByCustomerId(String customerId);

    BigDecimal calculateTotal(List<ShoppingCart> cartItems);

    boolean checkInventory(String customerId);

    String createOrder(String customerId);

    void addToCart(String customerId, String productId, Integer quantity, String sku);

    void removeFromCart(String cartId);

    boolean verifyInCart(String customerId, String productId);
}