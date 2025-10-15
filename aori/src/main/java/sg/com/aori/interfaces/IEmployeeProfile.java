package sg.com.aori.interfaces;

import java.util.Optional;

import sg.com.aori.model.Employee;
import sg.com.aori.dto.EmployeeProfileDTO;

/**
 * Service Interface for employee to view and update profile details.
 *
 * @author Ying Chun
 * @date 2025-10-09
 * @version 1.0
 * 
 * @date 2025-10-12
 * @version 2.0 - Added DTO
 */

public interface IEmployeeProfile {

    /**
     * Retrieves an employee by their email address.
     *
     * @param email The email of the employee.
     * @return An Optional containing the Employee.
     */
    Optional<Employee> getEmployeeByEmail(String email);

    /**
     * Updates the profile information of an existing employee.
     *
     * @param employeeId      The ID of the employee to update.
     * @param employeeDetails An Employee object containing the new details.
     * @return The updated Employee object.
     */
    Employee updateEmployeeProfile(String employeeId, EmployeeProfileDTO profileDto);
}
