package sg.com.aori.utils;

import java.util.Map;

public class AuthFilter {

    /**
     * Check if the request (path + method) is authorized/bypassed.
     * 
     * @param requestMap should contain "path" and "method" keys
     * @return true if the request is in the bypass list
     */
    public static boolean isAuthorized(Map<String, String> requestMap) {
        // Define bypass rules: path -> allowed HTTP method
        Map<String, String> byPassMap = Map.of(
                "/api/login", "POST",
                "/api/products", "GET",
                "/api/categories", "GET",
                "/api/products/search", "GET",
                "/api/wishlist/exists", "GET");

        // Extract path and method from requestMap
        String requestPath = requestMap.get("path");
        String requestMethod = requestMap.get("method");

        // Check if the path exists in byPassMap AND the method matches
        if (requestPath != null && requestMethod != null) {
            String allowedMethod = byPassMap.get(requestPath);
            if (allowedMethod != null && allowedMethod.equals(requestMethod)) {
                return true;
            }
        }

        return false;
    }

}
