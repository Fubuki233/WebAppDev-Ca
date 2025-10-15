package sg.com.aori.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import sg.com.aori.interfaces.IEmployee;
import sg.com.aori.model.Employee;

/**
 * Controller for Employee Login into Aori Admin Portal.
 *
 * @author Xiaobo
 * @date 2025-10-10
 * @version 1.0 - Initial implementation by Xiaobo
 * 
 * @author Ying Chun
 * @date 2025-10-10
 * @version 1.1 - Updated HTML template paths
 */

@Controller
@RequestMapping("/admin")
public class EmployeeLoginController {

    private final IEmployee employeeService;

    public EmployeeLoginController(IEmployee employeeService) {
        this.employeeService = employeeService;
    }

    /**
     * Display login form
     */
    @GetMapping("/login")
    public String showLoginForm(Model model, HttpSession session) {

        if (session.getAttribute("employeeId") != null) {
            return "redirect:/admin/dashboard";
        }

        model.addAttribute("employee", new Employee());
        return "admin/login-form";
    }

    /**
     * Process login submission
     */
    @PostMapping("/login")
    public String processLogin(@ModelAttribute("employee") Employee loginFormEmployee,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        String email = loginFormEmployee.getEmail();
        String password = loginFormEmployee.getPassword();

        employeeService.loginEmployee(email, password)
                .ifPresentOrElse(
                        (authenticatedEmployee) -> {
                            session.setAttribute("employeeId", authenticatedEmployee.getEmployeeId());
                            session.setAttribute("employeeEmail", authenticatedEmployee.getEmail());

                            redirectAttributes.addFlashAttribute("success",
                                    "Welcome, " + authenticatedEmployee.getFirstName() + "!");
                        },
                        () -> {
                            redirectAttributes.addFlashAttribute("error", "Invalid email or password.");
                        });

        return session.getAttribute("employeeId") != null
                ? "redirect:/admin/dashboard"
                : "redirect:/admin/login";
    }

    /**
     * Process logout
     */
    @PostMapping("/logout")
    public String logout(HttpSession session, RedirectAttributes redirectAttributes) {
        session.invalidate();
        redirectAttributes.addFlashAttribute("success", "You have been logged out successfully.");
        return "redirect:/admin/login";
    }
}
