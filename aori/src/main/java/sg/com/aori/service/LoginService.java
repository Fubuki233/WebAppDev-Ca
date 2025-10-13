package sg.com.aori.service;

import sg.com.aori.interfaces.ILogin;
import sg.com.aori.model.Customer;
import sg.com.aori.repository.CustomerRepository;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service class for handling login-related operations.
 *
 * @author Yunhe
 * @date 2025-10-07
 * @version 1.0
 */
@Service
@Transactional(readOnly = true)
public class LoginService implements ILogin {
    @Autowired
    private CustomerRepository customerRepository;

    @Override
    public Optional<Customer> findCustomerById(String id) {
        return customerRepository.findById(id);
    }

    @Override
    public Optional<Customer> findCustomerByEmail(String email) {
        return customerRepository.findCustomerByEmail(email);
    }

}