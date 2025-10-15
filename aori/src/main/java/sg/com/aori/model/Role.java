package sg.com.aori.model;

import java.util.ArrayList;
import java.util.List;
import jakarta.persistence.*;

/**
 * @author Ying Chun
 * @date 2025-10-14
 * @version 1.1 - Removed autogeneration of roleId as it is not required, to
 *          match what is in the DB
 */

@Entity
@Table(name = "Role")
public class Role {
    @Id
    @Column(name = "role_id", length = 36)
    private String roleId;

    @Column(name = "role_name", length = 50, unique = true, nullable = false)
    private String roleName;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "role_permission", joinColumns = @JoinColumn(name = "role_id"), inverseJoinColumns = @JoinColumn(name = "permission_id"))
    private List<Permission> permissions = new ArrayList<>();

    public Role() {
    }

    public Role(String roleName, String description) {
        this.roleName = roleName;
        this.description = description;
    }

    public void addPermission(Permission permission) {
        this.permissions.add(permission);

    }

    public void removePermission(Permission permission) {
        this.permissions.remove(permission);
    }

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