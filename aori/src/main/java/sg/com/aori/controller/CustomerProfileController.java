package sg.com.aori.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import sg.com.aori.interfaces.ICustomerProfile;
import sg.com.aori.model.Customer;
import sg.com.aori.service.CustomerProfileService;

/**
 * This is a Controller for Aori customer to manage their own profile.
 * This will interact with the Rest API and React JS (View), and the service layers.
 * 
 * @author Ying Chun
 * @date 2025-10-08
 * @version 1.0
 */

@CrossOrigin
@RestController
@RequestMapping("/api/profile")
public class CustomerProfileController {

	@Autowired
	private final ICustomerProfile manageCustProfile;
	
	// Dependency injection for the service layer
	public CustomerProfileController(ICustomerProfile manageCustProfile) {
		this.manageCustProfile = manageCustProfile;
	}

	// getSession -->
	
	//Retrieve profile info for a specific user by email
	//@param email The email of the customer, passed in the URL
	
	@GetMapping("/{email}")
	public ResponseEntity<Customer> getCustomerById(@PathVariable("email") String email) {
		Customer profile = manageCustProfile.getProfileByEmail(email);
		return ResponseEntity.ok(profile);
	}
		
	@PutMapping("/{email}")
	public ResponseEntity<Customer> updateCustomerProfile(
			@PathVariable String email,
			@RequestBody Customer customerUpdateData) {
		
		Customer updatedProfile = manageCustProfile.updateCustomerProfile(email, customerUpdateData);
		return ResponseEntity.ok(updatedProfile);
	}
	
}
