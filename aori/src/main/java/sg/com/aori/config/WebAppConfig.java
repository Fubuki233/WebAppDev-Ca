package sg.com.aori.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import sg.com.aori.interceptor.LoggingInterceptor;

@Component
public class WebAppConfig implements WebMvcConfigurer {
    @Autowired
    LoggingInterceptor loggingInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loggingInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns(
                        "/",
                        "/login",
                        "/css/**",
                        "/js/**",
                        "/images/**",
                        "/favicon.ico");

    }
}