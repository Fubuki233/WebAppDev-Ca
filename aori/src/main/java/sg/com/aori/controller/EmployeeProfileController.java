/**
 * Controller for Employee Profile Management.
 *
 * @author Ying Chun
 * @date 2025-10-09
 * @version 1.0
 */

package sg.com.aori.controller;

import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import sg.com.aori.dto.EmployeeProfileDTO;
import sg.com.aori.interfaces.IEmployeeProfile;
import sg.com.aori.model.Employee;
import sg.com.aori.repository.EmployeeRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpSession;

import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequestMapping("/admin/account")
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
    @GetMapping
    public String viewProfilePage(Model model, HttpSession session) {
        // 1. Check the session for logged-in employee's ID (primary key)
        String loggedInEmployeeId = (String) session.getAttribute("employeeId"); // changed this on 11 Oct

        // If not logged in, redirect to login page
        if (loggedInEmployeeId == null) {
            return "redirect:/admin/login";
        }
        try {
            // 2. Fetch employee details from the database using the ID from the session
            Employee employee = employeeRepository.findById(loggedInEmployeeId)
                    .orElseThrow(() -> new EntityNotFoundException("Employee not found"));
            
            // 3. Create DTO instance and populate it with existing data
            EmployeeProfileDTO profileDto = new EmployeeProfileDTO();
            profileDto.setEmployeeId(employee.getEmployeeId());
            profileDto.setPhoneNumber(employee.getPhoneNumber());
            
            // 4. Add full employee object for display and DTO for form binding, set active page 
            model.addAttribute("employee", employee);
            model.addAttribute("profileDto", profileDto);
            model.addAttribute("activePage", "profile");

            return "admin/account/account-profile"; // Path to the Thymeleaf template

        } catch (EntityNotFoundException e) {
            // Only if the user was deleted while their session was active
            session.invalidate(); 
            return "redirect:/admin/login";
        }
    }
    
    @PostMapping("/profile/edit")
    public String updateProfile(@Valid @ModelAttribute("profileDto") EmployeeProfileDTO profileDto,
                              BindingResult bindingResult,
                              RedirectAttributes redirectAttributes,
                              HttpSession session,
                              Model model) {

        String loggedInEmployeeId = (String) session.getAttribute("employeeId");

        if (loggedInEmployeeId == null) {
            return "redirect:/admin/login";
        }
        if (!loggedInEmployeeId.equals(profileDto.getEmployeeId())) {
             redirectAttributes.addFlashAttribute("error", "Unauthorized action.");
             return "redirect:/admin/dashboard";
        }

        // If form validation fails
        if (bindingResult.hasErrors()) {
            Employee employee = employeeRepository.findById(profileDto.getEmployeeId()).orElse(null);
            model.addAttribute("employee", employee);
            model.addAttribute("activePage", "profile");
            return "admin/account/account-profile";

        } else {
        // If validation passes, proceed to update
            try {
                
                /*
                 * Note: Updated method (12 Oct) uses DTO to only update the phone number field.
                 * Previous method of fetching entire Employee entity and updating
                 * caused issues with other fields being nullified.
                 */
                manageEmployeeProfile.updateEmployeeProfile(profileDto.getEmployeeId(), profileDto);
                redirectAttributes.addFlashAttribute("success", "Profile updated successfully!");

            } catch (Exception e) {
                redirectAttributes.addFlashAttribute("error", "An error occurred while updating your profile.");
                }

        return "redirect:/admin/account";
        }
    }
}