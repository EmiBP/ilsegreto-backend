package com.ilsegreto.backend.config;

import java.util.Arrays;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.ilsegreto.backend.service.CustomOAuth2UserService;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final CustomOAuth2UserService customOAuth2UserService;

    // Construtor injetando o serviço que captura os dados do Google
    public SecurityConfig(CustomOAuth2UserService customOAuth2UserService) {
        this.customOAuth2UserService = customOAuth2UserService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) // Desabilita CSRF para permitir requisições do React
            .cors(Customizer.withDefaults()) // Ativa o CORS buscando o Bean 'corsConfigurationSource' definido abaixo
            .headers(headers -> headers.frameOptions(frame -> frame.disable())) // Necessário para abrir o painel H2 sem travar em frames
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/h2-console/**").permitAll() // Garante acesso livre ao console H2 se precisar
                .requestMatchers("/api/products/**", "/api/user/**", "/api/orders/**").permitAll()  // Banco de dados liberado para desenvolvimento
                .requestMatchers("/login/**", "/oauth2/**").permitAll() // Libera as rotas de autenticação social do Google
                .anyRequest().authenticated() // Todo o resto continua protegido por barreira
            )
            .oauth2Login(oauth2 -> oauth2
                .userInfoEndpoint(userInfo -> userInfo.userService(customOAuth2UserService)) // Vincula o salvamento automático do usuário
                
                // 🚀 REDIRECIONAMENTO INTELIGENTE: Se o app rodar na Render, manda para a Vercel. Se rodar na sua máquina, usa o localhost.
                .defaultSuccessUrl(
                    System.getenv("RENDER") != null 
                    ? "https://ilsegreto-frontend.vercel.app/" 
                    : "http://localhost:5174/", 
                    true
                )
            )
            .formLogin(form -> form.disable()) // Remove a tela cinza padrão do Spring Security
            .httpBasic(basic -> basic.disable()); // Remove o pop-up de senha do navegador

        return http.build();
    }

    // 🚀 BEAN DE CONFIGURAÇÃO GLOBAL DO CORS - Resolve o erro 'Failed to fetch'
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        // Autoriza as portas locais e o domínio real da Vercel a se comunicarem com a API
        configuration.setAllowedOrigins(Arrays.asList(
            "http://localhost:5173", 
            "http://localhost:5174", 
            "https://ilsegreto-frontend.vercel.app" 
        ));
        
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Cache-Control", "Content-Type"));
        configuration.setAllowCredentials(true); // Permite envio de cookies/sessões entre domínios
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration); // Aplica essa regra em todas as rotas da API
        return source;
    }
}