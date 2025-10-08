/**
 * This is a Service Interface for Aori customer to manage their own profile.
 * This will interact with the Service Implementation and Controller.
 * 
 * @author Ying Chun
 * @date 2025-10-08
 * @version 1.0 - previously known as ICustomerProfile
 * @version 2.0 - Renamed to ICustomerAccount
 */

package sg.com.aori.interfaces;

import java.util.List;
import java.util.Optional;

import sg.com.aori.model.Customer;
import sg.com.aori.model.CustomerAddress;


public interface ICustomerAccount {

	Optional<Customer> getCustomerById(String customerId);
	Customer updateCustProfile(String customerId, Customer profileData);
	List<CustomerAddress> getCustomerAddresses(String customerId);
	CustomerAddress updateCustomerAddress(String customerId, String addressId, CustomerAddress addressData);

}
