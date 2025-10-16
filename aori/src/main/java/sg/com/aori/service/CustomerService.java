package sg.com.aori.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import sg.com.aori.model.Customer;
import sg.com.aori.repository.CustomerRepository;

/**
 * @author Yibai
 * @date 2025-10-08
 * @version 1.0
 */

@Service
public class CustomerService {

    @Autowired
    private CustomerRepository customerRepository;

    public Optional<Customer> findCustomerByEmail(String email) {
        return customerRepository.findCustomerByEmail(email);
    }

    public Optional<Customer> findCustomerById(String id) {
        return customerRepository.findById(id);
    }

}