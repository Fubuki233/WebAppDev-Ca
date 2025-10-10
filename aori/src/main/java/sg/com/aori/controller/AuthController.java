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
import org.springframework.web.bind.annotation.CrossOrigin;

import sg.com.aori.model.Customer;
import sg.com.aori.service.LoginService;

/**
 * Controller class for handling authentication-related requests.
 * not fully tested*
 * 
 * @author Yunhe
 * @date 2025-10-07
 * @version 1.0
 * 
 *          ------------------------------------------------------------------------
 *          now, all the methods had been tested,they will return the correct
 *          response
 *          to the frontend
 * @author Yunhe
 * @date 2025-10-09
 * @version 2.0
 */
@CrossOrigin
@RestController
@RequestMapping("/api/auth")

public class AuthController {
    @Autowired
    LoginService loginService;

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    /**
     * Handles user login requests.
     * Example response:
     * {
     * "success": true,
     * "message": "Login successful",
     * "user": {
     * "customerId": "5f2f7b1d-c3d1-4a3e-abca-6447215ea70a",
     * "email": "john@example.com",
     * "firstName": "John",
     * "lastName": "Doe1"
     * },
     * "sessionId": "58A9B2F4502A5906C4FCC20B0DE699B7"
     * }
     *
     * @param email   User's email address.
     * @param passwd  User's password.
     * @param session HTTP session for storing user information.
     * @return ResponseEntity containing login result.
     */
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

    /**
     * Handles user logout requests.
     * Example response:
     * {
     * "success": true,
     * "message": "Logout successful"
     * }
     *
     * @return ResponseEntity containing logout result.
     * 
     */
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

    /**
     * Get current user's UUID from session
     * Example response:
     * {
     * "uuid": "5f2f7b1d-c3d1-4a3e-abca-6447215ea70a"
     * }
     * 
     * @param session HTTP session
     * @return ResponseEntity containing UUID or null if not authenticated
     */
    @GetMapping("/uuid")
    public ResponseEntity<?> getSession(HttpSession session) {
        String uuid = (String) session.getAttribute("id");
        if (uuid != null && !uuid.isEmpty()) {
            System.out.println("[AuthController] session UUID: " + uuid);
            return ResponseEntity.ok().body(new UuidResponse(uuid));
        } else {
            System.out.println("[AuthController] no valid session");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new UuidResponse(null));
        }
    }

    // Inner class for UUID response
    static class UuidResponse {
        private String uuid;

        public UuidResponse(String uuid) {
            this.uuid = uuid;
        }

        public String getUuid() {
            return uuid;
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