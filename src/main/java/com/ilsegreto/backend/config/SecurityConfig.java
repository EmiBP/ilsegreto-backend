package com.ilsegreto.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

import com.ilsegreto.backend.service.CustomOAuth2UserService;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final CustomOAuth2UserService customOAuth2UserService;

    // Construtor injetando o serviço que criamos para capturar os dados do Google
    public SecurityConfig(CustomOAuth2UserService customOAuth2UserService) {
        this.customOAuth2UserService = customOAuth2UserService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) // Desabilita CSRF para permitir requisições do React
            .cors(Customizer.withDefaults()) // CORREÇÃO: Ativa o CORS no padrão oficial do Spring Boot 3
            .headers(headers -> headers.frameOptions(frame -> frame.disable())) // Necessário para abrir o painel H2 sem travar em frames
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/products/**").permitAll() // Produtos públicos
                .requestMatchers("/api/products/**", "/api/user/**", "/api/orders/**").permitAll()  // Banco de dados liberado para desenvolvimento
                .requestMatchers("/login/**", "/oauth2/**").permitAll() // Libera as rotas de autenticação social do Google
                .anyRequest().authenticated()                    // Todo o resto continua protegido por barreira
            )
            .oauth2Login(oauth2 -> oauth2
                .userInfoEndpoint(userInfo -> userInfo.userService(customOAuth2UserService)) // Vincula o salvamento automático do usuário
                .defaultSuccessUrl("http://localhost:5173/", true) // Redireciona de volta para o React após o sucesso
            )
            .formLogin(form -> form.disable()) // Remove a tela cinza padrão do Spring Security
            .httpBasic(basic -> basic.disable()); // Remove o pop-up de senha do navegador

        return http.build();
    }
}