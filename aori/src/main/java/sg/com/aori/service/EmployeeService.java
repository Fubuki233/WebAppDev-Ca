package sg.com.aori.service;

import java.util.List;

import sg.com.aori.interfaces.IEmployee;
import sg.com.aori.model.Employee;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import sg.com.aori.repository.EmployeeRepository;

import java.util.NoSuchElementException;

/**
 * Service for Employee entity.
 *
 * @author xiaobo
 * @date 2025-10-07
 * @version 1.0
 */

@Service
public class EmployeeService implements IEmployee { // Note: Implement your interface here

    private final EmployeeRepository employeeRepository;
    // You'd typically inject a PasswordEncoder here for security

    public EmployeeService(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    @Override
    @Transactional
    public Employee createEmployee(Employee employee) {
        // employee.setPasswordHash(passwordEncoder.encode(employee.getPasswordHash()));

        // You may also want to set the initial status (e.g., EmployeeStatus.Active) if
        // not provided.

        return employeeRepository.save(employee);
    }

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

        // Assume the role update is handled via a separate business process
        if (employeeDetails.getRole() != null) {
            existingEmployee.setRole(employeeDetails.getRole());
        }

        return employeeRepository.save(existingEmployee);
    }

    @Override
    @Transactional
    public void deleteEmployee(String id) {
        employeeRepository.deleteById(id);
    }
}
