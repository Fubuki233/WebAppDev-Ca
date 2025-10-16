package sg.com.aori.service;

import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import sg.com.aori.repository.EmployeeRepository;
import sg.com.aori.interfaces.IEmployee;
import sg.com.aori.model.Employee;
import sg.com.aori.model.Role;

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

    public EmployeeService(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    /**
     * Create a new employee after business validations.
     *
     * @param employee Employee payload from request (already passed Bean
     *                 Validation).
     * @return The persisted Employee.
     * @throws IllegalArgumentException if input is invalid.
     */
    @Override
    @Transactional
    public Employee createEmployee(Employee employee) {
        validateOnCreate(employee);

        employee.setStatus(Employee.EmployeeStatus.Active);

        return employeeRepository.save(employee);
    }

    /**
     * Retrieve an employee by id.
     *
     * @param id Employee primary key.
     * @return The found Employee.
     * @throws IllegalArgumentException if input is invalid.
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
        Optional<Employee> employeeOpt = employeeRepository.findByEmail(email);

        if (employeeOpt.isEmpty()) {
            return Optional.empty();
        }

        Employee employee = employeeOpt.get();

        if (employee.getPassword().equals(rawPassword)) {
            return Optional.of(employee);
        }

        return Optional.empty();
    }

    /**
     * Update an existing employee after business validations.
     *
     * @param id              Employee primary key.
     * @param employeeDetails Fields to update.
     * @return The updated Employee.
     * @throws IllegalArgumentException if input is invalid.
     */
    @Override
    @Transactional
    public Employee updateEmployee(String id, Employee employeeDetails) {
        Employee existingEmployee = getEmployeeById(id);

        existingEmployee.setFirstName(employeeDetails.getFirstName());
        existingEmployee.setLastName(employeeDetails.getLastName());
        existingEmployee.setEmail(employeeDetails.getEmail());
        existingEmployee.setPhoneNumber(employeeDetails.getPhoneNumber());
        existingEmployee.setStatus(employeeDetails.getStatus());

        if (employeeDetails.getPassword() != null && !employeeDetails.getPassword().isEmpty()) {
            if (!isPasswordStrong(employeeDetails.getPassword())) {
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

    /**
     * Business validation for creating an employee.
     *
     * @param employee Employee to validate.
     * @return void
     * @throws IllegalArgumentException if input is invalid.
     */
    private void validateOnCreate(Employee employee) {
        if (employee.getEmail() != null && employeeRepository.existsByEmail(employee.getEmail())) {
            throw new IllegalArgumentException("Email already registered");
        }

        if (!isPasswordStrong(employee.getPassword())) {
            throw new IllegalArgumentException("Password must include upper, lower, digit, and symbol");
        }
        if (employee.getRole() == null || employee.getRole().getRoleId() == null) {
            throw new IllegalArgumentException("Invalid role");
        }
    }

    /**
     * Business validation for updating an employee.
     *
     * @param employee Employee (merged state) to validate.
     * @return void
     * @throws IllegalArgumentException if input is invalid.
     */
    private void validateOnUpdate(Employee employee) {
        if (employee.getEmail() != null &&
                employeeRepository.existsByEmailAndEmployeeIdNot(employee.getEmail(), employee.getEmployeeId())) {
            throw new IllegalArgumentException("Email already registered by another user");
        }

        if (employee.getRole() == null || employee.getRole().getRoleId() == null) {
            throw new IllegalArgumentException("Invalid role");
        }
    }

    /**
     * Check password complexity: must contain upper, lower, digit, and symbol;
     * length ≥ 8.
     *
     * @param pwd Raw password string.
     * @return true if strong enough; otherwise false.
     * @throws IllegalArgumentException if input is invalid.
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

    public Optional<Employee> findEmployeeWithPermissions(String employeeId) {
        return employeeRepository.findEmployeeWithRoleAndPermissions(employeeId);
    }

    /**
     * Checks if the Employee's single assigned Role grants the required permission.
     */
    public boolean hasPermission(Employee employee, String requiredPermissionNode) {

        if (employee == null || employee.getRole() == null) {
            return false;
        }

        Role employeeRole = employee.getRole();

        if (employeeRole.getPermissions() != null) {
            return employeeRole.getPermissions().stream()
                    .anyMatch(permission -> permission.getPermissionName().equals(requiredPermissionNode));
        }

        return false;
    }

}
