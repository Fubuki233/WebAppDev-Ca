package sg.com.aori.controller;

import jakarta.servlet.http.HttpSession;
import sg.com.aori.repository.EmployeeRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin") // Base path for admin pages
public class AdminDashboardController {

    @Autowired
    private final EmployeeRepository employeeRepository;
    
    public AdminDashboardController(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    @GetMapping("/dashboard")
    public String showDashboard(HttpSession session, Model model) {
        // Simple check to ensure a logged-in employee is accessing the page
        String employeeId = (String) session.getAttribute("employeeId");

        if (employeeId == null) {
            return "redirect:/admin/login";
        }

        // add the found employee object to the model
        employeeRepository.findById(employeeId).ifPresent(employee -> {
            model.addAttribute("employee", employee);
        });

        // Set the active page for navigation highlighting
        model.addAttribute("activePage", "dashboard");

        // Return the path to the HTML view: /templates/admin/dashboard.html
        return "admin/dashboard";
    }
}