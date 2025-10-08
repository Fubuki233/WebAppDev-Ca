package sg.com.aori.controller;

import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import sg.com.aori.model.Customer;
import sg.com.aori.service.LoginService;

/**
 * Controller class for handling login-related requests.
 * not fully tested*
 * 
 * @author Yunhe
 * @date 2025-10-07
 * @version 1.0
 */
@Controller
public class LoginController {
    @Autowired
    LoginService loginService;

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @PostMapping("/login")
    public String handleLogin(@RequestParam("email") String email, @RequestParam("passwd") String passwd,
            Model model, HttpSession session) {
        System.out.println("email: " + email + ", passwd: " + passwd);
        Customer customer = loginService.findCustomerByEmail(email).orElse(null);
        if (customer == null) {
            System.out.println("user not found: " + email);
            return "login";
        } else if (customer.getPassword().equals(passwd)) {
            System.out.println("User Valid");// better store customer in redis for session management

            session.setAttribute("email", email);
            session.setAttribute("id", customer.getCustomerId());

            return "login";
        }
        System.out.println("password invalid: " + session.getAttribute("email"));

        return "login";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();

        return "login";
    }
}