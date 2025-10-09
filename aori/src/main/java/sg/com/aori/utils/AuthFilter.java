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

import java.util.Map;

public class AuthFilter {

    public static boolean isAuthorized(Map<String, String> requestMap) {
        // Define bypass rules: path -> allowed HTTP method
        Map<String, String> byPassMap = Map.of(
                "/api/auth/login", "POST",
                "/api/products", "GET",
                "/api/categories", "GET",
                "/api/products/search", "GET",
                "/api/wishlist/exists", "GET");

        // Extract path and method from requestMap
        System.out.println("AuthFilter checking requestMap: " + requestMap);
        String requestPath = requestMap.get("path");
        String requestMethod = requestMap.get("method");
        System.out.println("AuthFilter checking path: " + requestPath + ", method: " + requestMethod);

        // Check if the path exists in byPassMap AND the method matches
        if (requestPath != null && requestMethod != null) {
            String allowedMethod = byPassMap.get(requestPath);
            System.out.println("Allowed method for " + requestPath + ": " + allowedMethod);
            if (allowedMethod != null && allowedMethod.equals(requestMethod)) {
                System.out.println("Request authorized!");
                return true;
            }
        }

        System.out.println("Request NOT authorized!");
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
