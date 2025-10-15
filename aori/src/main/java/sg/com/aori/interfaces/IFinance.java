package sg.com.aori.interfaces;

/**
 * @author Yibai
 * @date 2025-10-10
 * @version 1.0
 * @version 1.1 - Modified to cooperate with FinanceService
 */

public interface IFinance {
    Boolean verifyPayment(String orderId);
}