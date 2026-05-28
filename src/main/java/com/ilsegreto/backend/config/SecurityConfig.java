package com.ilsegreto.backend.config;

import java.util.Arrays;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.ilsegreto.backend.service.CustomOAuth2UserService;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final CustomOAuth2UserService customOAuth2UserService;
    private final OAuth2SuccessHandler oAuth2SuccessHandler;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    // Construtor atualizado injetando os novos componentes do ecossistema JWT
    public SecurityConfig(CustomOAuth2UserService customOAuth2UserService,
                          OAuth2SuccessHandler oAuth2SuccessHandler,
                          JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.customOAuth2UserService = customOAuth2UserService;
        this.oAuth2SuccessHandler = oAuth2SuccessHandler;
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) // Desabilita CSRF para permitir requisições do React
            .cors(Customizer.withDefaults()) // Ativa o CORS buscando o Bean 'corsConfigurationSource' definido abaixo
            .headers(headers -> headers.frameOptions(frame -> frame.disable())) // Necessário para abrir o painel H2 sem travar em frames
            
            // 🚀 MUDANÇA CRUCIAL: Transforma a API em STATELESS. O servidor não guarda mais sessões em cookies na memória.
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/h2-console/**").permitAll() // Garante acesso livre ao console H2 se precisar
                .requestMatchers("/api/products/**", "/api/user/**", "/api/orders/**").permitAll() // Liberado para desenvolvimento temporário
                .requestMatchers("/login/**", "/oauth2/**").permitAll() // Libera as rotas de autenticação social do Google
                .anyRequest().authenticated() // Todo o resto continua protegido por barreira
            )
            .oauth2Login(oauth2 -> oauth2
                .userInfoEndpoint(userInfo -> userInfo.userService(customOAuth2UserService)) // Vincula o salvamento automático do usuário
                
                // 🚀 ADICIONADO: O handler que gera o JWT e joga o token na URL ao redirecionar para o React
                .successHandler(oAuth2SuccessHandler)
            )
            .formLogin(form -> form.disable()) // Remove a tela cinza padrão do Spring Security
            .httpBasic(basic -> basic.disable()); // Remove o pop-up de senha do navegador

        // 🚀 ADICIONADO: Adiciona o nosso filtro JWT na fila de verificação do Spring Security antes do filtro padrão de autenticação
        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // Bean de CORS global configurado para dar suporte ao tráfego do Token nos Headers
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
        
        // Permite os cabeçalhos tradicionais incluindo o 'Authorization' que transportará o token
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Cache-Control", "Content-Type"));
        
        configuration.setAllowCredentials(true); 
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration); 
        return source;
    }
}