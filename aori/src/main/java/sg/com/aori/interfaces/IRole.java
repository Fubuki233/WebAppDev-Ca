package sg.com.aori.interfaces;

import java.util.List;
import java.util.Optional;

import sg.com.aori.model.Role;

/**
 * Interface for Role entity.
 *
 * @author xiaobo
 * @date 2025-10-07
 * @version 1.0
 */

public interface IRole {

    /**
     * Creates a new Role.
     * 
     * @param role The Role object to save.
     * @return The saved Role object.
     */
    Role createRole(Role role);

    /**
     * Retrieves a Role by its unique ID.
     * 
     * @param roleId The ID of the role.
     * @return An Optional containing the Role if found, or empty otherwise.
     */
    Optional<Role> getRoleById(String roleId);

    /**
     * Retrieves all existing Roles.
     * 
     * @return A list of all Roles.
     */
    List<Role> getAllRoles();

    /**
     * Updates an existing Role.
     * 
     * @param roleId      The ID of the role to update.
     * @param roleDetails The Role object containing updated data.
     * @return The updated Role object.
     */
    Role updateRole(String roleId, Role roleDetails);

    /**
     * Deletes a Role by its ID.
     * 
     * @param roleId The ID of the role to delete.
     */
    void deleteRole(String roleId);

    // Optional: Methods for managing role-permission relationships

    /**
     * Assigns a permission to a role.
     * 
     * @param roleId       The ID of the role.
     * @param permissionId The ID of the permission.
     * @return true if assignment was successful, false otherwise.
     */
    boolean assignPermissionToRole(String roleId, String permissionId);
}
