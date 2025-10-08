package sg.com.aori.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import sg.com.aori.model.Employee;

/**
 * Repository interface for Employee entity.
 *
 * @author xiaobo
 * @date 2025-10-07
 * @version 1.0
 */
public interface EmployeeRepository extends JpaRepository<Employee, String> {
    @Query("SELECT e FROM Employee e WHERE e.email = :email")
    Optional<Employee> findEmployeeByEmail(@Param("email") String email);
}
