package sg.com.aori.service;

import jakarta.persistence.EntityNotFoundException;
import java.util.Optional;

import org.springframework.stereotype.Service;

import sg.com.aori.dto.EmployeeProfileDTO;
import sg.com.aori.interfaces.IEmployeeProfile;
import sg.com.aori.repository.EmployeeRepository;
import sg.com.aori.model.Employee;

/**
 * Service Implementation for Employee Profile Management.
 *
 * @author Ying Chun
 * @date 2025-10-09
 * @version 1.0
 */

@Service
public class EmployeeProfileService implements IEmployeeProfile {

    private final EmployeeRepository employeeRepository;

    public EmployeeProfileService(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    @Override
    public Optional<Employee> getEmployeeByEmail(String email) {
        return employeeRepository.findByEmail(email);
    }

    @Override
    public Employee updateEmployeeProfile(String employeeId, EmployeeProfileDTO profileDto) {
        Employee existingEmployee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new EntityNotFoundException("Employee not found with id: " + employeeId));

        existingEmployee.setPhoneNumber(profileDto.getPhoneNumber());

        return employeeRepository.save(existingEmployee);
    }
}
