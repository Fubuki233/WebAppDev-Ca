/*
 * Updated by Ying Chun on 14 Oct 2025. Removed autogeneration of roleId as it is not required, to match what is in the DB.
 */

package sg.com.aori.model;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "Role")
public class Role {
    @Id
    @Column(name = "role_id", length = 36)
    private String roleId;

    // Maps to role_name VARCHAR(50) UNIQUE NOT NULL
    @Column(name = "role_name", length = 50, unique = true, nullable = false)
    private String roleName;

    // Maps to description TEXT
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    // many-to-many with permission
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "role_permission", // Name of the join table (Role Permission in your SQL)
            joinColumns = @JoinColumn(name = "role_id"), // Foreign key column for THIS entity (Role)
            inverseJoinColumns = @JoinColumn(name = "permission_id") // Foreign key column for the TARGET entity
    )
    private List<Permission> permissions = new ArrayList<>();

    // --- Constructors ---
    public Role() {
    }

    public Role(String roleName, String description) {
        this.roleName = roleName;
        this.description = description;
    }

    public void addPermission(Permission permission) {
        this.permissions.add(permission);

    }

    // remove permission
    public void removePermission(Permission permission) {
        this.permissions.remove(permission);
        // permission.getRoles().remove(this);
        // requires a setter/remover in Permission
    }

    // setters and getters
    public void setPermissions(List<Permission> permissions) {
        this.permissions = permissions;
    }

    public List<Permission> getPermissions() {
        return permissions;
    }

    public String getRoleId() {
        return roleId;
    }

    public void setRoleId(String roleId) {
        this.roleId = roleId;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "{" +
                " roleId='" + getRoleId() + "'" +
                ", roleName='" + getRoleName() + "'" +
                ", description='" + getDescription() + "'" +
                "}";
    }

}