/**
 * Service for Return entity.
 *
 * @author xiaobo
 * @date 2025-10-09
 * @version 1.0
 */

package sg.com.aori.service;

import sg.com.aori.model.Orders;
import sg.com.aori.model.Returns;
import sg.com.aori.interfaces.IReturn;
import sg.com.aori.model.OrderItem;
import sg.com.aori.repository.OrderRepository;
import sg.com.aori.repository.ReturnRepository;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ReturnService implements IReturn {

    private final OrderRepository orderRepository;
    private final ReturnRepository returnRepository;

    public ReturnService(
            OrderRepository orderRepository,
            ReturnRepository returnRepository) {
        this.orderRepository = orderRepository;
        this.returnRepository = returnRepository;
    }

    /**
     * Processes a return request using the Returns entity as the input object.
     * The Returns entity must contain a valid orderItemId and reason.
     */
    @Transactional
    public String processReturnRequest(Returns returns, String userId) {

        // --- 1. Find OrderItem and Check Eligibility ---

        // NOTE: You need a method in OrderRepository to find the OrderItem by its ID
        // AND the User ID
        OrderItem itemToReturn = orderRepository.findOrderItemByItemIdAndUserId(
                returns.getOrderItemId(), // Uses the field from the Returns entity
                userId)
                .orElseThrow(() -> new IllegalArgumentException("Order item not found or unauthorized access."));

        Orders order = itemToReturn.getOrder();

        // Example Eligibility Check (30-day policy)
        if (order.getCreatedAt().isBefore(java.time.LocalDateTime.now().minusDays(30))) {

            // Set final rejected status and save for tracking
            returns.setReturnStatus(Returns.ReturnStatus.Denied); // Use Returns's enum
            returnRepository.save(returns);
            return "Return ineligible: Past 30-day window.";
        }

        // --- 2. Finalize Entity Data and Save ---

        // Set status to the initial state (Pervious: Requested)
        returns.setReturnStatus(Returns.ReturnStatus.Requested);

        // Step 8: Creates the record in the database
        returnRepository.save(returns); // 👈 Correctly saves the Returns entity

        // --- 3. Update Order Status ---
        order.setOrderStatus(Orders.OrderStatus.Returned);
        orderRepository.save(order);

        // --- 4. Return Confirmation ---
        return "Return Confirmed. Instructions and Refund Processed.";
    }

    @Override
    public Optional<Returns> findReturnById(String returnId) {
        return returnRepository.findById(returnId);
    }

    @Override
    public boolean checkEligibility(String orderId, String productId) {
        throw new UnsupportedOperationException("Unimplemented method 'checkEligibility'");
    }

    @Override
    public String getReturnInstructions(String returnId) {
        throw new UnsupportedOperationException("Unimplemented method 'getReturnInstructions'");
    }
}