package sg.com.aori.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.Resource;
import sg.com.aori.interfaces.ICustomerProfile;
import sg.com.aori.model.Customer;
import sg.com.aori.repository.CustomerRepository;

/**
 * This is a Service Implementation for Aori customer to manage their own profile.
 * This will interact with the Service Interface (ICustomerProfile) and CustomerProfileController.
 * 
 * @author Ying Chun
 * @date 2025-10-08
 * @version 1.0
 */

@Service
@Transactional(readOnly = true)
public class CustomerProfileService implements ICustomerProfile {
	
	@Resource
	private CustomerRepository customerRepository;
	
	public CustomerProfileService(CustomerRepository customerRepository) {
		this.customerRepository = customerRepository;
	}

	@Override
	public Customer getProfileByEmail(String email) {
		Optional<Customer> optCustomer = customerRepository.findCustomerByEmail(email);
		
		if (optCustomer.isPresent()) {
			return optionalCustomer.get();
		} else {
			// throws exception if customer cannot be found
		}
		throw new EntityNotFoundException("Customer not found with email:" + email);
	}

	@Override
	@Transactional(readOnly = false)
	public Customer updateCustomerProfile(String email, Customer customerUpdateData) {
		Optional<Customer> optCustomer = customerRepository.findCustomerByEmail(email);

		Customer customerToUpdate;
		
		return null;
	}
	
	@Override
	
	
	
	
}
