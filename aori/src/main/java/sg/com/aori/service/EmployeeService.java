package sg.com.aori.service;

import java.util.List;

import sg.com.aori.model.Employee;

public interface EmployeeService {
    Employee createEmployee(Employee employee);

    Employee getEmployeeById(String id);

    List<Employee> getAllEmployees();

    Employee updateEmployee(String id, Employee employeeDetails);

    void deleteEmployee(String id);
}