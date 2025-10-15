package sg.com.aori.utils;

import java.util.*;

/**
 * @author Xiaobo
 * @date 2025-10-09
 * @version 1.0
 */

public class PermissionMapper {

    private static final Map<String, String> PERMISSION_MAP = new HashMap<>();

    static {
        PERMISSION_MAP.put("POST:/api/permissions", "PERMISSION_CREATE");
        PERMISSION_MAP.put("GET:/api/permissions", "PERMISSION_READ");
        PERMISSION_MAP.put("GET:/api/permissions/", "PERMISSION_READ");
        PERMISSION_MAP.put("PUT:/api/permissions/", "PERMISSION_UPDATE");
        PERMISSION_MAP.put("DELETE:/api/permissions/", "PERMISSION_DELETE");
    }

    /**
     * Finds the required permission node based on the request URI and method,
     * handles path matching for ID variables.
     */
    public static Optional<String> getRequiredPermission(String path, String method) {
        String key = method.toUpperCase() + ":" + path;

        if (PERMISSION_MAP.containsKey(key)) {
            return Optional.of(PERMISSION_MAP.get(key));
        }

        if (path.matches("^" + EMPLOYEE_PATH_PREFIX + "/[^/]+$")) {
            String patternedKey = method.toUpperCase() + ":" + EMPLOYEE_PATH_PREFIX + "/";
            if (PERMISSION_MAP.containsKey(patternedKey)) {
                return Optional.of(PERMISSION_MAP.get(patternedKey));
            }
        }

        return Optional.empty();
    }

    private static final String EMPLOYEE_PATH_PREFIX = "/api/permissions";

}