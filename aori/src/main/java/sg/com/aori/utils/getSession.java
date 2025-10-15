package sg.com.aori.utils;

import sg.com.aori.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import jakarta.servlet.http.HttpSession;

/**
 * Method to get Customer ID from Session
 * 
 * @param session
 * @return customerId
 * 
 * @author Yibai
 * @date 2025-10-08
 * @version 1.0
 */
public class getSession {

    @Autowired
    public CustomerService customerService;

    public static String getCustomerId(HttpSession session) {
        return (String) session.getAttribute("id");
    }

}