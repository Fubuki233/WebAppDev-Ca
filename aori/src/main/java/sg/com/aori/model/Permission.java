package sg.com.aori.model;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;

@Entity
@Table(name = "Permission")
public class Permission {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "permission_id", length = 36)
    // permission_id VARCHAR(36) PRIMARY KEY DEFAULT (UUID()),
    private String permissionId;

    // permission_name VARCHAR(100) UNIQUE NOT NULL,
    @Column(name = "permission_name", length = 100, nullable = false)
    private String permissionName;

    // description TEXT
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    // many-to-many with Role
    @ManyToMany(mappedBy = "permissions")
    private List<Role> roles = new ArrayList<>();

    public List<Role> getRoles() {
        return roles;
    }

    // Setter for roles.
    public void setRoles(List<Role> roles) {
        this.roles = roles;
    }

    public Permission() {
    }

    public Permission(String permissionName, String description) {
        this.permissionName = permissionName;
        this.description = description;
    }

    public String getPermissionId() {
        return permissionId;
    }

    public void setPermissionId(String permissionId) {
        this.permissionId = permissionId;
    }

    public String getPermissionName() {
        return permissionName;
    }

    public void setPermissionName(String permissionName) {
        this.permissionName = permissionName;
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
                " permissionId='" + getPermissionId() + "'" +
                ", permissionName='" + getPermissionName() + "'" +
                ", description='" + getDescription() + "'" +
                "}";
    }
}
