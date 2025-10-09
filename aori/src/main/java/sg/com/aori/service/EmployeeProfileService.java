/**
 * Service Implementation for Employee Profile Management.
 *
 * @author Ying Chun
 * @date 2025-10-09
 * @version 1.0
 */

package sg.com.aori.service;

import org.springframework.stereotype.Service;
import jakarta.persistence.EntityNotFoundException;
import java.util.Optional;

import sg.com.aori.interfaces.IEmployeeProfile;
import sg.com.aori.repository.EmployeeRepository;
import sg.com.aori.model.Employee;

@Service
public class EmployeeProfileService implements IEmployeeProfile{

    private final EmployeeRepository employeeRepository;

    public EmployeeProfileService(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    @Override
    public Optional<Employee> getEmployeeByEmail(String email){
        return employeeRepository.findByEmail(email);
    }

    @Override
    public Employee updateEmployeeProfile(String employeeId, Employee employeeDetails) {
        // 1. Locate the existing employee in the database
        Employee existingEmployee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new EntityNotFoundException("Employee not found with id: " + employeeId));

        // Update only permitted fields that can be changed from the profile page
        // Permitted fields: firstName, lastName, phoneNumber
        // Non-permitted fields: email, role, department, etc.
        existingEmployee.setFirstName(employeeDetails.getFirstName());
        existingEmployee.setLastName(employeeDetails.getLastName());
        existingEmployee.setPhoneNumber(employeeDetails.getPhoneNumber());

        // Save the updated employee back to the database
        return employeeRepository.save(existingEmployee);
    }
}
