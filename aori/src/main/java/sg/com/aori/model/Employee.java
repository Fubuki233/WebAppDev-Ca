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
import jakarta.persistence.Table;

@Entity
@Table(name = "employee") // Maps the class to the "Employee" table in the database
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
    @Column(name = "role_id", nullable = false)
    private String roleId;

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
    private LocalDateTime updateAt;

}
