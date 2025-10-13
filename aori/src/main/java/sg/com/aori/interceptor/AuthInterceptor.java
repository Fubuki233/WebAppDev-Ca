package sg.com.aori.interceptor;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import sg.com.aori.service.EmployeeService;
import sg.com.aori.service.LoginService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import sg.com.aori.utils.AuthFilter;
import sg.com.aori.config.DebugMode;
import sg.com.aori.utils.AuthHandler;

/**
 * Interceptor to log and validate user sessions.
 *
 * @author Yunhe
 * @date 2025-10-07
 * @version 1.0 basic version
 *          ------------------------------------------------------------------------
 * @author Yunhe
 * @date 2025-10-09
 * @version 2.0 introduced AuthFilter for bypass rules, now works well, but
 *          haven't introduced role-based access control yet
 *          ------------------------------------------------------------------------
 * @author Xiaobo
 * @date 2025-10-09
 * @version 3.0 Introduce role-based access control for employee. Seperate 2
 *          types of access (Customer and Employee)
 *          ------------------------------------------------------------------------
 * @author Yunhe
 * @date 2025-10-09
 * @version 3.1 optimized.
 *          ------------------------------------------------------------------------
 */

@Component
public class AuthInterceptor implements HandlerInterceptor {
    @Autowired
    LoginService loginService;

    @Autowired
    EmployeeService employeeService;

    // Configuration Constant for Employee Paths
    private static final String EMPLOYEE_PATH_PREFIX = "/api/permissions";

    // ----------------------------------------------------------------------------------
    // PUBLIC preHandle METHOD
    // ----------------------------------------------------------------------------------

    @Override
    public boolean preHandle(HttpServletRequest request,
            HttpServletResponse response, Object handler) throws Exception {

        if (DebugMode.DEBUG) {
            System.out.println(
                    "[LoggingInterceptor] Debug mode ON - bypassing all checks, if you wanna turn it off, please set DebugMode.DEBUG to false");
            return true;
        }

        String method = request.getMethod();
        String path = request.getRequestURI();

        Map<String, String> requestMap = Map.of("path", path, "method", method);
        System.out.println("[LoggingInterceptor] Request map: " + requestMap);

        if (AuthFilter.isAuthorized(requestMap)) {
            System.out.println("[LoggingInterceptor] Request bypass: " + path + ", " + method);
            return true;
        }

        // This handles server-rendered admin pages (/admin/*) and specific employee API
        // endpoints.
        if (path.startsWith("/admin") || path.startsWith(EMPLOYEE_PATH_PREFIX)) {
            return AuthHandler.handleEmployeeAccess(request, response, handler, employeeService);
        }

        // If it's not an admin/employee path, it must be a customer path.
        return AuthHandler.handleCustomerAccess(request, response, loginService);
    }
}