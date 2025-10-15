package sg.com.aori.interfaces;

import java.util.List;
import java.util.Optional;

import jakarta.persistence.EntityNotFoundException;
import sg.com.aori.model.Customer;
import sg.com.aori.model.CustomerAddress;

/**
 * Service Interface for Managing Customer Account
 * This defines the contract for all account-related business logic.
 * 
 * @author Ying Chun
 * @date 2025-10-08
 * @version 1.0 - previously known as ICustomerProfile
 * @version 2.0 - Renamed to ICustomerAccount. Added the updating of addresses.
 */

public interface ICustomerAccount {
	/**
	 * Retrieves a Customer by their unique ID.
	 *
	 * @param customerId The primary key of the customer.
	 * @return An Optional containing the Customer if found, otherwise an empty
	 *         Optional.
	 */
	Optional<Customer> getCustomerById(String customerId);

	/**
	 * Updates the profile details for a specific customer.
	 *
	 * @param customerId  The ID of the customer whose profile is to be updated.
	 * @param profileData A Customer object containing the new details to apply.
	 * @return The updated and persisted Customer entity.
	 * @throws EntityNotFoundException if no customer exists with the given
	 *                                 customerId.
	 */
	Customer updateCustProfile(String customerId, Customer profileData);

	/**
	 * Retrieves all addresses associated with a specific customer.
	 *
	 * @param customerId The ID of the customer.
	 * @return A List of CustomerAddress objects, which may be empty if none are
	 *         found.
	 */
	List<CustomerAddress> getCustomerAddresses(String customerId);

	/**
	 * Updates the addresses for a specific customer.
	 *
	 * @param customerId  The ID of the customer whose address is to be updated.
	 * @param addressId   The ID of the address tied to the customer, whose details
	 *                    is to be updated.
	 * @param addressData A Customer object containing the new details to apply.
	 * @return The updated and persisted CustomerAddress object.
	 * @throws EntityNotFoundException if no customer address exists with the given
	 *                                 customerId and addressId.
	 */
	CustomerAddress updateCustomerAddress(String customerId, String addressId, CustomerAddress addressData);

}
