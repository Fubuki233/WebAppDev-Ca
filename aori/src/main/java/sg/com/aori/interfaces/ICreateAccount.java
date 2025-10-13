/**
 * Service Interface for Create Account
 *
 * @author SunRui
 * @date 2025-10-07
 * @version 1.0
 */

package sg.com.aori.interfaces;
import sg.com.aori.model.Customer;
import sg.com.aori.model.CustomerAddress;

/**
 * Service （interface) for the "Create Account" use case.
 * Focuses on creating a new Customer and (optionally) an initial CustomerAddress.
 */

public interface ICreateAccount{

    /**
     * Creates and persists a new Customer record.
     *
     * param: customer. A fully-prepared Customer entity to be saved (id is generate in the entity).
     * return: The persisted Customer entity with database-managed fields populated.
     * throws: IllegalArgumentException if input is invalid.
     */
    Customer createCustomer(Customer customer);

    /**
     * Optionally creates an initial CustomerAddress for the given customer.
     * If the customer currently has no default address, the saved address will be marked as default.
     *
     * param: address. The CustomerAddress to persist. Its customerId must reference the created customer.
     * return: The persisted CustomerAddress.
     * throws: IllegalArgumentException if input is invalid.
     */
    CustomerAddress addInitialAddress(CustomerAddress address);

}
