package sg.com.aori.interfaces;

import java.util.Optional;

import sg.com.aori.model.Customer;

/**
 * Interface for login-related operations.
 *
 * @author Yunhe
 * @date 2025-10-07
 * @version 1.0
 */

public interface ILogin {
    Optional<Customer> findCustomerById(String id);

    Optional<Customer> findCustomerByEmail(String email);
}