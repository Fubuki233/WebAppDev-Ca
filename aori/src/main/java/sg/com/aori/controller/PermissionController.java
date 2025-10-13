package sg.com.aori.controller;

/**
 * Controller for Permission entity.
 *
 * @author xiaobo，Sun RUi
 * @date 2025-10-07
 * @version 1.1 - need to change some paths validaiton has been added
 */

import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import sg.com.aori.interfaces.IPermission;
import sg.com.aori.model.Permission;

@RestController
@RequestMapping("/api/permissions")
@Validated
public class PermissionController {

    private final IPermission permissionService;

    // Dependency Injection of the Service Interface
    public PermissionController(IPermission permissionService) {
        this.permissionService = permissionService;
    }

    /**
     * POST /api/permissions : Creates a new Permission.
     */
    @PostMapping
    public ResponseEntity<Permission> createPermission(@Valid @RequestBody Permission permission) {
        Permission createdPermission = permissionService.createPermission(permission);
        return new ResponseEntity<>(createdPermission, HttpStatus.CREATED);
    }

    /**
     * GET /api/permissions : Retrieves all Permissions.
     */
    @GetMapping
    public ResponseEntity<List<Permission>> getAllPermissions() {
        List<Permission> permissions = permissionService.getAllPermissions();
        return ResponseEntity.ok(permissions);
    }

    /**
     * GET /api/permissions/{id} : Retrieves a Permission by ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Permission> getPermissionById(@PathVariable @NotBlank(message = "Permission ID can not be empty") String id) {
        // Using Optional from the service and handling not found case
        Permission permission = permissionService.getPermissionById(id)
                .orElseThrow(() -> new NoSuchElementException("Permission not found with ID: " + id));
        return ResponseEntity.ok(permission);
    }

    /**
     * PUT /api/permissions/{id} : Updates an existing Permission.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Permission> updatePermission(@PathVariable @NotBlank(message = "Permission ID cannot be empty") String id,
            @Valid @RequestBody Permission permissionDetails) {
        try {
            Permission updatedPermission = permissionService.updatePermission(id, permissionDetails);
            return ResponseEntity.ok(updatedPermission);
        } catch (NoSuchElementException e) {
            // Handle the case where the ID doesn't exist (e.g., return 404)
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * DELETE /api/permissions/{id} : Deletes a Permission by ID.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePermission(@PathVariable @NotBlank(message = "Permission ID cannot be empty") String id) {
        permissionService.deletePermission(id);
        return ResponseEntity.noContent().build();
    }
}
