/**
 * Controller for Employee Profile Management.
 *
 * @author Ying Chun
 * @date 2025-10-09
 * @version 1.0
 */

package sg.com.aori.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import sg.com.aori.interfaces.IEmployeeProfile;
import sg.com.aori.model.Employee;
import sg.com.aori.repository.EmployeeRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequestMapping("/admin/profile")
public class EmployeeProfileController {

    private final IEmployeeProfile manageEmployeeProfile;
    private final EmployeeRepository employeeRepository;

    // Constructor-based dependency injection
    public EmployeeProfileController(EmployeeRepository employeeRepository, 
        IEmployeeProfile manageEmployeeProfile) {
        this.employeeRepository = employeeRepository;
        this.manageEmployeeProfile = manageEmployeeProfile;
    }   
    /*
     * Displays the employee profile page.
     * Fetches currently logged-in employee's details.
     */
    @GetMapping("/{id}")
    public String viewProfilePage(Model model, HttpSession session) {
        // 1. Check the session for logged-in employee's ID (primary key)
        String loggedInEmployeeId = (String) session.getAttribute("loggedInEmployeeId");

        // 2. If not logged in, redirect to login page
        if (loggedInEmployeeId == null) {
            return "redirect:/admin/login";
        }
        try {
            // 3. Fetch employee details from the database using the ID from the session
            Employee employee = employeeRepository.findById(loggedInEmployeeId)
                    .orElseThrow(() -> new EntityNotFoundException("Employee not found"));

            model.addAttribute("employee", employee);
            return "admin/profile"; // Path to your Thymeleaf template
        } catch (EntityNotFoundException e) {
            // Only if the user was deleted while their session was active
            session.invalidate(); // Clear the invalid session
            return "redirect:/admin/login";
        }
    }
    
    @PostMapping("/edit")
    public String updateProfile(@Valid @ModelAttribute("employee") Employee employeeDetails,
                              BindingResult bindingResult,
                              RedirectAttributes redirectAttributes,
                              HttpSession session) {

        String loggedInEmployeeId = (String) session.getAttribute("loggedInEmployeeId");

        if (loggedInEmployeeId == null) {
            return "redirect:/admin/login";
        }

        // Basic check to prevent a user from updating another user's profile via a crafted request
        if (!loggedInEmployeeId.equals(employeeDetails.getEmployeeId())) {
             redirectAttributes.addFlashAttribute("error", "Unauthorized action.");
             return "redirect:/admin/profile";
        }

        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("error", "Invalid data. Please check the fields.");
            // Since we return to the profile view, we need to add the employee object
            // back to the model to repopulate the form with the incorrect data.
            // A better way is to not redirect but return the view name directly.
            return "admin/profile";
        }

        try {
            manageEmployeeProfile.updateEmployeeProfile(loggedInEmployeeId, employeeDetails);
            redirectAttributes.addFlashAttribute("success", "Profile updated successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "An error occurred while updating your profile.");
        }

        return "redirect:/admin/profile";
    }
    
}
