package com.ilsegreto.backend.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.function.Function;

@Component
public class JwtUtil {

    // Chave secreta de pelo menos 32 caracteres para segurança do algoritmo HS256
    private final String SECRET_KEY = "IlSegretoDellaBellezzaSuperSecretKeyJwtToken2026!";
    
    // O token expira em 24 horas (em milissegundos)
    private final long JWT_EXPIRATION = 86400000; 

    private SecretKey getSigningKey() {
        byte[] keyBytes = SECRET_KEY.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    // 🚀 Gera o token JWT baseado no e-mail do usuário logado usando a nova API
    public String generateToken(String email) {
        return Jwts.builder()
                .subject(email)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + JWT_EXPIRATION))
                .signWith(getSigningKey()) // Identifica o algoritmo automaticamente pela chave
                .compact();
    }

    // Extrai o e-mail (subject) de dentro do token
    public String extractEmail(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    // Extrai a data de expiração
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    // 🚀 CORREÇÃO DA IMAGEM image_bcd425.png: Nova sintaxe do JJWT para descriptografar tokens
    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey()) // Substitui setSigningKey()
                .build() // Constrói o parser
                .parseSignedClaims(token) // Substitui parseClaimsJws()
                .getPayload(); // Substitui getBody()
    }

    // Verifica se o token já passou da data de validade
    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    // Valida o token comparando o e-mail do usuário e a expiração
    public Boolean validateToken(String token, String email) {
        final String extractedEmail = extractEmail(token);
        return (extractedEmail.equals(email) && !isTokenExpired(token));
    }
}