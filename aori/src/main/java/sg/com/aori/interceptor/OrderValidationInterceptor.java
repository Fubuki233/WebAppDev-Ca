/**
 * v1.1: REST API applied
 * v1.2: Session applied
 * @author Jiang
 * @date 2025-10-07
 * @version 1.1
 */

package sg.com.aori.interceptor;

import sg.com.aori.model.Orders;
import sg.com.aori.model.Customer;
import sg.com.aori.service.CustomerService;
import sg.com.aori.interfaces.IOrder;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import java.util.HashMap;
import java.util.Map;

@Component
public class OrderValidationInterceptor implements HandlerInterceptor {

    @Autowired
    private IOrder orderService;
    
    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CustomerService customerService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) 
            throws Exception {
        
        String requestURI = request.getRequestURI();
        String method = request.getMethod();
        
        // Intercept API order endpoints
        if (requestURI.startsWith("/api/orders/")) {
            String orderId = extractOrderId(requestURI);
            
            if (orderId == null) {
                return handleApiError(response, HttpServletResponse.SC_BAD_REQUEST, "Invalid order ID format");
            }

            // Validate order exists
            Orders order = orderService.findOrderById(orderId);
            if (order == null) {
                return handleApiError(response, HttpServletResponse.SC_NOT_FOUND, "Order not found");
            }

            // Get customerId from session
            String customerId = getCustomerIdFromSession(request);
            if (customerId == null) {
                return handleApiError(response, HttpServletResponse.SC_UNAUTHORIZED, "User not logged in");
            }
            
            // Validate order access permissions
            if (!order.getCustomerId().equals(customerId)) {
                return handleApiError(response, HttpServletResponse.SC_FORBIDDEN, "Access denied");
            }
            
            // For modification requests, check order status
            if (isModificationRequest(method, requestURI)) {
                if (!order.getOrderStatus().equals(Orders.OrderStatus.Pending)) {
                    return handleApiError(response, HttpServletResponse.SC_CONFLICT, "Order cannot be modified");
                }
            }
            
            // Store validated order in request attributes for later use
            request.setAttribute("validatedOrder", order);
        }
        
        return true;
    }
    
    /**
     * Extract order ID from API URL
     * Expected format: /api/orders/{orderId} or /api/orders/{orderId}/...
     */
    private String extractOrderId(String requestURI) {
        String[] pathParts = requestURI.split("/");
        
        if (requestURI.startsWith("/api/orders/") && pathParts.length >= 4) {
            return pathParts[3];
        }
        
        return null;
    }
    
    /**
     * Determine if request is a modification request
     */
    private boolean isModificationRequest(String method, String requestURI) {
        // POST, PUT, DELETE methods are modification requests
        if ("POST".equals(method) || "PUT".equals(method) || "DELETE".equals(method)) {
            return true;
        }
        
        return false;
    }

    /**
     * Get customerId from session based on LoginController logic
     */
    private String getCustomerIdFromSession(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return null;
        }
        
        String email = (String) session.getAttribute("email");
        if (email == null) {
            return null;
        }
        
        // Find customer by email to get customerId
        Customer customer = customerService.findCustomerByEmail(email).orElse(null);
        return customer != null ? customer.getCustomerId() : null;
    }
    

    /**
     * Handle API error responses with JSON format
     */
    private boolean handleApiError(HttpServletResponse response, int statusCode, String errorMessage) 
            throws Exception {
        
        response.setStatus(statusCode);
        response.setContentType("application/json");
        
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("success", false);
        errorResponse.put("message", errorMessage);
        errorResponse.put("statusCode", statusCode);
        
        objectMapper.writeValue(response.getWriter(), errorResponse);
        
        return false;
    }
}