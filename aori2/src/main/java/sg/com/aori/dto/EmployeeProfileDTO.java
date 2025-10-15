package sg.com.aori.dto;

import jakarta.validation.constraints.Pattern;

/**
 * DTO for updating an employee's profile.
 *
 * @author Ying Chun
 * @date 2025-10-12
 * @version 1.0
 */

public class EmployeeProfileDTO {

    private String employeeId;

    @Pattern(regexp = "^\\+?[1-9]\\d{1,14}$", message = "Phone number must follow this exact format (e.g. +6581234567) with no spaces.")
    private String phoneNumber;

    public String getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
