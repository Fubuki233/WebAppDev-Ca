/**
 * @author Jiang
 * @date 2025-10-07
 * @version 1.0
 */

package sg.com.aori.interfaces;

import sg.com.aori.model.ShoppingCart;
import java.math.BigDecimal;
import java.util.List;

public interface ICart {
    List<ShoppingCart> findCartByCustomerId(String customerId);
    BigDecimal calculateTotal(List<ShoppingCart> cartItems);
    boolean checkInventory(String customerId);
    String createOrder(String customerId);
    void addToCart(String customerId, String variantId, Integer quantity);
    void removeFromCart(String cartId);
    boolean verifyInCart(String customerId, String variantId);
}