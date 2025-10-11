package sg.com.aori.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin") // Base path for admin pages
public class AdminDashboardController {

    @GetMapping("/dashboard")
    public String showDashboard(HttpSession session) {
        // Simple check to ensure a logged-in employee is accessing the page
        if (session.getAttribute("employeeId") == null) {
            return "redirect:/admin/login";
        }

        // Return the path to the dashboard HTML file
        // It will look for: /templates/admin/dashboard.html
        return "admin/dashboard";
    }
}