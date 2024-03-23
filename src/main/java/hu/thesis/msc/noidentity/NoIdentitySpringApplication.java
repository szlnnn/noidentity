package hu.thesis.msc.noidentity;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.filter.CommonsRequestLoggingFilter;

@SpringBootApplication
@EnableScheduling
public class NoIdentitySpringApplication {

	public static void main(String[] args) {
		SpringApplication.run(NoIdentitySpringApplication.class, args);
	}



}
