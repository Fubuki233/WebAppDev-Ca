package sg.com.aori.service;

import java.util.List;

import sg.com.aori.interfaces.IEmployee;
import sg.com.aori.model.Employee;
import sg.com.aori.model.Role;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import sg.com.aori.repository.EmployeeRepository;

import java.util.NoSuchElementException;
import java.util.Optional;

/**
 * Service for Employee entity.
 *
 * @author Xiaobo, Sun Rui
 * @date 2025-10-08
 * @version 1.1
 */

@Service
public class EmployeeService implements IEmployee {
    @Autowired
    private final EmployeeRepository employeeRepository;
    // You'd typically inject a PasswordEncoder here for security

    public EmployeeService(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    /**
     * Create a new employee after business validations.
     *
     * param: employee Employee payload from request (already passed Bean
     * Validation).
     * return: The persisted Employee.
     * throws: IllegalArgumentException if input is invalid.
     */
    @Override
    @Transactional
    public Employee createEmployee(Employee employee) {
        validateOnCreate(employee); // Business verification (uniqueness/password complexity/role basicity)

        // Set a default status for new employees
        employee.setStatus(Employee.EmployeeStatus.Active);

        return employeeRepository.save(employee);
    }

    /**
     * Retrieve an employee by id.
     *
     * param: id Employee primary key.
     * return: The found Employee.
     * throws: IllegalArgumentException if input is invalid.
     */ 
    @Override
    @Transactional(readOnly = true)
    public Employee getEmployeeById(String id) {
        return employeeRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Employee not found with ID: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Employee> getAllEmployees() {
        return employeeRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Employee> loginEmployee(String email, String rawPassword) {
        // 1. Find the Employee by email
        Optional<Employee> employeeOpt = employeeRepository.findByEmail(email);

        if (employeeOpt.isEmpty()) {
            return Optional.empty(); // User not found
        }

        Employee employee = employeeOpt.get();

        // 2. Validate the password

        // --- SECURE IMPLEMENTATION (Requires PasswordEncoder bean) ---
        // if (passwordEncoder.matches(rawPassword, employee.getPassword())) {
        // return Optional.of(employee);
        // }

        // --- INSECURE/SIMPLE IMPLEMENTATION (For demonstration only) ---
        if (employee.getPassword().equals(rawPassword)) {
            return Optional.of(employee);
        }

        return Optional.empty(); // Authentication failed
    }

    /**
     * Update an existing employee after business validations.
     *
     * param: id Employee primary key.
     * param: employeeDetails Fields to update.
     * return: The updated Employee.
     * throws: IllegalArgumentException if input is invalid.
     */
    @Override
    @Transactional
    public Employee updateEmployee(String id, Employee employeeDetails) {
        Employee existingEmployee = getEmployeeById(id);

        // Update fields. Note: Password hash is typically updated via a separate
        // endpoint/method.
        existingEmployee.setFirstName(employeeDetails.getFirstName());
        existingEmployee.setLastName(employeeDetails.getLastName());
        existingEmployee.setEmail(employeeDetails.getEmail());
        existingEmployee.setPhoneNumber(employeeDetails.getPhoneNumber());
        existingEmployee.setStatus(employeeDetails.getStatus());

        // Only update password if a new one is provided and is not empty.
        if (employeeDetails.getPassword() != null && !employeeDetails.getPassword().isEmpty()) {
            if (!isPasswordStrong(employeeDetails.getPassword())) {
                // This will be caught by the controller and shown to the user.
                throw new IllegalArgumentException("Password must include upper, lower, digit, and symbol");
            }
            existingEmployee.setPassword(employeeDetails.getPassword());
        }
        validateOnUpdate(existingEmployee);

        return existingEmployee;
    }

    @Override
    @Transactional
    public void deleteEmployee(String id) {
        employeeRepository.deleteById(id);
    }

    // ================== Business Verification==================
    /**
     * Business validation for creating an employee.
     *
     * param: employee Employee to validate.
     * return: void
     * throws: IllegalArgumentException if input is invalid.
     */
    private void validateOnCreate(Employee employee) {
        // 1) Unique Email
        if (employee.getEmail() != null && employeeRepository.existsByEmail(employee.getEmail())) {
            throw new IllegalArgumentException("Email already registered");
        }
        // 2) Complexity of Password (length is already set in Bean Validation,
        // character type is set here)
        if (!isPasswordStrong(employee.getPassword())) {
            throw new IllegalArgumentException("Password must include upper, lower, digit, and symbol");
        }
        // 3) Role basicity: at least not null and has roleId
        if (employee.getRole() == null || employee.getRole().getRoleId() == null) {
            throw new IllegalArgumentException("Invalid role");
        }
    }

    /**
     * Business validation for updating an employee.
     *
     * param: employee Employee (merged state) to validate.
     * return: void
     * throws: IllegalArgumentException if input is invalid.
     */
    private void validateOnUpdate(Employee employee) {
        // 1) Email address only (excluding yourself)
        if (employee.getEmail() != null &&
                employeeRepository.existsByEmailAndEmployeeIdNot(employee.getEmail(), employee.getEmployeeId())) {
            throw new IllegalArgumentException("Email already registered by another user");
        }
        // 2) Role basics
        if (employee.getRole() == null || employee.getRole().getRoleId() == null) {
            throw new IllegalArgumentException("Invalid role");
        }
    }

    /**
     * Check password complexity: must contain upper, lower, digit, and symbol;
     * length ≥ 8.
     *
     * param: pwd Raw password string.
     * return: true if strong enough; otherwise false.
     * throws: IllegalArgumentException if input is invalid.
     */
    private boolean isPasswordStrong(String pwd) {
        if (pwd == null)
            return false;
        boolean hasUpper = pwd.matches(".*[A-Z].*");
        boolean hasLower = pwd.matches(".*[a-z].*");
        boolean hasDigit = pwd.matches(".*\\d.*");
        boolean hasSymbol = pwd.matches(".*[^A-Za-z0-9].*");
        return hasUpper && hasLower && hasDigit && hasSymbol && pwd.length() >= 8;
    }

    // -----------------------------------------------------------
    // Implementation of the method called by the Interceptor
    // -----------------------------------------------------------
    public Optional<Employee> findEmployeeWithPermissions(String employeeId) {

        return employeeRepository.findEmployeeWithRoleAndPermissions(employeeId);
    }

    // -----------------------------------------------------------
    // Implementation of the core permission check logic (SIMPLIFIED)
    // -----------------------------------------------------------
    /**
     * Checks if the Employee's single assigned Role grants the required permission.
     */
    public boolean hasPermission(Employee employee, String requiredPermissionNode) {

        // Safety check: Employee must exist and must have a Role assigned
        if (employee == null || employee.getRole() == null) {
            return false;
        }

        Role employeeRole = employee.getRole();

        // Check if the permission node is granted by this single role
        if (employeeRole.getPermissions() != null) {

            // Iterate through all Permissions of the single role
            return employeeRole.getPermissions().stream()
                    .anyMatch(permission -> permission.getPermissionName().equals(requiredPermissionNode));
        }

        return false; // Role exists but has no permissions defined.
    }

}
