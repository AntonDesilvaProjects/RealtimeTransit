package com.transit;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
@Configuration
public class Application {
    public static void main(String... args) {
        SpringApplication.run(Application.class, args);
    }
    @Bean
    public RestOperations restOperations() {
        return new RestTemplate();
    }
}
