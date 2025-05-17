package com.barraviera.login_auth_api.infra.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

// Pra indicar que esta á uma classe de configuração que o spring terá que carregar antes de todos os outros componentes
@Configuration
// Tipo de segurança web
@EnableWebSecurity
public class SecurityConfig {

    // Vamos injetar essas classes
    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Autowired
    SecurityFilter securityFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                // ativa o suporte a cors
                .cors(Customizer.withDefaults())
                // indicamos que a nossa aplicação é STATELESS ou seja, nao guardam estado de login dentro delas
                // toda vez que o usuario bater na nossa api o usuario deverá informar o token dele pra saber se está autenticado e válido
                // pois o padrao das apis rest é nao ter um banco de dados pra guardar se o usuario está autenticado ou nao
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // liberando esses endpoints para nao precisarem de autenticação
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(HttpMethod.POST, "/auth/login").permitAll()
                        .requestMatchers(HttpMethod.POST, "/auth/register").permitAll()
                        .anyRequest().authenticated() // ja no caso de outros endpoints queremos que seja autenticado
                )
                // ante de fazer o authorizeHttpRequests iremos aplicar o filtro securityFilter pra saber se está apto
                // pra passar a requisição para os nossos controllers, e caso a pessoa nao esteja autenticada o spring security ja barra retornando um erro 403
                .addFilterBefore(securityFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    // Usamos este metodo no controler pra fazer a criptografia da senha
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

}
