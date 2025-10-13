/**
 * REST Controller for Managing Customer Account
 * 
 * @author Ying Chun, Sun Rui
 * @date 2025-10-08
 * @version 1.0 - previously known as CustomerProfileController; renamed to cover different sub-services
 * @version 2.0 - Renamed to CustomerAccountController. Introduce Http Session to authenticate users before they can perform actions.
 * @version 2.1 - Added validation
 * @version 2.2 - Added Javadoc comments
 */

package sg.com.aori.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import jakarta.validation.Valid;

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

	// dependency injection; no need to use @Autowired as it is not needed in this
	// version
	public CustomerAccountController(ICustomerAccount manageCustAccount) {
		this.manageCustAccount = manageCustAccount;
	}

	// Helper method to get customer_id (PK) from current session
	// Even though customer will login via email, the more efficient method is still
	// to authenticate session via customer_id

	private String getCustomerIdFromSession(HttpSession session) {
		return (String) session.getAttribute("id");
	}

	/**
	 * View profile details. Retrieves profile details for a logged in customer.
	 * {
	 * "success": true,
	 * "profile": {
	 * "customerId": "07532ea4-8954-5e60-86da-c1b7844e0a7f",
	 * "firstName": "John",
	 * "lastName": "Tang",
	 * "email": "john@example.com",
	 * "password": "SecurePass123!",
	 * "phoneNumber": "+6588112233",
	 * "gender": "Undisclosed",
	 * "dateOfBirth": "1991-02-24",
	 * "createdAt": "2025-05-21T17:22:33",
	 * "updatedAt": "2025-10-11T07:56:05"
	 * }
	 * 
	 * @param customerId Customer ID tagged to email used during login
	 * @param session    HTTP session for storing user information
	 * @return ResponseEntity with profile details or error message
	 */

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

	/**
	 * Update profile details. Allow logged-in customer to edit profile details.
	 * 
	 * [Postman] Data required to be passed in JSON format in the request body:
	 * {
	 * "customerId": "07532ea4-8954-5e60-86da-c1b7844e0a7f",
	 * "firstName": "John",
	 * "lastName": "Tang",
	 * "email": "john@example.com",
	 * "password": "SecurePass123!",
	 * "phoneNumber": "+6586671234",
	 * "gender": "Male",
	 * "dateOfBirth": "1993-01-24"
	 * }
	 * [Postman] Message received if successful:
	 * {
	 * "success": true,
	 * "profile": {
	 * "customerId": "07532ea4-8954-5e60-86da-c1b7844e0a7f",
	 * "firstName": "John",
	 * "lastName": "Tang",
	 * "email": "john@example.com",
	 * "password": "SecurePass123!",
	 * "phoneNumber": "+6586671234",
	 * "gender": "Male",
	 * "dateOfBirth": "1993-01-24",
	 * "createdAt": "2025-05-21T17:22:33",
	 * "updatedAt": "2025-10-12T22:11:49.180996"
	 * }
	 * 
	 * @param profileData
	 * @param session
	 * @return ResponseEntity with updated profile details or error message
	 */

	@PutMapping("/profile/edit")
	public ResponseEntity<Map<String, Object>> updateProfile(@RequestBody Map<String, Object> profileData,
			HttpSession session) {
		System.out.println("[CustomerAccountController] Data received from frontend: " + profileData.toString());
		Map<String, Object> response = new HashMap<>();
		try {
			String customerId = getCustomerIdFromSession(session);
			if (customerId == null) {
				response.put("message", "User not logged in.");
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
			}

			// Get existing customer data
			Optional<Customer> optCustomer = manageCustAccount.getCustomerById(customerId);
			if (!optCustomer.isPresent()) {
				response.put("success", false);
				response.put("message", "Customer not found.");
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
			}

			Customer existingCustomer = optCustomer.get();

			// Update only the fields that are provided in the request
			if (profileData.containsKey("firstName")) {
				String firstName = (String) profileData.get("firstName");
				if (firstName == null || firstName.trim().isEmpty()) {
					response.put("success", false);
					response.put("message", "First name is required");
					return ResponseEntity.badRequest().body(response);
				}
				if (!firstName.matches("^[A-Za-z]+$")) {
					response.put("success", false);
					response.put("message", "First name must contain alphabets only");
					return ResponseEntity.badRequest().body(response);
				}
				existingCustomer.setFirstName(firstName);
			}
			if (profileData.containsKey("lastName")) {
				String lastName = (String) profileData.get("lastName");
				if (lastName == null || lastName.trim().isEmpty()) {
					response.put("success", false);
					response.put("message", "Last name is required");
					return ResponseEntity.badRequest().body(response);
				}
				if (!lastName.matches("^[A-Za-z]+$")) {
					response.put("success", false);
					response.put("message", "Last name must contain alphabets only");
					return ResponseEntity.badRequest().body(response);
				}
				existingCustomer.setLastName(lastName);
			}
			if (profileData.containsKey("phone")) {
				String phone = (String) profileData.get("phone");
				if (phone != null && !phone.trim().isEmpty()) {
					// Validate phone number format (E.164 format - must start with +)
					// Database check constraint requires phone numbers to start with +
					if (!phone.matches("^\\+[1-9]\\d{1,14}$")) {
						response.put("success", false);
						response.put("message",
								"Phone number must start with + and follow E.164 format (e.g., +6512345678)");
						return ResponseEntity.badRequest().body(response);
					}
					existingCustomer.setPhoneNumber(phone);
				} else {
					existingCustomer.setPhoneNumber(null);
				}
			}
			if (profileData.containsKey("dateOfBirth")) {
				String dob = (String) profileData.get("dateOfBirth");
				if (dob != null && !dob.trim().isEmpty()) {
					existingCustomer.setDateOfBirth(java.time.LocalDate.parse(dob));
				} else {
					existingCustomer.setDateOfBirth(null);
				}
			}
			if (profileData.containsKey("gender")) {
				String genderStr = (String) profileData.get("gender");
				if (genderStr != null && !genderStr.trim().isEmpty()) {
					try {
						existingCustomer.setGender(Customer.Gender.valueOf(genderStr));
					} catch (IllegalArgumentException e) {
						// Invalid gender value, set to null
						existingCustomer.setGender(null);
					}
				} else {
					existingCustomer.setGender(null);
				}
			}

			Customer updatedCustomer = manageCustAccount.updateCustProfile(customerId, existingCustomer);
			response.put("success", true);
			response.put("profile", updatedCustomer);
			return ResponseEntity.ok(response);

		} catch (Exception e) {
			e.printStackTrace();
			response.put("success", false);
			response.put("message", "Unable to update profile due to error: " + e.getMessage());
			return ResponseEntity.badRequest().body(response);
		}
	}

	/**
	 * View Customer Address. Retrieves address details for a logged in customer
	 * Example of successful response:
	 * {
	 * "addresses": [
	 * {
	 * "addressId": "b7f782c1-ca4c-5909-ad02-9155dad466e3",
	 * "customerId": "07532ea4-8954-5e60-86da-c1b7844e0a7f",
	 * "recipientName": "John Doe",
	 * "phoneNumber": "+65 84318855",
	 * "addressLine1": "931 Orchard Rd",
	 * "addressLine2": "Unit #35-847",
	 * "city": "Singapore",
	 * "postalCode": "249145",
	 * "country": "Singapore",
	 * "isBilling": true,
	 * "isDefault": true,
	 * "createdAt": "2025-10-10T05:16:59",
	 * "customer": {
	 * "customerId": "07532ea4-8954-5e60-86da-c1b7844e0a7f",
	 * "firstName": "John",
	 * "lastName": "Tang",
	 * "email": "john@example.com",
	 * "password": "SecurePass123!",
	 * "phoneNumber": "+6586671234",
	 * "gender": "Male",
	 * "dateOfBirth": "1993-01-24",
	 * "createdAt": "2025-05-21T17:22:33",
	 * "updatedAt": "2025-10-12T22:11:49",
	 * "hibernateLazyInitializer": {}
	 * }
	 * }
	 * ],
	 * "success": true
	 * }
	 * 
	 * @param customerId Customer ID tagged to email used during login
	 * @param session    HTTP session for storing user information
	 * @return ResponseEntity with address details or error message
	 */

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

	/**
	 * Update addresses. Allow logged-in customer to edit address details.
	 * Example of response to post to a specific addressId:
	 * {
	 * "customerId": "07532ea4-8954-5e60-86da-c1b7844e0a7f",
	 * "recipientName": "Johnny Depp",
	 * "phoneNumber": "+65 84318855",
	 * "addressLine1": "931 Swensen Rd",
	 * "addressLine2": "Unit #35-847",
	 * "city": "Singapore",
	 * "postalCode": "249145",
	 * "country": "Singapore",
	 * "isBilling": true,
	 * "isDefault": true
	 * }
	 * Response if successfully updated:
	 * {
	 * "addresses": [
	 * {
	 * "addressId": "b7f782c1-ca4c-5909-ad02-9155dad466e3",
	 * "customerId": "07532ea4-8954-5e60-86da-c1b7844e0a7f",
	 * "recipientName": "John Doe",
	 * "phoneNumber": "+65 84318855",
	 * "addressLine1": "931 Orchard Rd",
	 * "addressLine2": "Unit #35-847",
	 * "city": "Singapore",
	 * "postalCode": "249145",
	 * "country": "Singapore",
	 * "isBilling": true,
	 * "isDefault": true,
	 * "createdAt": "2025-10-10T05:16:59",
	 * "customer": {
	 * "customerId": "07532ea4-8954-5e60-86da-c1b7844e0a7f",
	 * "firstName": "John",
	 * "lastName": "Tang",
	 * "email": "john@example.com",
	 * "password": "SecurePass123!",
	 * "phoneNumber": "+6586671234",
	 * "gender": "Male",
	 * "dateOfBirth": "1993-01-24",
	 * "createdAt": "2025-05-21T17:22:33",
	 * "updatedAt": "2025-10-12T22:11:49",
	 * "hibernateLazyInitializer": {}
	 * }
	 * }
	 * ],
	 * "success": true
	 * }
	 * 
	 * @param addressId
	 * @param addressData
	 * @param session
	 * @return ResponseEntity with updated address details or error message
	 */

	@PutMapping("/addresses/{addressId}")
	public ResponseEntity<Map<String, Object>> updateAddress(
			@PathVariable String addressId, @Valid @RequestBody CustomerAddress addressData,
			HttpSession session) {
		Map<String, Object> response = new HashMap<>();
		try {
			String customerId = getCustomerIdFromSession(session);
			if (customerId == null) {
				response.put("message", "User not logged in.");
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
			}

			CustomerAddress updatedAddress = manageCustAccount.updateCustomerAddress(customerId, addressId,
					addressData);
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
