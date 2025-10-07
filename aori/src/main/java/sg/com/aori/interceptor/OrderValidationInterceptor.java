/**
 * @author Jiang
 * @date 2025-10-07
 * @version 1.0
 */

package sg.com.aori.interceptor;

import sg.com.aori.model.Orders;
import sg.com.aori.service.OrderService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class OrderValidationInterceptor implements HandlerInterceptor {

    @Autowired
    private OrderService orderService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) 
            throws Exception {
        
        String requestURI = request.getRequestURI();
        
        // Only intercept order detail pages
        if (requestURI.startsWith("/order/") && !requestURI.equals("/order/pay") && !requestURI.equals("/order/cancel")) {
            String[] pathParts = requestURI.split("/");
            if (pathParts.length >= 3) {
                String orderId = pathParts[2];
                
                // Validate order exists and is accessible
                Orders order = orderService.findOrderById(orderId);
                if (order == null) {
                    response.sendRedirect("/cart?error=Order not found");
                    return false;
                }
                
                // For demo, using fixed customer ID - in real app, check if current user owns this order
                // ***** Must be modified
                String customerId = "demo-customer-id";
                if (!order.getCustomerId().equals(customerId)) {
                    response.sendRedirect("/cart?error=Access denied");
                    return false;
                }
                
                // Check if order can be modified (only pending orders)
                if (request.getMethod().equals("POST") && 
                    !order.getOrderStatus().equals(Orders.OrderStatus.Pending)) {
                    response.sendRedirect("/order/" + orderId + "?error=Order cannot be modified");
                    return false;
                }
            }
        }
        
        return true;
    }
}