
/**
 * Checks:
1. The user accessing {customerId} is the user themselves (logged-in users can only access/modify their own data, not others').
2. Registration and email address queries are whitelisted and are allowed.
 *
 * @author Sun Rui
 * @date 2025-10-08
 * @version 1.0
 */

package sg.com.aori.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * Interceptor to enforce resource ownership (authorization) on
 * /api/customers/**.
 * It DOES NOT check whether the user is logged in (authentication).
 * Login/redirect behavior is handled by LoginInterceptor or other components.
 */
@Component
public class SecurityInterceptor implements HandlerInterceptor {

    /** Session key for logged-in customer id */
    private static final String AUTH_CUSTOMER_ID = "AUTH_CUSTOMER_ID";

    /** Message to be displayed to the user */
    private static final String FORBIDDEN_MSG = "{\"error\":\"FORBIDDEN\",\"message\":\"Not your resource\"}";

    /** Pattern to extract {customerId} from /api/customers/{customerId}/... */
    private static final Pattern CUSTOMER_PATH = Pattern.compile("^/api/customers/([^/]+)(?:/.*)?$");

    /**
     * Enforce "self-only" access on /api/customers/{customerId}/** while allowing
     * anonymous whitelisted endpoints.
     * (No authentication check here; only ownership check if session key exists.)
     *
     * param: request Current HTTP request.
     * return: true to continue; false if ownership check fails and a 403 response
     * is written.
     * throws: IllegalArgumentException if input is invalid.
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        final String method = request.getMethod();
        final String path = request.getRequestURI();

        // 1) Anonymous whitelist: allow open endpoints (no auth/ownership needed)
        if (isAnonymousAllowed(method, path, request)) {
            return true;
        }

        // 2) Ownership check ONLY IF session already carries AUTH_CUSTOMER_ID
        HttpSession session = request.getSession(false); // do not create new session
        String authCustomerId = (session == null) ? null : (String) session.getAttribute(AUTH_CUSTOMER_ID);

        // If there is no session or no auth id, we DO NOT block here (authentication
        // handled elsewhere)
        if (authCustomerId == null || authCustomerId.isBlank()) {
            return true; // pass through; LoginInterceptor (or other) should handle login requirement
        }

        // 3) When path includes a customerId, ensure it equals the session one
        String pathCustomerId = extractCustomerIdFromPath(path);
        if (pathCustomerId != null && !pathCustomerId.equals(authCustomerId)) {
            writeJson(response, HttpServletResponse.SC_FORBIDDEN, FORBIDDEN_MSG);
            return false;
        }

        return true;
    }

    /**
     * Allow anonymous access for specific endpoints.
     *
     * param: method HTTP method.
     * param: path Request URI.
     * param: request HTTP request (for reading query params).
     * return: true if the request is whitelisted.
     * throws: IllegalArgumentException if input is invalid.
     */
    private boolean isAnonymousAllowed(String method, String path, HttpServletRequest request) {
        // Registration
        if ("POST".equals(method) && "/api/customers".equals(path))
            return true;

        // Email lookup
        if ("GET".equals(method) && "/api/customers".equals(path) && request.getParameter("email") != null)
            return true;

        return false;
    }

    /**
     * Extract {customerId} from /api/customers/{id}/...
     *
     * param: path Request URI.
     * return: customerId if present; otherwise null.
     * throws: IllegalArgumentException if input is invalid.
     */
    private String extractCustomerIdFromPath(String path) {
        Matcher m = CUSTOMER_PATH.matcher(path);
        return m.matches() ? m.group(1) : null;
    }

    /**
     * Write a small JSON error body.
     *
     * param: response HttpServletResponse to write to.
     * param: status HTTP status code.
     * param: body JSON string body.
     * return: void
     * throws: IllegalArgumentException if input is invalid.
     */
    private void writeJson(HttpServletResponse response, int status, String body) {
        try {
            response.setStatus(status);
            response.setCharacterEncoding(UTF_8.name());
            response.setContentType("application/json");
            response.getWriter().write(body);
            response.getWriter().flush();
        } catch (Exception ignored) {
        }
    }
}
