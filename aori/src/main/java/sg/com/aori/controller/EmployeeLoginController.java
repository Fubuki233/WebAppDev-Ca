package sg.com.aori.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;
import sg.com.aori.interfaces.IEmployee;
import sg.com.aori.model.Employee;

@Controller
@RequestMapping("/admin")
public class EmployeeLoginController {
    private final IEmployee employeeService;

    public EmployeeLoginController(IEmployee employeeService) {
        this.employeeService = employeeService;
    }

    // --- 1. DISPLAY LOGIN FORM (GET /admin/login) ---
    @GetMapping("/login")
    public String showLoginForm(Model model, HttpSession session) {
        // Redirect if already logged in
        if (session.getAttribute("loggedInEmployeeId") != null) {
            return "redirect:/admin/account/profile";
        }

        // Pass a blank Employee object to the model for Thymeleaf to bind form data
        model.addAttribute("employee", new Employee());
        return "login-form"; // Thymeleaf template name
    }

    // --- 2. PROCESS LOGIN SUBMISSION (POST /admin/login) ---
    @PostMapping("/login")
    public String processLogin(@ModelAttribute("employee") Employee loginFormEmployee,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        String email = loginFormEmployee.getEmail();
        String password = loginFormEmployee.getPassword();

        employeeService.loginEmployee(email, password)
                .ifPresentOrElse(
                        // Success: Employee found and authenticated
                        (authenticatedEmployee) -> {
                            // Store authentication context in the session
                            session.setAttribute("loggedInEmployeeId", authenticatedEmployee.getEmployeeId());
                            session.setAttribute("loggedInEmployeeEmail", authenticatedEmployee.getEmail());
                            redirectAttributes.addFlashAttribute("success",
                                    "Welcome, " + authenticatedEmployee.getFirstName() + "!");
                        },
                        // Failure: Authentication failed
                        () -> {
                            redirectAttributes.addFlashAttribute("error", "Invalid email or password.");
                        });

        // Redirect to profile on success, or back to login on failure
        return session.getAttribute("loggedInEmployeeId") != null
                ? "redirect:/admin/account/profile"
                : "redirect:/admin/login";
    }

    // --- 3. LOGOUT (GET /admin/logout) ---
    @GetMapping("/logout")
    public String logout(HttpSession session, RedirectAttributes redirectAttributes) {
        session.invalidate(); // Destroy the session
        redirectAttributes.addFlashAttribute("success", "You have been logged out successfully.");
        return "redirect:/admin/login";
    }
}