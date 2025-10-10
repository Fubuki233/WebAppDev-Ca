/**
 * @author Jiang
 * @date 2025-10-08
 * @version 1.0
 */

package sg.com.aori.utils;

import sg.com.aori.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import jakarta.servlet.http.HttpSession;
// import sg.com.aori.model.Customer;

public class getSession {

    @Autowired
    public CustomerService customerService;

    public static String getCustomerId(HttpSession session) {

        // String email = (String) session.getAttribute("email");
        // if (email == null)
        // return null;
        // Customer customer = customerService.findCustomerByEmail(email).orElse(null);
        // return customer != null ? customer.getCustomerId() : null;
        // ***** Use the statement below if customerId is stored in session
        // AuthController stores customerId as "id" in session
        return (String) session.getAttribute("id");
    }
}
