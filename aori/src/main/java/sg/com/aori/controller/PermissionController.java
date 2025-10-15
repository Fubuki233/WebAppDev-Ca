package sg.com.aori.controller;

import sg.com.aori.interfaces.IPermission;
import sg.com.aori.model.Permission;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Controller for Permission entity.
 *
 * @author Xiaobo，Sun Rui
 * @date 2025-10-07
 * @version 1.0
 * @version 1.1 - ATTENTION: Need to change some paths
 * 
 * @author Sun Rui
 * @version 1.2 - Validaiton has been added
 */

@RestController
@RequestMapping("/api/permissions")
@Validated
public class PermissionController {

    private final IPermission permissionService;

    public PermissionController(IPermission permissionService) {
        this.permissionService = permissionService;
    }

    /**
     * Creates a new Permission.
     */
    @PostMapping
    public ResponseEntity<Permission> createPermission(@Valid @RequestBody Permission permission) {
        Permission createdPermission = permissionService.createPermission(permission);
        return new ResponseEntity<>(createdPermission, HttpStatus.CREATED);
    }

    /**
     * Retrieves all Permissions.
     */
    @GetMapping
    public ResponseEntity<List<Permission>> getAllPermissions() {
        List<Permission> permissions = permissionService.getAllPermissions();
        return ResponseEntity.ok(permissions);
    }

    /**
     * Retrieves a Permission by ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Permission> getPermissionById(
            @PathVariable @NotBlank(message = "Permission ID can not be empty") String id) {
        Permission permission = permissionService.getPermissionById(id)
                .orElseThrow(() -> new NoSuchElementException("Permission not found with ID: " + id));
        return ResponseEntity.ok(permission);
    }

    /**
     * Updates an existing Permission.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Permission> updatePermission(
            @PathVariable @NotBlank(message = "Permission ID cannot be empty") String id,
            @Valid @RequestBody Permission permissionDetails) {
        try {
            Permission updatedPermission = permissionService.updatePermission(id, permissionDetails);
            return ResponseEntity.ok(updatedPermission);
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Deletes a Permission by ID.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePermission(
            @PathVariable @NotBlank(message = "Permission ID cannot be empty") String id) {
        permissionService.deletePermission(id);
        return ResponseEntity.noContent().build();
    }
}
