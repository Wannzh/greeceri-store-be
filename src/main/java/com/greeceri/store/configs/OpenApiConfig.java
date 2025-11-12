package com.greeceri.store.configs;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;

@Configuration
public class OpenApiConfig { 

    @Bean
    public OpenAPI openAPI() {
        
        // --- Server ---
        // Server Lokal (yang sudah Anda buat)
        Server localServer = new Server()
                .url("http://localhost:8080")
                .description("Server Lokal (Development)");

        // Saran 1: Tambahkan Server Produksi
        Server productionServer = new Server()
                .url("https://api.greeceri.store")
                .description("Server Produksi (Live)");

        
        // --- Kontak ---
        Contact contact = new Contact()
                .url("https://greeceri.store") 
                .name("Muhamad Alwan Fadhlurrohman (Greeceri Developer)")
                .email("alwanfdhlrhmn@gmail.com");

        
        // --- Lisensi ---
        License license = new License()
                .name("Apache License 2.0")
                .url("https://www.apache.org/licenses/LICENSE-2.0");

        
        // --- Info ---
        Info info = new Info()
                .title("API Greeceri Store")
                .version("1.0.0")
                .summary("Dokumentasi REST API E-Commerce Greeceri")
                .description("REST API untuk aplikasi E-Commerce Greeceri Store")
                .contact(contact)
                .termsOfService("https://greeceri.store/terms") 
                .license(license);

        
        // --- Security ---
        SecurityScheme securityScheme = new SecurityScheme()
            .type(SecurityScheme.Type.HTTP)
            .scheme("bearer")
            .bearerFormat("JWT")
            .in(SecurityScheme.In.HEADER)
            .name("Authorization");

        SecurityRequirement securityRequirement = new SecurityRequirement()
            .addList("Bearer Authentication");

        
        // --- Return OpenAPI Object ---
        return new OpenAPI()
            .info(info)
            .addSecurityItem(securityRequirement)
            .components(new Components()
                .addSecuritySchemes("Bearer Authentication", securityScheme) 
            )
            .servers(List.of(localServer, productionServer)); 
    }
}