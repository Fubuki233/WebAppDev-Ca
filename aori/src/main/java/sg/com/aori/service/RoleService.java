package sg.com.aori.service;

/**
 * Service for role entity.
 *
 * @author xiaobo
 * @date 2025-10-08
 * @version 1.0
 */

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sg.com.aori.interfaces.IRole;
import sg.com.aori.model.Role;
import sg.com.aori.model.Permission;
import sg.com.aori.repository.RoleRepository;
import sg.com.aori.repository.PermissionRepository;

import java.util.List;
import java.util.Optional;
import java.util.NoSuchElementException;

@Service
public class RoleService implements IRole {

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;

    public RoleService(
            RoleRepository roleRepository,
            PermissionRepository permissionRepository) {
        this.roleRepository = roleRepository;
        this.permissionRepository = permissionRepository;

    }

    @Override
    @Transactional
    public Role createRole(Role role) {
        return roleRepository.save(role);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Role> getRoleById(String roleId) {
        return roleRepository.findById(roleId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }

    @Override
    @Transactional
    public Role updateRole(String roleId, Role roleDetails) {
        Role existingRole = roleRepository.findById(roleId)
                .orElseThrow(() -> new NoSuchElementException("Role not found with ID: " + roleId));

        // Update fields
        existingRole.setRoleName(roleDetails.getRoleName());
        existingRole.setDescription(roleDetails.getDescription());

        return roleRepository.save(existingRole);
    }

    @Override
    @Transactional
    public void deleteRole(String roleId) {
        // The deletion of associated entries in 'role_permission' is
        // typically handled automatically by JPA's cascading rules if set up,
        // or implicitly when the owning side (Role) is deleted.
        roleRepository.deleteById(roleId);
    }

    // --- Implementation for Relationship Management ---

    @Override
    @Transactional
    public boolean assignPermissionToRole(String roleId, String permissionId) {
        // 1. Fetch both entities
        Role role = roleRepository.findById(roleId)
                .orElse(null);
        Permission permission = permissionRepository.findById(permissionId)
                .orElse(null);

        if (role == null || permission == null) {
            return false; // Role or Permission doesn't exist
        }

        // 2. Manage the relationship on the owning side (Role)
        // Check if the permission is already assigned to avoid duplicates
        if (role.getPermissions().contains(permission)) {
            return true; // Already assigned, consider it a success
        }

        // Add the permission to the collection
        role.addPermission(permission);

        // IMPORTANT: For bidirectional consistency (optional but recommended)
        // You should also update the inverse side (Permission).
        // If Role does not have a helper method, you can use the getter.
        if (!permission.getRoles().contains(role)) {
            permission.getRoles().add(role);
        }

        // 3. Save the owning entity (Role).
        // JPA will automatically manage the insertion into the 'role_permission' join
        // table.
        roleRepository.save(role);

        return true;
    }
}