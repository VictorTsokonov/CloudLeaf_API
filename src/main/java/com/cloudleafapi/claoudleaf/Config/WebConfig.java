package com.cloudleafapi.claoudleaf.Config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

	@Override
	public void addCorsMappings(CorsRegistry registry) {
		registry.addMapping("/**") // all endpoints in the application
				.allowedOrigins("*") // allow this origin
				.allowedMethods("*") // allow all methods (GET, POST, etc.)
				.allowedHeaders("*"); // allow all headers
	}

}
