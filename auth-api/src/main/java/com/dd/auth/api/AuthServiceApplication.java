package com.dd.auth.api;

import com.dd.auth.api.config.SwaggerConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.AbstractEnvironment;

@SpringBootApplication
@Import(SwaggerConfig.class)
public class AuthServiceApplication {

    public static void main(String[] args) {
        System.setProperty(AbstractEnvironment.ACTIVE_PROFILES_PROPERTY_NAME, "local");

        SpringApplication.run(AuthServiceApplication.class, args);
    }

}
