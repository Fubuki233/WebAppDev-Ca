/**
 * Service Implementation for Create Customer Account
 *optimise logic
 * 
 * @author SunRui
 * @date 2025-10-10
 * @version 1.3
 */

package sg.com.aori.service;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import sg.com.aori.interfaces.ICreateAccount;
import sg.com.aori.model.Customer;
import sg.com.aori.model.CustomerAddress;
import sg.com.aori.repository.CustomerAddressRepository;
import sg.com.aori.repository.CustomerRepository;

/**
 * Default implementation for the "Create Account" use case service.
 */

@Service
public class CreateAccountServiceImpl implements ICreateAccount {

    /** Repository for accessing Customer records */
    private final CustomerRepository customerRepository;

    /** Repository for accessing CustomerAddress records */
    private final CustomerAddressRepository addressRepository;

    public CreateAccountServiceImpl(CustomerRepository customerRepository,
            CustomerAddressRepository addressRepository) {
        this.customerRepository = customerRepository;
        this.addressRepository = addressRepository;
    }

    /**
     * Creates and persists a new Customer record.
     * 
     * param: customer. A fully-prepared Customer entity to be saved (id is
     * generated in the entity).
     * return: The persisted Customer entity with database-managed fields populated.
     * throws: IllegalArgumentException if input is invalid.
     */
    @Transactional
    @Override
    public Customer createCustomer(Customer customer) {
        validateBusinessRules(customer);
        return customerRepository.save(customer);
    }

    /**
     * Optionally creates an initial CustomerAddress for the given customer.
     * If the customer currently has no default address, the saved address will be
     * marked as default.
     *
     * param: address. The CustomerAddress to persist. Its customerId must reference
     * the created customer.
     * return: The persisted CustomerAddress.
     * throws: IllegalArgumentException if input is invalid.
     */
    @Transactional
    @Override
    public CustomerAddress addInitialAddress(CustomerAddress address) {
        String cid = address.getCustomerId();
    if (cid == null || !customerRepository.existsById(cid)) {
        throw new IllegalArgumentException("Customer not found");
    }

    // 若该用户目前没有默认地址，则将本地址设为默认
    if (addressRepository.findFirstByCustomerIdAndIsDefaultTrue(cid).isEmpty()) {
        address.setIsDefault(true);
    }

    return addressRepository.save(address);
    }

    /**
     * Retrieves a Customer by email.
     *
     * param: email. Email address to search for.
     * return: Optional containing Customer if found; otherwise empty.
     * throws: IllegalArgumentException if input is invalid.
     */
    @Transactional(readOnly = true)
    @Override
    public Optional<Customer> getCustomerByEmail(String email) {
        return customerRepository.findCustomerByEmail(email);
    }

    /*------Validaiton part------- */

    /**
     * Validate business rules before persisting a new customer.
     *
     * param: customer Customer entity carrying user input.
     * return: void
     * throws: IllegalArgumentException if input is invalid.
     */
    private void validateBusinessRules(Customer customer) {
        // 1) Unique email（ findCustomerByEmail）
        if (customer.getEmail() != null
                && customerRepository.findCustomerByEmail(customer.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email already registered");
        }

        // 2) age >= 13
        if (customer.getDateOfBirth() != null) {
            int age = java.time.Period.between(customer.getDateOfBirth(), java.time.LocalDate.now()).getYears();
            if (age < 13) {
                throw new IllegalArgumentException("Age must be at least 13");
            }
        }

        // 3) Password complexity: at least 1 uppercase/lowercase/number/symbol
        String p = customer.getPassword();
        if (!isPasswordStrong(p)) {
            throw new IllegalArgumentException("Password must include upper, lower, digit, and symbol");
        }
    }

    /**
     * Check password complexity: must contain upper, lower, digit, and symbol.
     *
     * param: pwd Raw password string.
     * return: true if strong enough; otherwise false.
     * throws: IllegalArgumentException if input is invalid.
     */
    private boolean isPasswordStrong(String pwd) {
        if (pwd == null)
            return false;
        boolean hasUpper = pwd.matches(".*[A-Z].*");
        boolean hasLower = pwd.matches(".*[a-z].*");
        boolean hasDigit = pwd.matches(".*\\d.*");
        boolean hasSymbol = pwd.matches(".*[^A-Za-z0-9].*");
        return hasUpper && hasLower && hasDigit && hasSymbol && pwd.length() >= 8;
    }

}
