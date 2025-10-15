package sg.com.aori.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import sg.com.aori.interfaces.IRole;
import sg.com.aori.model.Role;

import java.util.*;

/**
 * Controller for Role entity.
 *
 * @author Xiaobo
 * @date 2025-10-07
 * @version 1.0
 */

@RestController
@RequestMapping("/api/roles")
public class RoleController {

    private final IRole roleService;

    public RoleController(IRole roleService) {
        this.roleService = roleService;
    }

    /**
     * Creates a new Role.
     */
    @PostMapping
    public ResponseEntity<Role> createRole(@RequestBody Role role) {
        Role createdRole = roleService.createRole(role);
        return new ResponseEntity<>(createdRole, HttpStatus.CREATED);
    }

    /**
     * Retrieves all Roles.
     */
    @GetMapping
    public ResponseEntity<List<Role>> getAllRoles() {
        List<Role> roles = roleService.getAllRoles();
        return ResponseEntity.ok(roles);
    }

    /**
     * Retrieves a Role by ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Role> getRoleById(@PathVariable String id) {
        Role role = roleService.getRoleById(id)
                .orElseThrow(() -> new NoSuchElementException("Role not found with ID: " + id));
        return ResponseEntity.ok(role);
    }

    /**
     * Updates an existing Role.
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
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
}