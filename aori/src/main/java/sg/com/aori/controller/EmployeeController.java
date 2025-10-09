package sg.com.aori.controller;

import org.springframework.web.bind.annotation.RequestMapping;

import sg.com.aori.model.Employee;
import sg.com.aori.service.EmployeeService;

import java.util.List;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

/**
 * Controller for Employee entity.
 *
 * @author xiaobo, SunRui
 * @date 2025-10-07
 * @version 1.1
 */

@Controller // This enables Spring to resolve view names (e.g., "employee-list")
@RequestMapping("/employees")
public class EmployeeController {

    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    // --- RENDER ALL EMPLOYEES (Read) ---
    // GET /employees
    @GetMapping
    public String listEmployees(Model model) {
        List<Employee> employees = employeeService.getAllEmployees();

        // Pass the list of employees to the view template
        model.addAttribute("employees", employees);

        // Return the name of the Thymeleaf template
        return "employee-list";
    }

    // --- RENDER FORM FOR NEW EMPLOYEE (Create - GET) ---
    // GET /employees/new
    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("employee", new Employee());
        return "employee-form"; // Use the same form template for create and update
    }

    // --- PROCESS NEW EMPLOYEE (Create - POST) ---
    // POST /employees
    @PostMapping
    public String createEmployee(@Valid @ModelAttribute("employee") Employee employee) {
        employeeService.createEmployee(employee);
        // Redirect to the list view after successful creation
        return "redirect:/employees";
    }

    // --- RENDER FORM TO EDIT EMPLOYEE (Update - GET) ---
    // GET /employees/{id}/edit
    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable String id, Model model) {
        Employee employee = employeeService.getEmployeeById(id);

        // Pass the existing Employee data to pre-populate the form
        model.addAttribute("employee", employee);
        return "employee-form";
    }

    // --- PROCESS EDITED EMPLOYEE (Update - POST/PUT) ---
    // POST /employees/{id} (Often used instead of PUT in pure form submissions for
    // simplicity)
    @PostMapping("/{id}")
    public String updateEmployee(@PathVariable String id,
            @Valid @ModelAttribute("employee") Employee employeeDetails) {
        employeeService.updateEmployee(id, employeeDetails);
        return "redirect:/employees";
    }

    // --- DELETE EMPLOYEE ---
    // GET /employees/{id}/delete (Simple method often used for quick UI links)
    @GetMapping("/{id}/delete")
    public String deleteEmployee(@PathVariable String id) {
        employeeService.deleteEmployee(id);
        return "redirect:/employees";
    }
}