package sg.com.aori.controller;

import jakarta.servlet.http.HttpSession;
import sg.com.aori.repository.EmployeeRepository;
import sg.com.aori.repository.OrderRepository;
import sg.com.aori.repository.ProductRepository;

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

    @Autowired
    private final OrderRepository orderRepository;

    @Autowired
    private final ProductRepository productRepository;

    public AdminDashboardController(EmployeeRepository employeeRepository,
            OrderRepository orderRepository,
            ProductRepository productRepository) {
        this.employeeRepository = employeeRepository;
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
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

        // Add statistics for dashboard cards
        long totalOrders = orderRepository.count();
        long totalProducts = productRepository.count();
        long totalEmployees = employeeRepository.count();

        model.addAttribute("totalOrders", totalOrders);
        model.addAttribute("totalProducts", totalProducts);
        model.addAttribute("totalEmployees", totalEmployees);

        // Set the active page for navigation highlighting
        model.addAttribute("activePage", "dashboard");

        // Return the path to the HTML view: /templates/admin/dashboard.html
        return "admin/dashboard";
    }
}