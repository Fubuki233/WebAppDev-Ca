package sg.com.aori.model;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

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

@Entity
@Table(name = "Employee")
public class Employee {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    // employee_id VARCHAR(36) PRIMARY KEY DEFAULT (UUID())
    @Column(name = "employee_id", length = 36)
    private String employeeId;

    // first_name VARCHAR(50) NOT NULL,
    @Column(name = "first_name", length = 50, nullable = false)
    private String firstName;

    // last_name VARCHAR(50) NOT NULL,
    @Column(name = "last_name", length = 50, nullable = false)
    private String lastName;

    // email VARCHAR(255) UNIQUE NOT NULL,
    @Column(name = "email", unique = true, nullable = false)
    private String email;

    // password_hash VARCHAR(255) NOT NULL,
    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    // phone_number VARCHAR(15),
    @Column(name = "phone_number", length = 15)
    private String phoneNumber;

    // role_id VARCHAR(36) NOT NULL,
    // Many-to-one relationship with Role entity
    @ManyToOne
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;

    // status ENUM('Active', 'Inactive', 'Suspended') NOT NULL,
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private EmployeeStatus status;

    // created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    // updated_at TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    @CreationTimestamp
    @Column(name = "updated_at", updatable = true)
    private LocalDateTime updatedAt;

    public enum EmployeeStatus {
        Active,
        Inactive,
        Suspended
    }
    
    // Constructors
    
    public Employee() { }

    public Employee(String firstName, String lastName, String email, String passwordHash,
            String phoneNumber, Role role, EmployeeStatus status) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.passwordHash = passwordHash;
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

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
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

}
