/**
 * Service Implementation for Managing Customer Account
 * 
 * @author Ying Chun
 * @date 2025-10-08
 * @version 1.0 - Previously named CustomerProfileService
 * @version 2.0 - Renamed as CustomerAccountService. Introduce Http Session to authenticate users before they can perform actions.
 */

package sg.com.aori.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.Resource;
import jakarta.persistence.EntityNotFoundException;
import sg.com.aori.model.Customer;
import sg.com.aori.model.CustomerAddress;
import sg.com.aori.repository.CustomerAddressRepository;
import sg.com.aori.repository.CustomerRepository;
import sg.com.aori.interfaces.ICustomerAccount;

@Service
@Transactional(readOnly = true)
public class CustomerAccountService implements ICustomerAccount {

	@Resource
	private final CustomerRepository customerRepository;
	private final CustomerAddressRepository addressRepository;

	// dependency injection; no need to use @Autowired as it is not needed in this
	// version
	public CustomerAccountService(CustomerRepository customerRepository, CustomerAddressRepository addressRepository) {
		this.customerRepository = customerRepository;
		this.addressRepository = addressRepository;
	}

	// Locate customer by ID

	@Override
	public Optional<Customer> getCustomerById(String customerId) {
		return customerRepository.findById(customerId);
	}

	/**
	 * Updates the profile information for a specific customer.
	 * It finds the customer by their ID and applies the new details.
	 *
	 * @param customerId  The unique ID of the customer to update.
	 * @param profileData A Customer object containing the new profile information.
	 * @return The updated and saved Customer entity.
	 * @throws EntityNotFoundException if no customer is found.
	 */

	@Override
	@Transactional(readOnly = false)
	public Customer updateCustProfile(String customerId, Customer profileData) {
		// Retrieve customer by ID
		Optional<Customer> optCustomer = customerRepository.findById(customerId);

		// Check if the customer exists
		Customer existingCustomer;
		if (optCustomer.isPresent()) {
			existingCustomer = optCustomer.get();
		} else {
			// throws exception if customer cannot be found
			throw new EntityNotFoundException("Customer not found with ID: " + customerId);
		}

		// Update customer's profile details for each field, encapsulated by the
		// 'profileData' object
		existingCustomer.setFirstName(profileData.getFirstName());
		existingCustomer.setLastName(profileData.getLastName());
		existingCustomer.setPhoneNumber(profileData.getPhoneNumber());
		existingCustomer.setGender(profileData.getGender());
		existingCustomer.setDateOfBirth(profileData.getDateOfBirth());

		Customer updatedCustomer = customerRepository.save(existingCustomer);

		return updatedCustomer;
	}

	// Locate customer address list by customer ID
	@Override
	public List<CustomerAddress> getCustomerAddresses(String customerId) {
		return addressRepository.findByCustomerId(customerId);
	}

	// Update customer addresses
	@Override
	@Transactional(readOnly = false)
	public CustomerAddress updateCustomerAddress(String customerId, String addressId, CustomerAddress addressData) {
		// Retrieve customer address by customer ID and address ID
		Optional<CustomerAddress> optAddress = addressRepository.findByAddressIdAndCustomerId(addressId, customerId);

		// Check if address exists and belongs to the customer
		CustomerAddress existingAddress;
		if (optAddress.isPresent()) {
			existingAddress = optAddress.get();
		} else {
			// exception if address cannot be found or does not match the customer
			throw new EntityNotFoundException("Address cannot be found or cannot be matched to your account.");
		}

		// Update fields of the existing address entity with new data
		existingAddress.setRecipientName(addressData.getRecipientName());
		existingAddress.setPhoneNumber(addressData.getPhoneNumber());
		existingAddress.setAddressLine1(addressData.getAddressLine1());
		existingAddress.setAddressLine2(addressData.getAddressLine2());
		existingAddress.setCity(addressData.getCity());
		existingAddress.setPostalCode(addressData.getPostalCode());
		existingAddress.setCountry(addressData.getCountry());

		// Save updated address to the database
		CustomerAddress updatedAddress = addressRepository.save(existingAddress);

		// Return the updated address
		return updatedAddress;
	}

}
