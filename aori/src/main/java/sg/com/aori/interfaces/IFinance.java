package sg.com.aori.interfaces;

/**
 * @author Jiang
 * @date 2025-10-10
 * @version 1.0
 * @version 1.1 - Modified to cooperate with FinanceService
 */

public interface IFinance {
    Boolean verifyPayment(String orderId);
}