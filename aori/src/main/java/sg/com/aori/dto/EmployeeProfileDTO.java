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

    @Pattern(regexp = "^\\+65 ?\\d{4} ?\\d{4}$", message = "Phone number must be in the format +65 XXXX XXXX")
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
