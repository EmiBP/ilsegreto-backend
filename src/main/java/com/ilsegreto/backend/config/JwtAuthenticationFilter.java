package com.ilsegreto.backend.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    public JwtAuthenticationFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // 1. Extrai o cabeçalho "Authorization" da requisição vinda do React
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String userEmail;

        // 2. Se o cabeçalho estiver vazio ou não começar com "Bearer ", ignora e segue o fluxo
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // 3. Recorta a String para pegar apenas o Token purificado (retira a palavra "Bearer ")
        jwt = authHeader.substring(7);
        
        try {
            userEmail = jwtUtil.extractEmail(jwt);

            // 4. Se encontrou o e-mail e o Spring ainda não autenticou essa requisição nesta thread
            if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                
                // Valida se o token não expirou e bate com o e-mail
                if (jwtUtil.validateToken(jwt, userEmail)) {
                    
                    // Cria o objeto de autenticação que o Spring Security exige internamente
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userEmail, 
                            null, 
                            Collections.emptyList() // Lista de roles/permissões vazia por enquanto
                    );
                    
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    
                    // 🚀 AUTENTICAÇÃO CONCLUÍDA: injeta o usuário no contexto de segurança da requisição atual
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
        } catch (Exception e) {
            // Se o token estiver corrompido ou adulterado, o filtro apenas barra silenciosamente
            System.err.println("Errore durante a validação do token JWT no filtro: " + e.getMessage());
        }

        // Continua o fluxo normal para os próximos filtros ou para o Controller
        filterChain.doFilter(request, response);
    }
}