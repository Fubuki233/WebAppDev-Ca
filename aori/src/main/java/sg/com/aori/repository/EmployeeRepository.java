package sg.com.aori.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jakarta.transaction.Transactional;
import sg.com.aori.model.Employee;

/**
 * Repository interface for Employee entity.
 *
 * @author xiaobo
 * @date 2025-10-07
 * @version 1.0
 */
public interface EmployeeRepository extends JpaRepository<Employee, String> {
    /**
     * Finds employees by their last name.
     * Derived Query: findByLastName
     */
    List<Employee> findByLastName(String lastName);

    /**
     * Finds employees by their email address (usually unique).
     * Derived Query: findByEmail
     */
    Optional<Employee> findByEmail(String email);

    /**
     * Custom Query: Finds all employees assigned to a specific role name.
     * This requires a JOIN to the Role entity.
     * Assumes Employee entity has a field 'role' which is a @ManyToOne relationship
     * to Role.
     */
    @Query("SELECT e FROM Employee e JOIN e.role r WHERE r.roleName = :roleName")
    List<Employee> findEmployeesByRoleName(@Param("roleName") String roleName);

    @Query("SELECT e FROM Employee e WHERE e.email = :email")
    Optional<Employee> findEmployeeByEmail(@Param("email") String email);

    /**
     * Custom Update Query: Bulk updates the email of a specific employee.
     */
    @Modifying
    @Transactional
    @Query("UPDATE Employee e SET e.email = :newEmail WHERE e.id = :employeeId")
    int updateEmployeeEmail(@Param("employeeId") String employeeId, @Param("newEmail") String newEmail);

    /**
     * Custom Update Query: Bulk sets a null role for all employees with a given
     * role ID.
     * Useful for unassigning roles before deleting a Role entity.
     */
    @Modifying
    @Transactional
    @Query("UPDATE Employee e SET e.role = NULL WHERE e.role.roleId = :roleId")
    int unassignRoleFromEmployees(@Param("roleId") String roleId);

}
