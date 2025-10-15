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

    Optional<Role> findByRoleName(String roleName);

    boolean existsByRoleName(String roleName);

    List<Role> findByDescriptionContainingIgnoreCase(String keyword);
}