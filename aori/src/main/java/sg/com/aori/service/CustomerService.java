/**
 * @author Jiang
 * @date 2025-10-08
 * @version 1.0
 */

package sg.com.aori.service;

import sg.com.aori.model.Customer;
import sg.com.aori.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Optional;

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