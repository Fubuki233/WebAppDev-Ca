package sg.com.aori.repository;

import sg.com.aori.model.Permission;

import java.util.List;
import java.util.Optional;
import jakarta.transaction.Transactional;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

import io.lettuce.core.dynamic.annotation.Param;

/**
 * Repository interface for Permission entity.
 *
 * @author Xiaobo
 * @date 2025-10-07
 * @version 1.0
 */

@Repository
public interface PermissionRepository extends JpaRepository<Permission, String> {

    Optional<Permission> findByPermissionName(String permissionName);

    List<Permission> findByPermissionNameContainingIgnoreCase(String keyword);

    boolean existsByPermissionName(String permissionName);

    @Modifying
    @Transactional
    @Query("UPDATE Permission p SET p.description = :newDescription WHERE p.permissionId = :permissionId")
    int updatePermissionDescription(
            @Param("permissionId") String permissionId,
            @Param("newDescription") String newDescription);

    @Query("SELECT p FROM Permission p JOIN p.roles r WHERE r.roleId = :roleId")
    List<Permission> findPermissionsByRoleId(@Param("roleId") String roleId);

}
