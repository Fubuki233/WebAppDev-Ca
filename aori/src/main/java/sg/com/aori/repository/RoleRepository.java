package sg.com.aori.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import sg.com.aori.model.Role;

/**
 * Repository interface for Role entity.
 *
 * @author Xiaobo
 * @date 2025-10-07
 * @version 1.0
 */
@Repository
public interface RoleRepository extends JpaRepository<Role, String> {
    /**
     * Finds a Role by its unique roleName.
     */
    Optional<Role> findByRoleName(String roleName);

    /**
     * Checks if a Role with the given roleName already exists.
     */
    boolean existsByRoleName(String roleName);

    /**
     * Finds roles whose description contains a specific keyword (case-insensitive).
     * This uses a Spring Data JPA keyword query method.
     */
    List<Role> findByDescriptionContainingIgnoreCase(String keyword);
}