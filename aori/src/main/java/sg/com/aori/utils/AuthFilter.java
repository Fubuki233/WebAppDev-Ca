/**
 * Check if the request (path + method) is authorized/bypassed.
 * 
 * @param requestMap should contain "path" and "method" keys
 * @return true if the request is in the bypass list
 * @Author Yunhe
 * @Date 2025-10-09
 * @Version 1.0
 * 
 * @Author Yunhe
 * @date 2025-10-12
 * @version 1.1 - added more bypass rules
 * 
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
                Map.of("path", "/api/customers", "method", "POST"),
                Map.of("path", "/admin/login", "method", "GET"),
                Map.of("path", "/admin/login", "method", "POST"),
                Map.of("path", "/admin/logout", "method", "GET"),
                Map.of("path", "/api/wishlist", "method", "GET"),
                Map.of("path", "/api/view-history", "method", "GET"),
                Map.of("path", "/api/products/collectionDisplay", "method", "GET"));

        System.out.println("[AuthFilter] checking requestMap: " + requestMap);
        String requestPath = requestMap.get("path");
        String requestMethod = requestMap.get("method");
        System.out.println("[AuthFilter] checking path: " + requestPath + ", method: " + requestMethod);

        if ("OPTIONS".equals(requestMethod)) {
            System.out.println("[AuthFilter] OPTIONS request - allowing CORS preflight");
            return true;
        }

        if (requestPath != null && requestPath.equals("/error")) {
            System.out.println("[AuthFilter] Error page access allowed");
            return true;
        }

        if (requestPath != null && requestPath.startsWith("/api/products/") && "GET".equals(requestMethod)) {
            System.out.println("[AuthFilter] Individual product GET request allowed: " + requestPath);
            return true;
        }

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
