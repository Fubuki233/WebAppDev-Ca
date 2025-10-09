package sg.com.aori.interceptor;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import sg.com.aori.model.Customer;
import sg.com.aori.service.LoginService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import sg.com.aori.utils.AuthFilter;

/**
 * Interceptor to log and validate user sessions.
 *
 * @author Yunhe
 * @date 2025-10-07
 * @version 1.0
 */

@Component
public class LoggingInterceptor implements HandlerInterceptor {
    @Autowired
    LoginService loginService;

    @Override
    public boolean preHandle(HttpServletRequest request,
            HttpServletResponse response, Object handler) {
        String method = request.getMethod();
        String path = request.getRequestURI();
        Map<String, String> requestMap = Map.of(
                "path", path,
                "method", method);
        System.out.println("[LoggingInterceptor] Request map: " + requestMap);
        if (AuthFilter.isAuthorized(requestMap)) {
            System.out.println("[LoggingInterceptor] Request bypass: " + path + ", " + method);
            return true;
        }
        HttpSession session = request.getSession();
        String id = (String) session.getAttribute("id");

        // Check if user ID exists in session
        if (id == null || id.isEmpty()) {
            System.out.println("[LoggingInterceptor] No user ID in session - treating as guest");
            return false;

        }

        // Validate user exists in database
        Customer customer = loginService.findCustomerById(id).orElse(null);
        if (customer != null) {
            System.out.println("[LoggingInterceptor] Session valid for user: " + customer.getEmail());
            return true;
        } else {
            System.out.println("[LoggingInterceptor] Session invalid - user not found in database");
            // Clear invalid session
            session.invalidate();
            try {
                response.sendRedirect("/login");
            } catch (Exception e) {
                System.out.println("[LoggingInterceptor] Error redirecting: " + e.getMessage());
            }
            return false;
        }
    }

    @Override
    public void postHandle(HttpServletRequest request,
            HttpServletResponse response, Object handler, ModelAndView modelAndView) {
    }

    @Override
    public void afterCompletion(HttpServletRequest request,
            HttpServletResponse response, Object handler, Exception ex) {
    }
}