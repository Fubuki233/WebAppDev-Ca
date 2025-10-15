package sg.com.aori.repository;

import sg.com.aori.model.Permission;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

import io.lettuce.core.dynamic.annotation.Param;
import jakarta.transaction.Transactional;

/**
 * Repository interface for Permission entity.
 *
 * @author Xiaobo
 * @date 2025-10-07
 * @version 1.0
 */

@Repository
public interface PermissionRepository extends JpaRepository<Permission, String> {

    /**
     * Finds a Permission by its unique permissionName.
     */
    Optional<Permission> findByPermissionName(String permissionName);

    /**
     * Finds permissions whose name contains a specific keyword.
     */
    List<Permission> findByPermissionNameContainingIgnoreCase(String keyword);

    /**
     * Checks if a Permission with the given permissionName already exists.
     */
    boolean existsByPermissionName(String permissionName);

    /**
     * Custom update query to bulk update the description for a given permission ID.
     */
    @Modifying
    @Transactional
    @Query("UPDATE Permission p SET p.description = :newDescription WHERE p.permissionId = :permissionId")
    int updatePermissionDescription(
            @Param("permissionId") String permissionId,
            @Param("newDescription") String newDescription);

    /**
     * Custom Query Example: Find all Permissions associated with a specific Role
     * ID.
     * This query traverses the implicit many-to-many join table.
     */
    @Query("SELECT p FROM Permission p JOIN p.roles r WHERE r.roleId = :roleId")
    List<Permission> findPermissionsByRoleId(@Param("roleId") String roleId);

}
