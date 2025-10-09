package sg.com.aori.utils;

import jakarta.servlet.http.HttpSession;

public class LoginValidator {
    public static String getUUID(HttpSession session) {
        return (String) session.getAttribute("id");
    }
}