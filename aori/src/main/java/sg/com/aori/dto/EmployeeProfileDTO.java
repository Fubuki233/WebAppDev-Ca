/**
 * DTO for updating an employee's profile.
 *
 * @author Ying Chun
 * @date 2025-10-12
 * @version 1.0
 */
package sg.com.aori.dto;

import jakarta.validation.constraints.Pattern;

public class EmployeeProfileDTO {

    private String employeeId;

    // @Pattern(regexp = "^\\+65 ?\\d{4} ?\\d{4}$", message = "Phone number must be
    // in the format +65 XXXX XXXX")
    @Pattern(regexp = "^\\+?[1-9]\\d{1,14}$", message = "Phone number must follow this exact format (e.g. +6581234567) with no spaces.")
    private String phoneNumber;

    // Getters and Setters
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
