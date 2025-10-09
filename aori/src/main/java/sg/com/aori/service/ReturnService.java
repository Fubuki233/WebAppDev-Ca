/**
 * Service for Return entity.
 *
 * @author xiaobo
 * @date 2025-10-09
 * @version 1.0
 */

package sg.com.aori.service;

import sg.com.aori.model.Orders;
import sg.com.aori.interfaces.IReturn;
import sg.com.aori.model.OrderItem;
import sg.com.aori.model.ReturnRequest;
import sg.com.aori.repository.OrderRepository;
import sg.com.aori.repository.ReturnRepository;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ReturnService implements IReturn {
    @Autowired
    private final OrderRepository orderRepository;
    @Autowired
    private final ReturnRepository returnRepository;

    public ReturnService(
            OrderRepository orderRepository,
            ReturnRepository returnRepository) {
        this.orderRepository = orderRepository;
        this.returnRepository = returnRepository;

    }

    // Now accepts the ReturnRequest entity directly
    @Transactional
    public String processReturnRequest(ReturnRequest requestEntity, String userId) {

        // --- Find OrderItem and Check Eligibility ---
        OrderItem itemToReturn = orderRepository.findOrderItemForReturn(
                requestEntity.getOrderId(),
                requestEntity.getProductId(),
                userId).orElseThrow(() -> new IllegalArgumentException("Order item not found or unauthorized access."));

        Orders order = itemToReturn.getOrder();

        // Example Eligibility Check (as per "Passed Eligibility Check (company
        // policy)")
        if (order.getCreatedAt().isBefore(java.time.LocalDateTime.now().minusDays(30))) {
            return "Return ineligible: Past 30-day window.";
        }

        // --- Prepare the ReturnRequest Entity for Persistence ---

        // Manually set properties that were not in the incoming JSON
        requestEntity.setCustomerId(userId);
        requestEntity.setRefundAmount(itemToReturn.getPrice()); // Example: refund the item price
        requestEntity.setReturnStatus(ReturnRequest.ReturnStatus.PENDING_APPROVAL);

        // @PrePersist in the ReturnRequest entity to set ID and
        // timestamps.

        returnRepository.save(requestEntity); // Step 8: Creates the record

        // --- Update Order Status ---
        order.setOrderStatus(Orders.OrderStatus.Returned);
        orderRepository.save(order);

        // --- Return Confirmation ---
        return "Return Confirmed. Instructions and Refund Processed.";
    }
}