package sg.com.aori.interfaces;

import java.util.List;
import java.util.Optional;

import sg.com.aori.model.Permission;

/**
 * Interface for Permission entity.
 *
 * @author Xiaobo
 * @date 2025-10-07
 * @version 1.0
 */

public interface IPermission {
    /**
     * Creates a new Permission.
     * 
     * @param permission The Permission object to save.
     * @return The saved Permission object.
     */
    Permission createPermission(Permission permission);

    /**
     * Retrieves a Permission by its unique ID.
     * 
     * @param permissionId The ID of the permission.
     * @return An Optional containing the Permission if found, or empty otherwise.
     */
    Optional<Permission> getPermissionById(String permissionId);

    /**
     * Retrieves all existing Permissions.
     * 
     * @return A list of all Permissions.
     */
    List<Permission> getAllPermissions();

    /**
     * Updates an existing Permission.
     * 
     * @param permissionId      The ID of the permission to update.
     * @param permissionDetails The Permission object containing updated data.
     * @return The updated Permission object.
     */
    Permission updatePermission(String permissionId, Permission permissionDetails);

    /**
     * Deletes a Permission by its ID.
     * 
     * @param permissionId The ID of the permission to delete.
     */
    void deletePermission(String permissionId);
}