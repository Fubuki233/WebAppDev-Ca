package sg.com.aori.interfaces;

import sg.com.aori.model.Customer;

/**
 * This is a Service Interface for Aori customer to manage their own profile.
 * This will interact with the Service Implementation (CustomerProfileService) and CustomerProfileController.
 * 
 * @author Ying Chun
 * @date 2025-10-08
 * @version 1.0
 */

public interface ICustomerProfile {

	Customer getProfileByEmail(String email);
	Customer updateCustomerProfile(String email);
}
