package sg.com.aori.interfaces;

import sg.com.aori.model.Returns;

import java.util.Optional;

/**
 * Interface for Return entity.
 *
 * @author Xiaobo
 * @date 2025-10-09
 * @version 1.0
 */

public interface IReturn {

    /**
     * Processes a new return request, handling eligibility, persistence, and refund
     * initiation.
     * This is the core method corresponding to the sequence diagram's main flow.
     *
     * @param returns    The data transfer object/entity containing return details.
     * @param customerId The ID of the authenticated user submitting the request.
     * 
     * @return A confirmation message or instructions.
     * @throws IllegalArgumentException if the order/product is invalid or
     *                                  unauthorized.
     * @throws RuntimeException         if the external refund fails.
     */
    String processReturnRequest(Returns returns, String customerId);

    /**
     * 
     * 
     * Finds a pendin return request by its ID.
     * 
     * @param returnId The ID of the return request.
     * @return The ReturnRequest entity, if found.
     */
    Optional<Returns> findReturnById(String returnId);

    /**
     * Internal method to check if an item qualifies for a return based on bus
     * ness rules (e.g., date, status).
     * 
     * @param orderId   The ID of the order.
     * @param productId The ID of the product.
     * @return True if eligible, false otherwise.
     */
    boolean checkEligibility(String orderId, String productId);

    /**
     * Generates or retrieves instructions for the customer on how to ship the
     * product back.
     * 
     * @param returnId The ID of the approved return request.
     * @return Instructions string (e.g., address, return label link).
     */
    String getReturnInstructions(String returnId);
}