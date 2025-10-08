package sg.com.aori.service;

import java.util.List;

import sg.com.aori.model.Employee;


/**
 * Service for Employee entity.
 *
 * @author xiaobo
 * @date 2025-10-07
 * @version 1.0
 */

public interface EmployeeService {
    Employee createEmployee(Employee employee);

    Employee getEmployeeById(String id);

    List<Employee> getAllEmployees();

    Employee updateEmployee(String id, Employee employeeDetails);

    void deleteEmployee(String id);
}