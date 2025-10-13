package sg.com.aori.config;

/**
 * WebAppConfig class to configure web application settings.
 * Specifically, it registers the AuthInterceptor to intercept all incoming requests.
 * @author Yunhe
 * @date 2025-10-11
 * @version 1.0
 */
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import sg.com.aori.interceptor.AuthInterceptor;

@Component
public class WebAppConfig implements WebMvcConfigurer {
    @Autowired
    AuthInterceptor authInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authInterceptor)
                .addPathPatterns("/**"); // authInterceptor gonna intercept all requests and handle it

    }
}