package sg.com.aori.service;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sg.com.aori.interfaces.IRole;
import sg.com.aori.model.Role;
import sg.com.aori.model.Permission;
import sg.com.aori.repository.RoleRepository;
import sg.com.aori.repository.PermissionRepository;

import java.util.*;

/**
 * Service for role entity.
 *
 * @author Xiaobo
 * @date 2025-10-08
 * @version 1.0
 */

@Service
public class RoleService implements IRole {

    @Autowired
    private final RoleRepository roleRepository;

    @Autowired
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

        existingRole.setRoleName(roleDetails.getRoleName());
        existingRole.setDescription(roleDetails.getDescription());

        return roleRepository.save(existingRole);
    }

    @Override
    @Transactional
    public void deleteRole(String roleId) {
        roleRepository.deleteById(roleId);
    }

    @Override
    @Transactional
    public boolean assignPermissionToRole(String roleId, String permissionId) {
        Role role = roleRepository.findById(roleId)
                .orElse(null);
        Permission permission = permissionRepository.findById(permissionId)
                .orElse(null);

        if (role == null || permission == null) {
            return false;
        }

        if (role.getPermissions().contains(permission)) {
            return true;
        }

        role.addPermission(permission);

        if (!permission.getRoles().contains(role)) {
            permission.getRoles().add(role);
        }

        roleRepository.save(role);

        return true;
    }
}