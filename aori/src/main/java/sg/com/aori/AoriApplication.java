package sg.com.aori;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletContextInitializer;
import jakarta.servlet.ServletContext;
import jakarta.servlet.SessionTrackingMode;
import java.util.Collections;

@SpringBootApplication
public class AoriApplication implements ServletContextInitializer {

	public static void main(String[] args) {
		SpringApplication.run(AoriApplication.class, args);
	}

	@Override
	public void onStartup(ServletContext servletContext) {
		// Disable URL-based session tracking
		servletContext.setSessionTrackingModes(Collections.singleton(SessionTrackingMode.COOKIE));
	}
}
