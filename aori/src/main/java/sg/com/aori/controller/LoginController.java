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
    public ResponseEntity<?> handleLogin(@RequestParam("email") String email,
            @RequestParam("passwd") String passwd,
            HttpSession session) {
        System.out.println("[LoginController] email: " + email + ", passwd: " + passwd);
        System.out.println("[LoginController] session id: " + session.getId());

        Customer customer = loginService.findCustomerByEmail(email).orElse(null);
        if (customer == null) {
            System.out.println("[LoginController] user not found: " + email);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new LoginResponse(false, "User not found", null, null));
        } else if (customer.getPassword().equals(passwd)) {
            System.out.println("[LoginController] User Valid");// better store customer in redis for session management

            session.setAttribute("email", email);
            session.setAttribute("id", customer.getCustomerId());

            // Create user data object (without sensitive info)
            UserData userData = new UserData(
                    customer.getCustomerId(),
                    customer.getEmail(),
                    customer.getFirstName(),
                    customer.getLastName());

            return ResponseEntity.ok(new LoginResponse(true, "Login successful", userData, session.getId()));
        } else {
            System.out.println("[LoginController] password invalid for: " + email);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new LoginResponse(false, "Invalid password", null, null));
        }
    }

    // Inner class for login response
    static class LoginResponse {
        private boolean success;
        private String message;
        private UserData user;
        private String sessionId;

        public LoginResponse(boolean success, String message, UserData user, String sessionId) {
            this.success = success;
            this.message = message;
            this.user = user;
            this.sessionId = sessionId;
        }

        public boolean isSuccess() {
            return success;
        }

        public String getMessage() {
            return message;
        }

        public UserData getUser() {
            return user;
        }

        public String getSessionId() {
            return sessionId;
        }
    }

    // Inner class for user data (without sensitive information)
    static class UserData {
        private String customerId;
        private String email;
        private String firstName;
        private String lastName;

        public UserData(String customerId, String email, String firstName, String lastName) {
            this.customerId = customerId;
            this.email = email;
            this.firstName = firstName;
            this.lastName = lastName;
        }

        public String getCustomerId() {
            return customerId;
        }

        public String getEmail() {
            return email;
        }

        public String getFirstName() {
            return firstName;
        }

        public String getLastName() {
            return lastName;
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpSession session) {
        try {
            session.invalidate();
            return ResponseEntity.ok(new LogoutResponse(true, "Logout successful"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new LogoutResponse(false, "Logout failed"));
        }
    }

    // Inner class for logout response
    static class LogoutResponse {
        private boolean success;
        private String message;

        public LogoutResponse(boolean success, String message) {
            this.success = success;
            this.message = message;
        }

        public boolean isSuccess() {
            return success;
        }

        public String getMessage() {
            return message;
        }
    }
}