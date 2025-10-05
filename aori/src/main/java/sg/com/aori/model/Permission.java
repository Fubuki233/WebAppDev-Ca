package sg.com.aori.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "permission")
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

}
