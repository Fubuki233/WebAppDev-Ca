package sg.com.aori.controller;

import sg.com.aori.dto.EmployeeProfileDTO;
import sg.com.aori.interfaces.IEmployeeProfile;
import sg.com.aori.model.Employee;
import sg.com.aori.repository.EmployeeRepository;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

/**
 * Controller for Employee Profile Management.
 *
 * @author Ying Chun
 * @date 2025-10-09
 * @version 1.0
 */

@Controller
@RequestMapping("/admin/account")
public class EmployeeProfileController {

    private final IEmployeeProfile manageEmployeeProfile;
    private final EmployeeRepository employeeRepository;

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
        String loggedInEmployeeId = (String) session.getAttribute("employeeId");

        if (loggedInEmployeeId == null) {
            return "redirect:/admin/login";
        }
        try {
            Employee employee = employeeRepository.findById(loggedInEmployeeId)
                    .orElseThrow(() -> new EntityNotFoundException("Employee not found"));

            EmployeeProfileDTO profileDto = new EmployeeProfileDTO();
            profileDto.setEmployeeId(employee.getEmployeeId());
            profileDto.setPhoneNumber(employee.getPhoneNumber());

            model.addAttribute("employee", employee);
            model.addAttribute("profileDto", profileDto);
            model.addAttribute("activePage", "profile");

            return "admin/account/account-profile";

        } catch (EntityNotFoundException e) {
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

        if (bindingResult.hasErrors()) {
            Employee employee = employeeRepository.findById(profileDto.getEmployeeId()).orElse(null);
            model.addAttribute("employee", employee);
            model.addAttribute("activePage", "profile");
            return "admin/account/account-profile";
        } else {
            try {
                manageEmployeeProfile.updateEmployeeProfile(profileDto.getEmployeeId(), profileDto);
                redirectAttributes.addFlashAttribute("success", "Profile updated successfully!");
            } catch (Exception e) {
                redirectAttributes.addFlashAttribute("error", "An error occurred while updating your profile.");
            }

            return "redirect:/admin/account";
        }
    }
}