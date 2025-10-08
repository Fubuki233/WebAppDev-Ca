package sg.com.aori.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sg.com.aori.interfaces.IRole;
import sg.com.aori.model.Role;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Map;

/**
 * Controller for Role entity.
 *
 * @author xiaobo
 * @date 2025-10-07
 * @version 1.0 - need to change some paths
 */

@RestController
@RequestMapping("/api/roles")
public class RoleController {

    private final IRole roleService;

    // Dependency Injection of the Service Interface
    public RoleController(IRole roleService) {
        this.roleService = roleService;
    }

    /**
     * POST /api/roles : Creates a new Role.
     */
    @PostMapping
    public ResponseEntity<Role> createRole(@RequestBody Role role) {
        Role createdRole = roleService.createRole(role);
        return new ResponseEntity<>(createdRole, HttpStatus.CREATED);
    }

    /**
     * GET /api/roles : Retrieves all Roles.
     */
    @GetMapping
    public ResponseEntity<List<Role>> getAllRoles() {
        List<Role> roles = roleService.getAllRoles();
        return ResponseEntity.ok(roles);
    }

    /**
     * GET /api/roles/{id} : Retrieves a Role by ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Role> getRoleById(@PathVariable String id) {
        Role role = roleService.getRoleById(id)
                .orElseThrow(() -> new NoSuchElementException("Role not found with ID: " + id));
        return ResponseEntity.ok(role);
    }

    /**
     * PUT /api/roles/{id} : Updates an existing Role.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Role> updateRole(@PathVariable String id, @RequestBody Role roleDetails) {
        try {
            Role updatedRole = roleService.updateRole(id, roleDetails);
            return ResponseEntity.ok(updatedRole);
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * DELETE /api/roles/{id} : Deletes a Role by ID.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRole(@PathVariable String id) {
        roleService.deleteRole(id);
        return ResponseEntity.noContent().build();
    }

    // --- Role-Permission Management Endpoint ---

    /**
     * POST /api/roles/{roleId}/permissions : Assigns a permission to a role.
     */
    @PostMapping("/{roleId}/permissions")
    public ResponseEntity<Void> assignPermission(
            @PathVariable String roleId,
            @RequestBody Map<String, String> requestBody) {

        String permissionId = requestBody.get("permissionId");

        if (permissionId == null) {
            return ResponseEntity.badRequest().build();
        }

        boolean success = roleService.assignPermissionToRole(roleId, permissionId);

        if (success) {
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            // Role or Permission not found, or assignment failed
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
}