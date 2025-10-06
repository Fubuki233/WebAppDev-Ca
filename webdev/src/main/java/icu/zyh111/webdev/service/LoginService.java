package icu.zyh111.webdev.service;

import icu.zyh111.webdev.entity.Customer;
import icu.zyh111.webdev.interfaces.ILogin;
import icu.zyh111.webdev.repository.LoginRepository;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class LoginService implements ILogin {
    @Autowired
    private LoginRepository loginRepository;

    @Override
    public Optional<Customer> findCustomerById(String id) {
        return loginRepository.findById(id);
    }

    @Override
    public Optional<Customer> findCustomerByEmail(String email) {
        return loginRepository.findCustomerByEmail(email);
    }

}