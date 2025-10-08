/**
 * This is a Controller for Aori customer to manage their own profile.
 * This will interact with the Rest API and React JS (View), and the service layers.
 * 
 * @author Ying Chun
 * @date 2025-10-08
 * @version 1.0 - previously known as CustomerProfileController; renamed to cover different sub-services
 * @version 2.0 - Renamed to CustomerAccountController. Introduce Http Session to authenticate users before they can perform actions.
 * 
 */

package sg.com.aori.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpSession;
import sg.com.aori.interfaces.ICustomerAccount;
import sg.com.aori.model.Customer;
import sg.com.aori.model.CustomerAddress;

@CrossOrigin
@RestController
@RequestMapping("/api/account")
public class CustomerAccountController {
	
	private final ICustomerAccount manageCustAccount;
	
	// dependency injection; no need to use @Autowired as it is not needed in this version
	public CustomerAccountController(ICustomerAccount manageCustAccount) {
		this.manageCustAccount = manageCustAccount;
	}

	// Helper method to get customer_id (PK) from current session
	// Even though customer will login via email, the more efficient method is still to authenticate session via customer_id
	
	private String getCustomerIdFromSession(HttpSession session) {
		return (String) session.getAttribute("customerId");
	}
	
	// View profile details
	// Retrieves profile details for a logged in customer
	
	@GetMapping("/profile")
	public ResponseEntity<Map<String, Object>> getProfile(HttpSession session) {
		Map<String, Object> response = new HashMap<>();
		try {
			String customerId = getCustomerIdFromSession(session);
			if (customerId == null) {
				response.put("message", "User not logged in.");
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
			}
			
			Optional<Customer> optCustomer = manageCustAccount.getCustomerById(customerId);
			if (optCustomer.isPresent()) {
				response.put("success", true);
				response.put("profile", optCustomer.get());
				return ResponseEntity.ok(response);
			} else {
				response.put("success", false);
				response.put("message", "Profile not found for logged-in user.");
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
			}
		} catch (Exception e) {
			response.put("success", false);
			response.put("message", "An error has occurred: " + e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
		}
	}
	
	// Update profile details
	// Allow logged-in customer to edit profile details
	
	@PutMapping("/profile/edit")
	public ResponseEntity<Map<String, Object>> updateProfile(@RequestBody Customer profileData, HttpSession session) {
		Map<String, Object> response = new HashMap<>();
		try {
			String customerId = getCustomerIdFromSession(session);
			if (customerId == null) {
				response.put("message", "User not logged in.");
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
			}
			Customer updatedCustomer = manageCustAccount.updateCustProfile(customerId, profileData); 
			response.put("success",  true);
			response.put("profile", updatedCustomer);
			return ResponseEntity.ok(response);
			
		} catch (Exception e) {
			response.put("success", false);
			response.put("message", "Unable to update profile due to error: " + e.getMessage());
			return ResponseEntity.badRequest().body(response);
		}
	}
	
	// View Customer Address
	// Retrieves address details for a logged in customer
	@GetMapping("/addresses")
	public ResponseEntity<Map<String, Object>> getAddresses(HttpSession session) {
		Map<String, Object> response = new HashMap<>();
		try {
			String customerId = getCustomerIdFromSession(session);
			if (customerId == null) {
				response.put("message", "User not logged in.");
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
			}
			List<CustomerAddress> addresses = manageCustAccount.getCustomerAddresses(customerId);
			response.put("success", true);
			response.put("addresses", addresses);
			return ResponseEntity.ok(response);
			
		} catch (Exception e) {
			response.put("success", false);
			response.put("message", "Unable to view addresses: " + e.getMessage());
			return ResponseEntity.badRequest().body(response);
		}
		
	}
	
	// Update addresses
	// Allow logged-in customer to edit address details
	
	@PutMapping("/addresses/{addressId}")
	public ResponseEntity<Map<String, Object>> updateAddress(
		@PathVariable String addressId, 
		@RequestBody CustomerAddress addressData, 
		HttpSession session) {
		Map<String, Object> response = new HashMap<>();
		try {
			String customerId = getCustomerIdFromSession(session);
			if (customerId == null) {
				response.put("message", "User not logged in.");
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
		}

		CustomerAddress updatedAddress = manageCustAccount.updateCustomerAddress(customerId, addressId, addressData);
		response.put("success", true);
		response.put("address", updatedAddress);
		return ResponseEntity.ok(response);
		
	} catch (Exception e) {
		response.put("success", false);
		response.put("message", "Unable to update addresses: " + e.getMessage());
		return ResponseEntity.badRequest().body(response);
	}

	}
}
