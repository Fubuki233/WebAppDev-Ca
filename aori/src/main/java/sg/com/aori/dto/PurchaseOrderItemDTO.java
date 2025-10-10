package sg.com.aori.dto;

import java.math.BigDecimal;

import sg.com.aori.model.OrderItem;
import sg.com.aori.model.Product;
/**
 * purchasehistory
 *
 * @author Jiayi
 * @date 2025-10-08
 * @version 1.0
 */
/**
 * 订单行 DTO（含商品详情）
 * - 仅暴露购买历史所需的必要字段（避免整个实体树外泄）
 */
public class PurchaseOrderItemDTO {

    private String orderItemId;
    private String productId;
    private String productName;       // 商品名称（来自 Product）
    private String productImage;      // 商品主图（来自 Product）
    private Integer quantity;
    private BigDecimal priceAtPurchase;   // 下单时单价
    private BigDecimal discountApplied;   // 行级折扣
    private boolean refunded;             // 此行是否已退款/换货

    public static PurchaseOrderItemDTO from(OrderItem oi, boolean refunded) {
        PurchaseOrderItemDTO d = new PurchaseOrderItemDTO();
        d.orderItemId = oi.getOrderItemId();
        d.productId = oi.getProductId();
        d.quantity = oi.getQuantity();
        d.priceAtPurchase = oi.getPriceAtPurchase();
        d.discountApplied = oi.getDiscountApplied();

        Product p = oi.getProduct();
        if (p != null) {
            d.productName = p.getProductName();
            d.productImage = p.getImage();
        }
        d.refunded = refunded;
        return d;
    }

    // getter/setter
    public String getOrderItemId() { return orderItemId; }
    public String getProductId() { return productId; }
    public String getProductName() { return productName; }
    public String getProductImage() { return productImage; }
    public Integer getQuantity() { return quantity; }
    public BigDecimal getPriceAtPurchase() { return priceAtPurchase; }
    public BigDecimal getDiscountApplied() { return discountApplied; }
    public boolean isRefunded() { return refunded; }

    public void setOrderItemId(String orderItemId) { this.orderItemId = orderItemId; }
    public void setProductId(String productId) { this.productId = productId; }
    public void setProductName(String productName) { this.productName = productName; }
    public void setProductImage(String productImage) { this.productImage = productImage; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
    public void setPriceAtPurchase(BigDecimal priceAtPurchase) { this.priceAtPurchase = priceAtPurchase; }
    public void setDiscountApplied(BigDecimal discountApplied) { this.discountApplied = discountApplied; }
    public void setRefunded(boolean refunded) { this.refunded = refunded; }
}
