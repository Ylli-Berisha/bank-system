package com.ylli.users_service;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

@EnableFeignClients(basePackages = "com.ylli.shared.clients")
@SpringBootApplication
@EntityScan(basePackages = "com.ylli.shared.models")
@ComponentScan(basePackages = {
		"com.ylli.users_service",
		"com.ylli.shared.base"
})
@OpenAPIDefinition(
		info = @Info(
				title = "Users Micro-service API",
				version = "1.0",
				description = "API documentation for the Users Micro-service",
				contact = @Contact(
						name = "Ylli Berisha",
						email = "yllberishaa14@gmail.com"
				)
		)
)
public class UsersServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(UsersServiceApplication.class, args);
	}

}
