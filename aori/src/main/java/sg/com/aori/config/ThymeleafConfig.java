/**
 * @author Ying Chun
 * @date 2025-10-12
 * @version 1.0
 */

package sg.com.aori.config;

import nz.net.ultraq.thymeleaf.layoutdialect.LayoutDialect;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * To configure the additional Thymeleaf dialect used for this Spring Boot application.
 */
@Configuration
public class ThymeleafConfig {

    /**
     * This method defines the LayoutDialect bean. Spring Boot's auto-configuration
     * will detect this bean and automatically add it to the template engine.
     * This will allow us to use the layout.html for consistent page layouts.
     * Additional dependency "thymeleaf-layout-dialect" has been added in pom.xml for this to work.
     *
     * @return An instance of LayoutDialect.
     */
    @Bean
    public LayoutDialect layoutDialect() {
        return new LayoutDialect();
    }
}