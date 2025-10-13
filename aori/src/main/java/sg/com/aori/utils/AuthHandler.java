package sg.com.aori.utils;

/**
 * AuthHandler for handling customer and employee authentication.
 *
 * @author Xiaobo
 * @date 2025-10-09
 * @version 3.0 Introduce role-based access control for employee. Seperate 2
 *          types of access (Customer and Employee)
 */

import java.io.IOException;
import java.util.Optional;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import sg.com.aori.model.Customer;
import sg.com.aori.model.Employee;
import sg.com.aori.service.EmployeeService;
import sg.com.aori.service.LoginService;

public class AuthHandler {

    public static boolean handleEmployeeAccess(HttpServletRequest request,
            HttpServletResponse response,
            Object handler,
            EmployeeService employeeService) throws IOException {

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
    public static boolean handleCustomerAccess(HttpServletRequest request,
            HttpServletResponse response,
            LoginService loginService) throws IOException {

        HttpSession session = request.getSession();
        // Use "id" to match LoginController
        String customerId = (String) session.getAttribute("id");

        // Get the origin from request header to make redirect URL adaptive
        String origin = request.getHeader("Origin");
        if (origin == null || origin.isEmpty()) {
            // Fallback to constructing URL from request
            origin = request.getScheme() + "://" + request.getServerName() + ":5173";
        }
        String loginRedirectUrl = origin + "/#login";

        // Check if user ID exists in session
        if (customerId == null || customerId.isEmpty()) {
            System.out.println("[CustomerAccess] No customer ID in session - returning 401 Unauthorized " + customerId);

            // For API requests, return JSON error instead of redirect
            if (request.getRequestURI().startsWith("/api/")) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");
                response.getWriter().write(
                        "{\"success\":false,\"message\":\"Authentication required\",\"redirectTo\":\""
                                + loginRedirectUrl + "\"}");
                return false;
            } else {
                // For non-API requests, redirect to frontend login page
                response.sendRedirect(loginRedirectUrl);
                return false;
            }
        }

        // Validate customer exists in database
        Customer customer = loginService.findCustomerById(customerId).orElse(null);
        if (customer != null) {
            System.out.println("[CustomerAccess] Session valid for user: " + customer.getEmail());
            return true;
        } else {
            System.out.println("[CustomerAccess] Session invalid - user not found in database");
            session.invalidate();

            // For API requests, return JSON error instead of redirect
            if (request.getRequestURI().startsWith("/api/")) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");
                response.getWriter().write(
                        "{\"success\":false,\"message\":\"Invalid session\",\"redirectTo\":\"" + loginRedirectUrl
                                + "\"}");
                return false;
            } else {
                // For non-API requests, redirect to frontend login page
                response.sendRedirect(loginRedirectUrl);
                return false;
            }
        }
    }
}
