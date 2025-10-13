/**
 * @author xiaobo
 * @date 2025-10-09
 * @version 1.0
 */
package sg.com.aori.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class PermissionMapper {

    // Define a map where the key is "METHOD:PATH_PREFIX" and the value is the
    // permission node
    private static final Map<String, String> PERMISSION_MAP = new HashMap<>();

    static {
        // Example mappings for your /api/permissions controller
        PERMISSION_MAP.put("POST:/api/permissions", "PERMISSION_CREATE");
        PERMISSION_MAP.put("GET:/api/permissions", "PERMISSION_READ");
        PERMISSION_MAP.put("GET:/api/permissions/", "PERMISSION_READ"); // For /api/permissions/{id}
        PERMISSION_MAP.put("PUT:/api/permissions/", "PERMISSION_UPDATE"); // For /api/permissions/{id}
        PERMISSION_MAP.put("DELETE:/api/permissions/", "PERMISSION_DELETE"); // For /api/permissions/{id}

        // Add other administrative paths here (e.g., /admin/users)
        // PERMISSION_MAP.put("POST:/admin/users", "USER_CREATE");
    }

    /**
     * Finds the required permission node based on the request URI and method.
     * This method handles path matching for ID variables (e.g.,
     * /api/permissions/123 -> /api/permissions/).
     */
    public static Optional<String> getRequiredPermission(String path, String method) {
        String key = method.toUpperCase() + ":" + path;

        // 1. Try exact match first
        if (PERMISSION_MAP.containsKey(key)) {
            return Optional.of(PERMISSION_MAP.get(key));
        }

        // 2. Try pattern match for paths ending in an ID (e.g., /api/permissions/123)
        // This is a simplified regex; a real system might use PathMatcher.
        if (path.matches("^" + EMPLOYEE_PATH_PREFIX + "/[^/]+$")) {
            String patternedKey = method.toUpperCase() + ":" + EMPLOYEE_PATH_PREFIX + "/";
            if (PERMISSION_MAP.containsKey(patternedKey)) {
                return Optional.of(PERMISSION_MAP.get(patternedKey));
            }
        }

        return Optional.empty(); // No specific permission required for this path/method combination
    }

    // Note: The EMPLOYEE_PATH_PREFIX constant must be defined within the
    // PermissionMapper or passed in.
    private static final String EMPLOYEE_PATH_PREFIX = "/api/permissions";
}