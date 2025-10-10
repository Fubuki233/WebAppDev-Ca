
/**
 * EmployeeController Test Thymleaf.
 *
 * @author xiaobo SunRui
 * @date 2025-10-10
 * @version 1.0
 */

package sg.com.aori.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import org.springframework.test.web.servlet.MockMvc;

import sg.com.aori.model.Role;
import sg.com.aori.service.EmployeeService;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(EmployeeController.class) // Focuses test only on the controller layer
class EmployeeControllerTest {

    @Autowired
    private MockMvc mockMvc; // Used to simulate HTTP requests

    @MockitoBean
    private EmployeeService employeeService; // Mocks the service dependency

    /**
     * Test case for GET /employees (listEmployees method).
     * Verifies the model contains the list and the correct view is returned.
     */
    @Test
    void createEmployee_ShouldRedirectToListPage() throws Exception {

        // Arrange: Create a mock Role object (required by the Employee entity)
        Role mockRole = new Role("d323b4e1-2f3b-4c5d-8a9e-f0a1b2c3d4e5", "Manager");

        // Mock the service to return the created employee (or verify it was called)
        // You would mock the role service lookup if your controller did it separately.

        // Act & Assert: Simulate a form submission (POST)
        mockMvc.perform(post("/employees") // Endpoint for creation
                .param("firstName", "Alex")
                .param("lastName", "Chen")
                .param("email", "alex.chen@aori.com")
                .param("password", "SecurePassword123!")
                .param("phoneNumber", "+6598765432")
                // Pass the Role ID if your form uses a hidden input/dropdown for the role
                .param("role.roleId", mockRole.getRoleId())
                .param("status", "Active")
                .with(csrf())) // Required for POST/PUT/DELETE in Spring Security

                .andExpect(status().is3xxRedirection()) // Expect HTTP 302 Found (Redirection)
                .andExpect(redirectedUrl("/employees")); // Verify it redirects back to the list
    }
}