package sg.com.aori.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.constraints.NotBlank;
import sg.com.aori.model.Employee;
import sg.com.aori.service.EmployeeService;
import sg.com.aori.utils.ValidationGroups;
import sg.com.aori.service.RoleService;

/**
 * Controller for Employee entity.
 *
 * @author Xiaobo, Sun Rui
 * @date 2025-10-10
 * @version 1.1
 */

@Controller
@RequestMapping("/admin/employees")
public class EmployeeController {

    private final EmployeeService employeeService;

    private final RoleService roleService;

    public EmployeeController(EmployeeService employeeService, RoleService roleService) {
        this.employeeService = employeeService;
        this.roleService = roleService;
    }

    /**
     * Show all employees
     */
    @GetMapping(value = { "", "/" })
    public String listEmployees(Model model) {
        List<Employee> employees = employeeService.getAllEmployees();
        System.out.println("DEBUG: listEmployees() called. Retrieved " + employees.size() + " employees.");

        model.addAttribute("employees", employees);
        model.addAttribute("activePage", "employees");

        return "admin/employee/employee-list";
    }

    /**
     * Render form for new employee
     * 
     * @param model
     * @return
     */
    @GetMapping(value = "/new")
    public String showCreateForm(Model model) {
        System.out.println("DEBUG: showCreateForm() called. Initializing new Employee object.");

        model.addAttribute("employee", new Employee());
        model.addAttribute("allRoles", roleService.getAllRoles());
        model.addAttribute("activePage", "employees");

        return "admin/employee/employee-form";
    }

    /**
     * Process new employee
     */
    @PostMapping(value = "/")
    public String createEmployee(
            @Validated(ValidationGroups.Create.class) @ModelAttribute("employee") Employee employee,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {

        System.out.println("DEBUG: createEmployee() called. Submitted data: " + employee.toString());

        if (bindingResult.hasErrors()) {
            System.out.println("DEBUG: Create Validation Failed. Total Errors: " + bindingResult.getErrorCount());
            model.addAttribute("allRoles", roleService.getAllRoles());
            return "admin/employee/employee-form";
        }

        try {
            employeeService.createEmployee(employee);
            System.out.println("DEBUG: Employee created successfully. Redirecting to list.");
            redirectAttributes.addFlashAttribute("success", "New employee created successfully!");
            return "redirect:/admin/employees";
        } catch (IllegalArgumentException e) {
            System.out.println("ERROR: Business Validation Failed (e.g., duplicate email): " + e.getMessage());
            model.addAttribute("allRoles", roleService.getAllRoles());
            bindingResult.rejectValue("email", "error.employee", e.getMessage());
            return "admin/employee/employee-form";
        }
    }

    /**
     * Reder form to edit employee
     */
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable String id, Model model) {
        System.out.println("DEBUG: showEditForm() called. Employee ID from path: " + id);

        Employee employee = employeeService.getEmployeeById(id);

        if (employee == null) {
            System.out.println("ERROR: Employee not found with ID: " + id);
            return "redirect:/admin/employees";
        }

        System.out.println("DEBUG: Retrieved Employee for edit: " + employee.getEmail());

        model.addAttribute("employee", employee);
        model.addAttribute("allRoles", roleService.getAllRoles());
        model.addAttribute("activePage", "employees");

        return "admin/employee/employee-form";
    }

    /**
     * Process edited employee
     */
    @PostMapping("/{id}")
    public String updateEmployee(@PathVariable String id,
            @Validated(ValidationGroups.Update.class) @ModelAttribute("employee") Employee employeeDetails,
            BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes) {
        System.out.println("DEBUG: updateEmployee() called. Path ID: " + id);
        System.out.println("DEBUG: Submitted update data: " + employeeDetails.toString());

        if (bindingResult.hasErrors()) {
            model.addAttribute("allRoles", roleService.getAllRoles());
            model.addAttribute("activePage", "employees");
            employeeDetails.setEmployeeId(id);
            return "admin/employee/employee-form";
        }

        try {
            employeeService.updateEmployee(id, employeeDetails);
        } catch (IllegalArgumentException e) {
            String errorMessage = e.getMessage();
            model.addAttribute("allRoles", roleService.getAllRoles());
            if (errorMessage.toLowerCase().contains("password")) {
                bindingResult.rejectValue("password", "error.employee", errorMessage);
            } else {
                bindingResult.rejectValue("email", "error.employee", errorMessage);
            }
            return "admin/employee/employee-form";
        }

        System.out.println("DEBUG: Employee updated successfully. Redirecting to list.");

        redirectAttributes.addFlashAttribute("success", "Employee updated successfully!");
        return "redirect:/admin/employees";
    }

    /**
     * Delete employee
     */
    @PostMapping("/delete/{id}")
    public String deleteEmployee(@PathVariable @NotBlank(message = "Employee ID cannot be empty") String id) {
        System.out.println("DEBUG: deleteEmployee() called. ID to delete: " + id);
        employeeService.deleteEmployee(id);
        System.out.println("DEBUG: Employee deleted successfully. Redirecting to list.");
        return "redirect:/admin/employees";
    }
}