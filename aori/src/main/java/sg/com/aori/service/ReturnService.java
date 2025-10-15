/**
 * Service for Return entity.
 *
 * @author xiaobo
 * @date 2025-10-09
 * @version 1.0
 */

/*
 * * @author Simon Lei
 * * @date 2025-10-11
 * * @version 2.0
 * Update on the Returns entity to add some validations.
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
import org.springframework.validation.annotation.Validated;
import jakarta.validation.Valid;

@Service
@Validated
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
    public String processReturnRequest(@Valid Returns returns, String userId) {

        OrderItem itemToReturn = orderRepository.findOrderItemByItemIdAndUserId(
                returns.getOrderItemId(),
                userId)
                .orElseThrow(() -> new IllegalArgumentException("Order item not found or unauthorized access."));

        Orders order = itemToReturn.getOrder();

        if (order.getCreatedAt().isBefore(java.time.LocalDateTime.now().minusDays(30))) {

            returns.setReturnStatus(Returns.ReturnStatus.Denied);
            returnRepository.save(returns);
            return "Return ineligible: Past 30-day window.";
        }

        returns.setReturnStatus(Returns.ReturnStatus.Requested);

        returnRepository.save(returns);
        order.setOrderStatus(Orders.OrderStatus.Returned);
        orderRepository.save(order);

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