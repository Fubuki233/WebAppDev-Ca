package sg.com.aori.service;

import org.springframework.beans.factory.annotation.Autowired;
/**
 * Service for Permission entity.
 *
 * @author xiaobo
 * @date 2025-10-07
 * @version 1.0
 */
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sg.com.aori.interfaces.IPermission;
import sg.com.aori.model.Permission;
import sg.com.aori.repository.PermissionRepository;

import java.util.List;
import java.util.Optional;
import java.util.NoSuchElementException;

@Service
public class PermissionService implements IPermission {
    @Autowired
    private final PermissionRepository permissionRepository;

    public PermissionService(PermissionRepository permissionRepository) {
        this.permissionRepository = permissionRepository;
    }

    @Override
    @Transactional
    public Permission createPermission(Permission permission) {
        // Business logic before save (e.g., validation)
        return permissionRepository.save(permission);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Permission> getPermissionById(String permissionId) {
        return permissionRepository.findById(permissionId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Permission> getAllPermissions() {
        return permissionRepository.findAll();
    }

    @Override
    @Transactional
    public Permission updatePermission(String permissionId, Permission permissionDetails) {
        Permission existingPermission = permissionRepository.findById(permissionId)
                .orElseThrow(() -> new NoSuchElementException("Permission not found with ID: " + permissionId));

        // Update fields
        existingPermission.setPermissionName(permissionDetails.getPermissionName());
        existingPermission.setDescription(permissionDetails.getDescription());

        return permissionRepository.save(existingPermission);
    }

    @Override
    @Transactional
    public void deletePermission(String permissionId) {
        permissionRepository.deleteById(permissionId);
    }
}
