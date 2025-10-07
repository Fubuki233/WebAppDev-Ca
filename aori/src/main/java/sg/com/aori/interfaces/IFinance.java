/**
 * @author Jiang
 * @date 2025-10-07
 * @version 1.0
 */

package sg.com.aori.interfaces;

public interface IFinance {
    Boolean verifyPayment(String orderId);
    boolean processRefund(String orderId, Double amount);
    Boolean getPaymentStatus(String orderId);
    boolean isPaymentGatewayAvailable();
}