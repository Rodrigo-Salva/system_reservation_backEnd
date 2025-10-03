package com.rodrigo.curso.springboot.app.system_reservation_back.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Sistema de Reservas API")
                        .version("1.0")
                        .description("API para gestionar usuarios, salas y reservas 🚀")
                        .contact(new Contact()
                                .name("Rodrigo Developer")
                                .email("rodrigo@example.com")
                                .url("https://github.com/rodrigo"))
                        .license(new License().name("Apache 2.0").url("http://springdoc.org")));
    }
}