package sg.com.aori.interfaces;

import java.util.List;
import java.util.Optional;

import javax.management.relation.Role;

/**
 * Interface for Role entity.
 *
 * @author xiaobo
 * @date 2025-10-07
 * @version 1.0
 */

public interface IRole {
    
    Role createRole(Role role);
    Optional<Role> getRoleById(String roleId);
    List<Role> getAllRoles();
    Role updateRole(String roleId, Role roleDetails);
    void deleteRole(String roleId);

    // Optional: Methods for managing role-permission relationships
    boolean assignPermissionToRole(String roleId, String permissionId);
}

}
