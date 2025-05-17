package com.barraviera.login_auth_api.infra.cors;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

// Classe de configuração para habilitar todas as requisições que vieram do dominio http://localhost:4200 onde estará rodando o frontend
// permitindo requisições get, post, delete e put
@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:4200")
                .allowedMethods("GET", "POST", "DELETE", "PUT", "OPTIONS")
                .allowedHeaders("*");  // <--- Permite headers como Content-Type;
    }

}
