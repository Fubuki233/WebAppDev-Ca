package sg.com.aori.interceptor;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import sg.com.aori.model.Customer;
import sg.com.aori.model.Employee;
import sg.com.aori.service.EmployeeService;
import sg.com.aori.service.LoginService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import sg.com.aori.utils.AuthFilter;
import sg.com.aori.utils.PermissionMapper;
import sg.com.aori.config.DebugMode;

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
 */

@Component
public class AuthInterceptor implements HandlerInterceptor {
    @Autowired
    LoginService loginService;

    @Autowired // Inject the service to look up employee details and permissions
    EmployeeService employeeService;

    // Configuration Constant for Employee Paths
    private static final String EMPLOYEE_PATH_PREFIX = "/api/permissions";

    // ----------------------------------------------------------------------------------
    // PRIVATE HANDLERS
    // ----------------------------------------------------------------------------------

    private boolean handleEmployeeAccess(HttpServletRequest request,
            HttpServletResponse response,
            Object handler) throws IOException {

        HttpSession session = request.getSession();
        String employeeId = (String) session.getAttribute("employeeId");
        String path = request.getRequestURI();
        String method = request.getMethod();

        if (employeeId == null || employeeId.isEmpty()) {
            System.out.println("[EmployeeAccess] No employee session. Access denied.");
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Employee login required.");
            return false;
        }

        // Validate Employee in DB
        Employee employee = employeeService.findEmployeeWithPermissions(employeeId).orElse(null);
        if (employee == null) {
            System.out.println("[EmployeeAccess] Invalid session. Employee not found.");
            session.invalidate();
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Invalid session.");
            return false;
        }

        // --- NEW PATH-BASED ACCESS CONTROL (RBAC) Check ---

        // 1. Map the request path and method to a required permission node
        Optional<String> requiredPermissionNode = PermissionMapper.getRequiredPermission(path, method);

        if (requiredPermissionNode.isPresent()) {
            String permissionNode = requiredPermissionNode.get();

            System.out.println("[EmployeeAccess] Path required permission: " + permissionNode);

            // 2. Check the employee's permissions
            if (!employeeService.hasPermission(employee, permissionNode)) {
                System.out.println("[EmployeeAccess] Access denied. Missing permission: " + permissionNode);
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "Insufficient permissions.");
                return false;
            }
        }
        // NOTE: If getRequiredPermission returns empty, access is allowed by default.

        System.out.println("[EmployeeAccess] Access granted for employee: " + employee.getEmail());
        return true;
    }

    // NEW HANDLER FOR CUSTOMER ACCESS (Encapsulates the old validation logic)
    private boolean handleCustomerAccess(HttpServletRequest request,
            HttpServletResponse response) throws IOException {

        HttpSession session = request.getSession();
        String customerId = (String) session.getAttribute("customerId"); // 👈 Using "customerId" key

        // Check if user ID exists in session
        if (customerId == null || customerId.isEmpty()) {
            System.out.println("[CustomerAccess] No customer ID in session - redirecting to login.");
            response.sendRedirect("/login");
            return false;
        }

        // Validate customer exists in database
        Customer customer = loginService.findCustomerById(customerId).orElse(null);
        if (customer != null) {
            System.out.println("[CustomerAccess] Session valid for user: " + customer.getEmail());
            return true;
        } else {
            System.out.println("[CustomerAccess] Session invalid - user not found in database");
            session.invalidate();
            response.sendRedirect("/login");
            return false;
        }
    }

    // ----------------------------------------------------------------------------------
    // PUBLIC preHandle METHOD
    // ----------------------------------------------------------------------------------

    @Override
    public boolean preHandle(HttpServletRequest request,
            HttpServletResponse response, Object handler) throws Exception {

        if (DebugMode.DEBUG) {
            System.out.println("[LoggingInterceptor] Debug mode ON - bypassing all checks");
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

        // 1. Employee/Admin Check
        if (path.startsWith(EMPLOYEE_PATH_PREFIX) || path.startsWith("/admin")) {
            return handleEmployeeAccess(request, response, handler);
        }

        // 2. Customer Access Check (Default for non-employee restricted paths)
        System.out.println("[LoggingInterceptor] Not a bypass request - Validating customer session for path: " + path
                + ", method: " + method);
        return handleCustomerAccess(request, response);
    }
}