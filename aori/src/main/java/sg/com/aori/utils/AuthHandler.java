package sg.com.aori.utils;

import java.io.IOException;
import java.util.Optional;
import jakarta.servlet.http.*;

import sg.com.aori.model.Customer;
import sg.com.aori.model.Employee;
import sg.com.aori.service.EmployeeService;
import sg.com.aori.service.LoginService;

/**
 * AuthHandler for handling customer and employee authentication.
 *
 * @author Xiaobo
 * @date 2025-10-09
 * @version 3.0 - Introduce role-based access control for employee. Seperate 2
 *          types of access (Customer and Employee)
 */
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

        Employee employee = employeeService.findEmployeeWithPermissions(employeeId).orElse(null);
        if (employee == null) {
            System.out.println("[EmployeeAccess] Invalid session. Employee not found.");
            session.invalidate();
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Invalid session.");
            return false;
        }

        Optional<String> requiredPermissionNode = PermissionMapper.getRequiredPermission(path, method);

        if (requiredPermissionNode.isPresent()) {
            String permissionNode = requiredPermissionNode.get();

            System.out.println("[EmployeeAccess] Path required permission: " + permissionNode);

            if (!employeeService.hasPermission(employee, permissionNode)) {
                System.out.println("[EmployeeAccess] Access denied. Missing permission: " + permissionNode);
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "Insufficient permissions.");
                return false;
            }
        }

        System.out.println("[EmployeeAccess] Access granted for employee: " + employee.getEmail());
        return true;
    }

    public static boolean handleCustomerAccess(HttpServletRequest request,
            HttpServletResponse response,
            LoginService loginService) throws IOException {

        HttpSession session = request.getSession();
        String customerId = (String) session.getAttribute("id");

        String origin = request.getHeader("Origin");
        if (origin == null || origin.isEmpty()) {
            origin = request.getScheme() + "://" + request.getServerName() + ":5173";
        }

        String loginRedirectUrl = origin + "/#login";

        if (customerId == null || customerId.isEmpty()) {
            System.out.println("[CustomerAccess] No customer ID in session - returning 401 Unauthorized " + customerId);

            if ("/api/auth/logout".equals(request.getRequestURI())) {
                response.setStatus(HttpServletResponse.SC_OK);
                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");
                response.getWriter()
                        .write("{\"success\":false,\"message\":\"Already logged out\"}");
                return false;
            }

            if (request.getRequestURI().startsWith("/api/")) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");
                response.getWriter().write(
                        "{\"success\":false,\"message\":\"Authentication required\",\"redirectTo\":\""
                                + loginRedirectUrl + "\"}");
                return false;
            } else {
                response.sendRedirect(loginRedirectUrl);
                return false;
            }
        }

        Customer customer = loginService.findCustomerById(customerId).orElse(null);
        if (customer != null) {
            System.out.println("[CustomerAccess] Session valid for user: " + customer.getEmail());
            return true;
        } else {
            System.out.println("[CustomerAccess] Session invalid - user not found in database");
            session.invalidate();

            if (request.getRequestURI().startsWith("/api/")) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");
                response.getWriter().write(
                        "{\"success\":false,\"message\":\"Invalid session\",\"redirectTo\":\"" + loginRedirectUrl
                                + "\"}");
                return false;
            } else {
                response.sendRedirect(loginRedirectUrl);
                return false;
            }
        }
    }
}
