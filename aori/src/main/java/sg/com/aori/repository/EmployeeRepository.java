/**
 * Repository interface for Employee entity.
 *
 * @author Xiaobo, Sun Rui
 * @date 2025-10-08
 * @version 1.1
 */

package sg.com.aori.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jakarta.transaction.Transactional;
import sg.com.aori.model.Employee;

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

    /**
     * Fetches an Employee and eagerly loads their single assigned Role and the
     * Permissions associated with that Role.
     * Structure: Employee -> Role -> Permissions
     */
    @Query("SELECT e FROM Employee e " +
            "JOIN FETCH e.role r " + // Eagerly fetch the single Role (e.role)
            "LEFT JOIN FETCH r.permissions p " + // Eagerly fetch the Permissions of that Role
            "WHERE e.employeeId = :employeeId")
    Optional<Employee> findEmployeeWithRoleAndPermissions(@Param("employeeId") String employeeId);

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

    boolean existsByEmail(String email); // Check for duplicates when creating

    boolean existsByEmailAndEmployeeIdNot(String email, String employeeId); // Exclude yourself when checking if the
                                                                            // mailbox is occupied
}
