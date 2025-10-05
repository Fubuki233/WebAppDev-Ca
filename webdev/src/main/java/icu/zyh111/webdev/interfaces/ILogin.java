package icu.zyh111.webdev.interfaces;

import icu.zyh111.webdev.entity.Customer;
import java.util.Optional;

public interface ILogin {

    Optional<Customer> findCustomerById(String id);

    Optional<Customer> findCustomerByEmail(String email);

}