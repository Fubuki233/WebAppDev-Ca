/**
 * Setup OrderValidationController 
 * v1.1: REST API applied
 *
 * @author Jiang
 * @date 2025-10-07
 * @version 1.1
 */

package sg.com.aori.config;

import sg.com.aori.interceptor.OrderValidationInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class OrderConfig implements WebMvcConfigurer {

    @Autowired
    private OrderValidationInterceptor orderValidationInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(orderValidationInterceptor)
                .addPathPatterns("/api/orders/**");
    }
}