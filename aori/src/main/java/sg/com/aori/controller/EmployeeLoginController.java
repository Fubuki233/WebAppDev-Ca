/**
 * Controller for Employee Login into Aori Admin Portal.
 *
 * @author Xiaobo, Ying Chun
 * @date 2025-10-10
 * @version 1.0 (Xiaobo), 1.1 (Ying Chun)
 * v1 - Initial implementation by Xiaobo
 * v1.1 - Updated HTML template paths 
 * 
 */

package sg.com.aori.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import sg.com.aori.interfaces.IEmployee;
import sg.com.aori.model.Employee;

@Controller
@RequestMapping("/admin") // Base path for admin views
public class EmployeeLoginController {

    private final IEmployee employeeService;

    public EmployeeLoginController(IEmployee employeeService) {
        this.employeeService = employeeService;
    }

    // --- 1. DISPLAY LOGIN FORM (GET /admin/login) ---
    @GetMapping("/login")
    public String showLoginForm(Model model, HttpSession session) {
        // Redirect if the employee is already authenticated
        if (session.getAttribute("employeeId") != null) {
            return "redirect:/admin/dashboard";
        }

        // Use the Employee entity as the form backing object
        model.addAttribute("employee", new Employee());
        return "admin/login-form"; // Refers to src/main/resources/templates/login-form.html
    }

    // --- 2. PROCESS LOGIN SUBMISSION (POST /admin/login) ---
    @PostMapping("/login")
    public String processLogin(@ModelAttribute("employee") Employee loginFormEmployee,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        String email = loginFormEmployee.getEmail();
        String password = loginFormEmployee.getPassword();

        // 1. Authenticate via Service Layer
        employeeService.loginEmployee(email, password)
                .ifPresentOrElse(
                        // Success: Employee found and password matches
                        (authenticatedEmployee) -> {
                            // 2. CRITICAL: Store the Employee ID in the session under the key expected by
                            // AuthHandler.
                            // AuthHandler.java uses 'employeeId'
                            session.setAttribute("employeeId", authenticatedEmployee.getEmployeeId());
                            session.setAttribute("employeeEmail", authenticatedEmployee.getEmail());

                            redirectAttributes.addFlashAttribute("success",
                                    "Welcome, " + authenticatedEmployee.getFirstName() + "!");
                        },
                        // Failure: Authentication failed
                        () -> {
                            redirectAttributes.addFlashAttribute("error", "Invalid email or password.");
                        });

        // 3. Determine redirect path
        return session.getAttribute("employeeId") != null
                ? "redirect:/admin/dashboard" // Success path
                : "redirect:/admin/login"; // Failure path
    }

    // --- 3. LOGOUT (GET /admin/logout) ---
    @GetMapping("/logout")
    public String logout(HttpSession session, RedirectAttributes redirectAttributes) {
        session.invalidate(); // Clear the session
        redirectAttributes.addFlashAttribute("success", "You have been logged out successfully.");
        return "redirect:/admin/login";
    }
}
