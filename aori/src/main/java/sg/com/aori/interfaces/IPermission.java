package sg.com.aori.interfaces;

import java.security.Permission;
import java.util.List;
import java.util.Optional;

/**
 * Interface for Permission entity.
 *
 * @author xiaobo
 * @date 2025-10-07
 * @version 1.0
 */

public interface IPermission {
    Permission createPermission(Permission permission);

    Optional<Permission> getPermissionById(String permissionId);

    List<Permission> getAllPermissions();

    Permission updatePermission(String permissionId, Permission permissionDetails);

    void deletePermission(String permissionId);
}
