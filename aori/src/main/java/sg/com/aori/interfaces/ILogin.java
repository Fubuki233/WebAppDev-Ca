package sg.com.aori.interfaces;

import java.util.Optional;

import sg.com.aori.model.Customer;

public interface ILogin {

    Optional<Customer> findCustomerById(String id);

    Optional<Customer> findCustomerByEmail(String email);

}