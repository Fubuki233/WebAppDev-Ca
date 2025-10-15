package sg.com.aori.interfaces;

import sg.com.aori.model.Employee;
import java.util.List;
import java.util.Optional;

/**
 * Interface for Employee entity.
 *
 * @author Xiaobo
 * @date 2025-10-07
 * @version 1.0
 */

public interface IEmployee {

    /**
     * Creates a new Employee record in the system.
     * 
     * @param employee The Employee object to save.
     * @return The saved Employee object with generated ID and timestamps.
     */
    Employee createEmployee(Employee employee);

    /**
     * Retrieves an Employee by their unique ID.
     * 
     * @param id The employeeId.
     * @return The Employee object if found.
     * @throws java.util.NoSuchElementException if the employee is not found.
     */
    Employee getEmployeeById(String id);

    /**
     * Retrieves all Employee records.
     * 
     * @return A list of all Employees.
     */
    List<Employee> getAllEmployees();

    /**
     * Updates the details of an existing Employee.
     * 
     * @param id              The employeeId of the employee to update.
     * @param employeeDetails The Employee object containing updated data.
     * @return The updated Employee object.
     * @throws java.util.NoSuchElementException if the employee is not found.
     */
    Employee updateEmployee(String id, Employee employeeDetails);

    /**
     * Deletes an Employee record by ID.
     * 
     * @param id The employeeId of the employee to delete.
     */
    void deleteEmployee(String id);

    // Optional method signature if you wanted the service to return Optional
    // instead of throwing an exception:
    // Optional<Employee> findEmployeeById(String id);
    Optional<Employee> findEmployeeWithPermissions(String employeeId);

    /**
     * Authenticates an Employee by email and password.
     * * @param email The employee's email.
     * 
     * @param password The raw password.
     * @return An Optional containing the authenticated Employee, or empty if login
     *         fails.
     */
    Optional<Employee> loginEmployee(String email, String password);
}