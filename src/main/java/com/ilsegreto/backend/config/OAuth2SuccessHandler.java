package com.ilsegreto.backend.config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;

@Component
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtUtil jwtUtil;

    // Injeta o utilitário do JWT que criamos no passo anterior
    public OAuth2SuccessHandler(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        
        // 1. Captura o usuário autenticado pelo Google
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String email = oAuth2User.getAttribute("email");

        if (email == null) {
            response.sendRedirect("https://ilsegreto-frontend.vercel.app/login?error=EmailNotFound");
            return;
        }

        // 2. Cria o Token JWT blindado para o celular do cliente aceitar
        String token = jwtUtil.generateToken(email);

        // 3. Define para onde redirecionar (Redirecionamento Inteligente)
        String targetUrl;
        if (System.getenv("RENDER") != null) {
            targetUrl = "https://ilsegreto-frontend.vercel.app/";
        } else {
            targetUrl = "http://localhost:5174/";
        }

        // 4. Constrói a URL final grudando o token de forma limpa (Ex: .../?token=eyJ...)
        String finalUrl = UriComponentsBuilder.fromUriString(targetUrl)
                .queryParam("token", token)
                .build().toUriString();

        // 5. Limpa os atributos de autenticação temporários da sessão e redireciona
        clearAuthenticationAttributes(request);
        getRedirectStrategy().sendRedirect(request, response, finalUrl);
    }
}