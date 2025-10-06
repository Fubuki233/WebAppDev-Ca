package sg.com.aori.interceptor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import sg.com.aori.model.Customer;
import sg.com.aori.service.LoginService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Component
public class LoggingInterceptor implements HandlerInterceptor {
    @Autowired
    LoginService loginService;

    @Override
    public boolean preHandle(HttpServletRequest request,
            HttpServletResponse response, Object handler) {
        HttpSession session = request.getSession();
        String email = (String) session.getAttribute("email");
        String passwd = (String) session.getAttribute("password");
        Customer customer = loginService.findCustomerByEmail(email).orElse(null);
        if (customer != null && customer.getPassword().equals(passwd)) {// better store customer in redis
            System.out.println("session valid");
            return true;

        } else {
            System.out.println("session invalid");
            try {
                response.sendRedirect("/login");
            } catch (Exception e) {
                System.out.println(e);
            }
            return false;
        }
    }

    @Override
    public void postHandle(HttpServletRequest request,
            HttpServletResponse response, Object handler, ModelAndView modelAndView) {
    }

    @Override
    public void afterCompletion(HttpServletRequest request,
            HttpServletResponse response, Object handler, Exception ex) {
    }
}