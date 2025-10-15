package sg.com.aori.interfaces;

import sg.com.aori.service.FinanceService;

/**
 * @author Jiang
 * @date 2025-10-10
 * @version 1.0
 * @version 1.1 - Modified to cooperate with FinanceService
 */

public interface IFinance {
    Boolean verifyPayment(String orderId);

    Boolean getPaymentStatus(String orderId);

    Boolean isPaymentGatewayAvailable();

    FinanceService.PaymentStatusDetail getPaymentStatusDetail(String orderId);
}