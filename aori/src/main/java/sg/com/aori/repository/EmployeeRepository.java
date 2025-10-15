package sg.com.aori.repository;

import java.util.List;
import java.util.Optional;
import jakarta.transaction.Transactional;

import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import sg.com.aori.model.Employee;

/**
 * Repository interface for Employee entity.
 *
 * @author Xiaobo, Sun Rui
 * @version 1.0
 * 
 * @author Sun Rui
 * @date 2025-10-08
 * @version 1.1
 */

public interface EmployeeRepository extends JpaRepository<Employee, String> {

        List<Employee> findByLastName(String lastName);

        Optional<Employee> findByEmail(String email);

        @Query("SELECT e FROM Employee e JOIN e.role r WHERE r.roleName = :roleName")
        List<Employee> findEmployeesByRoleName(@Param("roleName") String roleName);

        @Query("SELECT e FROM Employee e " +
                        "JOIN FETCH e.role r " +
                        "LEFT JOIN FETCH r.permissions p " +
                        "WHERE e.employeeId = :employeeId")
        Optional<Employee> findEmployeeWithRoleAndPermissions(@Param("employeeId") String employeeId);

        @Query("SELECT e FROM Employee e WHERE e.email = :email")
        Optional<Employee> findEmployeeByEmail(@Param("email") String email);

        @Modifying
        @Transactional
        @Query("UPDATE Employee e SET e.email = :newEmail WHERE e.id = :employeeId")
        int updateEmployeeEmail(@Param("employeeId") String employeeId, @Param("newEmail") String newEmail);

        @Modifying
        @Transactional
        @Query("UPDATE Employee e SET e.role = NULL WHERE e.role.roleId = :roleId")
        int unassignRoleFromEmployees(@Param("roleId") String roleId);

        boolean existsByEmail(String email);

        boolean existsByEmailAndEmployeeIdNot(String email, String employeeId);

}
