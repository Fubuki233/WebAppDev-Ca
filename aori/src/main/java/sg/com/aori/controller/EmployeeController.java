package sg.com.aori.controller;

/**
 * Controller for Employee entity.
 *
 * @author xiaobo, SunRui
 * @date 2025-10-10
 * @version 1.1
 */

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import sg.com.aori.model.Employee;
import sg.com.aori.service.EmployeeService;
import sg.com.aori.service.RoleService;

@Controller
@RequestMapping("/admin/employees")
public class EmployeeController {

    private final EmployeeService employeeService;
    /** Inject roles for new employee */
    private final RoleService roleService;

    public EmployeeController(EmployeeService employeeService, RoleService roleService) {
        this.employeeService = employeeService;
        this.roleService = roleService;

    }

    // --- SHOW ALL EMPLOYEES (Read) ---
    // GET /admin/employees and /admin/employees/
    @GetMapping(value = { "", "/" })
    public String listEmployees(Model model) {
        List<Employee> employees = employeeService.getAllEmployees();
        // DEBUGGING: Check how many records were fetched
        System.out.println("DEBUG: listEmployees() called. Retrieved " + employees.size() + " employees.");
        // Pass the list of employees to the view template
        model.addAttribute("employees", employees);

        // Return the name of the Thymeleaf template
        return "admin/employee/employee-list";
    }

    // --- RENDER FORM FOR NEW EMPLOYEE (Create - GET) ---
    // GET /admin/employees/new
    @GetMapping(value = "/new")
    public String showCreateForm(Model model) {
        System.out.println("DEBUG: showCreateForm() called. Initializing new Employee object.");
        model.addAttribute("employee", new Employee());
        /** load all roles for creator to choose for the new employee */
        model.addAttribute("allRoles", roleService.getAllRoles());
        return "admin/employee/employee-form"; // Use the same form template for create and update
    }

    // --- PROCESS NEW EMPLOYEE (Create - POST) ---
    // POST /admin/employees/
    @PostMapping(value = "/")
    public String createEmployee(@Valid @ModelAttribute("employee") Employee employee) {
        employeeService.createEmployee(employee);
        // DEBUGGING: Show the data submitted from the form (before validation check)
        System.out.println("DEBUG: createEmployee() called. Submitted data: " + employee.toString());
        // Redirect to the list view after successful creation
        return "redirect:/admin/employees";
    }

    // --- RENDER FORM TO EDIT EMPLOYEE (Update - GET) ---
    // GET /admin/employees/{id}/edit
    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable String id, Model model) {
        // DEBUGGING: Check the ID from the URL path
        System.out.println("DEBUG: showEditForm() called. Employee ID from path: " + id);

        Employee employee = employeeService.getEmployeeById(id);

        if (employee == null) {
            System.out.println("ERROR: Employee not found with ID: " + id);
            return "redirect:/admin/employees";
        }

        System.out.println("DEBUG: Retrieved Employee for edit: " + employee.getEmail());

        // Pass the existing Employee data to pre-populate the form
        model.addAttribute("employee", employee);

        // Also pass the list of all roles for the dropdown
        model.addAttribute("allRoles", roleService.getAllRoles());

        return "admin/employee/employee-form";
    }

    // --- PROCESS EDITED EMPLOYEE (Update - POST/PUT) ---
    // POST /employees/{id} (Often used instead of PUT in pure form submissions for
    // simplicity)
    @PostMapping("/{id}")
    public String updateEmployee(@PathVariable String id, @Valid @ModelAttribute("employee") Employee employeeDetails,
            BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes) {
        // DEBUGGING: Check the path ID and the bound object data
        System.out.println("DEBUG: updateEmployee() called. Path ID: " + id);
        System.out.println("DEBUG: Submitted update data: " + employeeDetails.toString());

        if (bindingResult.hasErrors()) {
            // CRITICAL FIX: To prevent the Whitelabel Error Page,
            // you MUST re-add any model attributes the form needs, like 'allRoles'.
            model.addAttribute("allRoles", roleService.getAllRoles());

            // Ensure the ID is correctly set on the model object for the form action to
            // work
            employeeDetails.setEmployeeId(id);
            return "admin/employee/employee-form";
        }

        try {
            employeeService.updateEmployee(id, employeeDetails);
        } catch (IllegalArgumentException e) {
            // Catch business validation errors from the service
            model.addAttribute("allRoles", roleService.getAllRoles());
            // Add the error to the 'email' field to display it next to the input
            bindingResult.rejectValue("email", "error.employee", e.getMessage());
            return "admin/employee/employee-form";
        }

        // DEBUGGING
        System.out.println("DEBUG: Employee updated successfully. Redirecting to list.");
        redirectAttributes.addFlashAttribute("success", "Employee updated successfully!");
        return "redirect:/admin/employees";
    }

    // --- DELETE EMPLOYEE ---
    // GET /employees/{id}/delete (Simple method often used for quick UI links)
    @GetMapping("/{id}/delete")
    public String deleteEmployee(@PathVariable @NotBlank(message = "Employee ID cannot be empty") String id) {
        // DEBUGGING: Confirm the ID being targeted for deletion
        System.out.println("DEBUG: deleteEmployee() called. ID to delete: " + id);

        employeeService.deleteEmployee(id);
        System.out.println("DEBUG: Employee deleted successfully. Redirecting to list.");
        return "redirect:/admin/employees";
    }
}