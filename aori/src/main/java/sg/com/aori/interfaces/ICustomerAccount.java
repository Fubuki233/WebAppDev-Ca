/**
 * Service Interface for Managing Customer Account
 * 
 * @author Ying Chun
 * @date 2025-10-08
 * @version 1.0 - previously known as ICustomerProfile
 * @version 2.0 - Renamed to ICustomerAccount. Added the updating of addresses.
 */

package sg.com.aori.interfaces;

import java.util.List;
import java.util.Optional;

import sg.com.aori.model.Customer;
import sg.com.aori.model.CustomerAddress;

public interface ICustomerAccount {

	/**
	 * Locates a Customer via their unique ID
	 * 
	 * @param customerId customer's unique ID
	 * @return customer object if found
	 */

	Optional<Customer> getCustomerById(String customerId);

	Customer updateCustProfile(String customerId, Customer profileData);

	List<CustomerAddress> getCustomerAddresses(String customerId);

	CustomerAddress updateCustomerAddress(String customerId, String addressId, CustomerAddress addressData);

}
