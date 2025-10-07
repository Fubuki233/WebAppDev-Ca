package sg.com.aori.utils;

import jakarta.servlet.http.HttpSession;

public class LoginValidator {
    public static boolean sessionExists(String email, HttpSession session) {
        return email.equals(session.getAttribute("email"));
    }

}