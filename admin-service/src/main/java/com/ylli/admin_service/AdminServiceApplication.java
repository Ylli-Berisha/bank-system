package com.ylli.admin_service;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@EntityScan(basePackages = "com.ylli.shared.models")
@ComponentScan(basePackages = {
		"com.ylli.admin_service",
		"com.ylli.shared.base"
})
@OpenAPIDefinition(
		info = @Info(
				title = "Admin Micro-service API",
				version = "1.0",
				description = "API documentation for the Admin Micro-service",
				contact = @Contact(
						name = "Ylli Berisha",
						email = "yllberishaa14@gmail.com"
				)
		)
)
public class AdminServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(AdminServiceApplication.class, args);
	}

}
