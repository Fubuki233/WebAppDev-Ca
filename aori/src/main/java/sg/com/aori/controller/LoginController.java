package sg.com.aori.controller;

import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

import sg.com.aori.model.Customer;
import sg.com.aori.service.LoginService;
import sg.com.aori.config.DebugMode;

/**
 * Controller class for handling login-related requests.
 * not fully tested*
 * 
 * @author Yunhe
 * @date 2025-10-07
 * @version 1.0
 */
@RestController
@RequestMapping("/api/auth")

public class LoginController {
    @Autowired
    LoginService loginService;

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @PostMapping("/login")
    public ResponseEntity<String> handleLogin(@RequestParam("email") String email,
            @RequestParam("passwd") String passwd,
            HttpSession session) {
        System.out.println("email: " + email + ", passwd: " + passwd);
        System.out.println("session id: " + session.getId());

        Customer customer = loginService.findCustomerByEmail(email).orElse(null);
        if (customer == null) {
            System.out.println("user not found: " + email);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not found");
        } else if (customer.getPassword().equals(passwd)) {
            System.out.println("User Valid");// better store customer in redis for session management

            session.setAttribute("email", email);
            session.setAttribute("id", customer.getCustomerId());

            return ResponseEntity.ok("Login successful");
        } else {
            System.out.println("password invalid for: " + email);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid password");
        }
    }

    @GetMapping("/logout")
    public ResponseEntity<String> logout(HttpSession session) {
        session.invalidate();
        return ResponseEntity.ok("Logout successful");
    }
}