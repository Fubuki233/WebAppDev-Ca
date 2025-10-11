/**
 * Check if the request (path + method) is authorized/bypassed.
 * 
 * @param requestMap should contain "path" and "method" keys
 * @return true if the request is in the bypass list
 * @Aurthor Yunhe
 * @Date 2025-10-09
 * @Version 1.0
 */
package sg.com.aori.utils;

import java.util.List;
import java.util.Map;

public class AuthFilter {

    public static boolean isAuthorized(Map<String, String> requestMap) {
        List<Map<String, String>> byPassMap = List.of(
                Map.of("path", "/api/auth/login", "method", "POST"),
                Map.of("path", "/api/products", "method", "GET"),
                Map.of("path", "/api/categories", "method", "GET"),
                Map.of("path", "/api/products/search", "method", "GET"),
                Map.of("path", "/api/wishlist/exists", "method", "GET"),
                Map.of("path", "/api/customers", "method", "POST"), // For registration

                // --- NEW RULES FOR EMPLOYEE THYMELEAF LOGIN ---
                Map.of("path", "/admin/login", "method", "GET"), // To display the login form
                Map.of("path", "/admin/login", "method", "POST"), // To process the login form submission
                Map.of("path", "/admin/logout", "method", "GET"), // To handle logout
                Map.of("path", "/api/wishlist", "method", "GET")); // To handle wishlist
        // --- END NEW RULES ---

        // Extract path and method from requestMap
        System.out.println("[AuthFilter] checking requestMap: " + requestMap);
        String requestPath = requestMap.get("path");
        String requestMethod = requestMap.get("method");
        System.out.println("[AuthFilter] checking path: " + requestPath + ", method: " + requestMethod);

        // Always allow OPTIONS requests (CORS preflight)
        if ("OPTIONS".equals(requestMethod)) {
            System.out.println("[AuthFilter] OPTIONS request - allowing CORS preflight");
            return true;
        }

        // Always allow error page access (for all HTTP methods)
        if (requestPath != null && requestPath.equals("/error")) {
            System.out.println("[AuthFilter] Error page access allowed");
            return true;
        }

        // Allow GET requests to individual products: /api/products/{productId}
        if (requestPath != null && requestPath.startsWith("/api/products/") && "GET".equals(requestMethod)) {
            System.out.println("[AuthFilter] Individual product GET request allowed: " + requestPath);
            return true;
        }

        // Check if the path exists in byPassMap AND the method matches
        for (Map<String, String> entry : byPassMap) {
            if (entry.get("path").equals(requestPath) && entry.get("method").equals(requestMethod)) {
                System.out.println("[AuthFilter] Bypass rule matched: " + entry);
                return true;
            }
        }

        System.out.println("[AuthFilter] Request NOT a bypass rule");
        return false;
    }

    public static void main(String[] args) {
        Map<String, String> testRequest1 = Map.of("path", "/api/login", "method", "POST");
        Map<String, String> testRequest2 = Map.of("path", "/api/products", "method", "GET");
        Map<String, String> testRequest3 = Map.of("path", "/api/orders", "method", "GET");

        System.out.println("Test 1 (should be true): " + isAuthorized(testRequest1));
        System.out.println("Test 2 (should be true): " + isAuthorized(testRequest2));
        System.out.println("Test 3 (should be false): " + isAuthorized(testRequest3));
    }

}
