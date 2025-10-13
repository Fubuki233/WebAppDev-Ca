/**
 * v1.1: Cooperate with FinanceService
 * @author Jiang
 * @date 2025-10-10
 * @version 1.1
 */

package sg.com.aori.interfaces;

import sg.com.aori.service.FinanceService;

public interface IFinance {
    Boolean verifyPayment(String orderId);
    // boolean processRefund(String orderId, Double amount);
    Boolean getPaymentStatus(String orderId);
    Boolean isPaymentGatewayAvailable();
    FinanceService.PaymentStatusDetail getPaymentStatusDetail(String orderId);
}