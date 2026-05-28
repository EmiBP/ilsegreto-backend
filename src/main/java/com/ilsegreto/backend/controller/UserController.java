package com.ilsegreto.backend.controller;

import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ilsegreto.backend.model.DatiPagamento;
import com.ilsegreto.backend.model.Indirizzo;
import com.ilsegreto.backend.model.User;
import com.ilsegreto.backend.repository.UserRepository;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/api/user")
// 🚀 CORREÇÃO DO CORS: Liberadas as duas portas locais explicitamente para não travar nos seus testes
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:5174"}, allowCredentials = "true")
public class UserController {

    private final UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/profile")
    public ResponseEntity<?> getUserProfile(@AuthenticationPrincipal OAuth2User principal) {
        if (principal == null) {
            return ResponseEntity.status(401).body("Utente non autenticato");
        }
        String email = principal.getAttribute("email");
        return userRepository.findByEmail(email)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(404).build());
    }

    @PutMapping("/update")
    public ResponseEntity<?> updateProfile(@AuthenticationPrincipal OAuth2User principal, @RequestBody User dadosAtualizados) {
        if (principal == null) return ResponseEntity.status(401).body("Non autorizzato");
        
        String email = principal.getAttribute("email");
        Optional<User> userOpt = userRepository.findByEmail(email);
        
        if (userOpt.isEmpty()) return ResponseEntity.status(404).body("Utente non trovato");
        
        User user = userOpt.get();
        user.setTelefono(dadosAtualizados.getTelefono());
        
        // 🚀 Processamento seguro do objeto Indirizzo
        if (dadosAtualizados.getIndirizzo() != null && user.getIndirizzo() != null) {
            Indirizzo ind = user.getIndirizzo();
            Indirizzo indNovosDados = dadosAtualizados.getIndirizzo();
            
            ind.setVia(indNovosDados.getVia());
            ind.setCitta(indNovosDados.getCitta());
            ind.setCap(indNovosDados.getCap());
            ind.setProvincia(indNovosDados.getProvincia());
        }
        
        if (dadosAtualizados.getDatiPagamento() != null && user.getDatiPagamento() != null) {
            DatiPagamento pag = user.getDatiPagamento();
            DatiPagamento pagNovosDados = dadosAtualizados.getDatiPagamento();
            
            pag.setCircuito(pagNovosDados.getCircuito());
            pag.setCartaMascherata(pagNovosDados.getCartaMascherata());
        }
        
        userRepository.save(user);
        return ResponseEntity.ok(user);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request, HttpServletResponse response) {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            new SecurityContextLogoutHandler().logout(request, response, auth);
        }
        return ResponseEntity.ok().body("Logout effettuato");
    }
}