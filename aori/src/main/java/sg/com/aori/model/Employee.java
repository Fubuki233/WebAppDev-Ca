/**
 * Entity of Employee.
 *
 * @author YunHe / SunRui
 * @date 2025-10-08
 * @version 1.1
 */

package sg.com.aori.model;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.*;
import org.hibernate.validator.constraints.Length;

@Entity
@Table(name = "Employee")
public class Employee {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    // employee_id VARCHAR(36) PRIMARY KEY DEFAULT (UUID())
    @Column(name = "employee_id", length = 36)
    private String employeeId;

    // first_name VARCHAR(50) NOT NULL,
    @NotBlank
    @Pattern(regexp = "^[A-Za-z]+$", message = "First name must contain alphabets only")
    @Length(max = 50)
    @Column(name = "first_name", length = 50, nullable = false)
    private String firstName;

    // last_name VARCHAR(50) NOT NULL,
    @NotBlank
    @Pattern(regexp = "^[A-Za-z]+$", message = "Last name must contain alphabets only")
    @Length(max = 50)
    @Column(name = "last_name", length = 50, nullable = false)
    private String lastName;

    // email VARCHAR(255) UNIQUE NOT NULL,
    @NotBlank
    @Email
    @Length(max = 255)
    @Column(name = "email", unique = true, nullable = false)
    private String email;

    // password_hash VARCHAR(255) NOT NULL,
    @NotBlank(message = "Password is required")
    @Length(min = 8, max = 255)
    @Column(name = "password", nullable = false)
    private String password;

    // phone_number VARCHAR(15),
    @Pattern(regexp = "^\\+?[1-9]\\d{1,14}$", message = "Phone must follow E.164")
    @Length(max = 15)
    @Column(name = "phone_number", length = 15)
    private String phoneNumber;

    // role_id VARCHAR(36) NOT NULL,
    // Many-to-one relationship with Role entity
    @NotNull(message = "Role is required")
    @ManyToOne
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;

    // status ENUM('Active', 'Inactive', 'Suspended') NOT NULL,
    @NotNull(message = "Status is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private EmployeeStatus status;

    // created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    // updated_at TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    @UpdateTimestamp
    @Column(name = "updated_at", updatable = true)
    private LocalDateTime updatedAt;

    public enum EmployeeStatus {
        Active,
        Inactive,
        Suspended
    }

    // Constructors

    public Employee() {
    }

    public Employee(String firstName, String lastName, String email, String passwordHash,
            String phoneNumber, Role role, EmployeeStatus status) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.role = role;
        this.status = status;
    }

    // Getters and Setters

    public String getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public EmployeeStatus getStatus() {
        return status;
    }

    public void setStatus(EmployeeStatus status) {
        this.status = status;
    }

    // Getters for timestamps (no setters, as they are managed automatically)

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    @Override
    public String toString() {
        return "{" +
                "employeeId='" + employeeId + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", role=" + (role != null ? role.getRoleName() : "null") +
                ", status=" + status +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
