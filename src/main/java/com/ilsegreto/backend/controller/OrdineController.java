package com.ilsegreto.backend.controller;

import java.util.Optional;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ilsegreto.backend.model.Ordine;
import com.ilsegreto.backend.model.User;
import com.ilsegreto.backend.repository.UserRepository;

@RestController
@RequestMapping("/api/orders")
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true", allowedHeaders = "*")
public class OrdineController {

    private final UserRepository userRepository;

    public OrdineController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @PostMapping("/create")
    public ResponseEntity<?> createOrder(@AuthenticationPrincipal OAuth2User principal, @RequestBody OrderRequest requestData) {
        String email = null;

        // 1. Tenta pegar o e-mail pela sessão do Google (cookie)
        if (principal != null) {
            email = principal.getAttribute("email");
        } 
        
        // 2. Se o cookie falhar por causa do navegador, pega o e-mail do formulário do React
        if (email == null && requestData.getEmailUtente() != null) {
            email = requestData.getEmailUtente();
        }

        if (email == null || email.trim().isEmpty()) {
            return ResponseEntity.status(401).body("Errore: Utente non identificato. Effettua il login.");
        }

        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(404).body("Errore: Utente non trovato nel database per email: " + email);
        }

        User user = userOpt.get();

        // Cria o código do pedido elegante
        String codiceOrdine = "#ILSEGRETO-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        // Cria a entidade associando o pedido ao usuário encontrado
        Ordine nuovoOrdine = new Ordine(codiceOrdine, requestData.getTotale(), "In Elaborazione", requestData.getProdotti(), user);
        
        user.getOrdini().add(nuovoOrdine);
        userRepository.save(user); // Salva o usuário e o pedido por cascata

        return ResponseEntity.ok(nuovoOrdine);
    }

    // DTO (Classe auxiliar) para receber o JSON do React perfeitamente sem erros de tipo
    public static class OrderRequest {
        private Double totale;
        private String prodotti;
        private String emailUtente;

        public OrderRequest() {}

        public Double getTotale() { return totale; }
        public void setTotale(Double totale) { this.totale = totale; }

        public String getProdotti() { return prodotti; }
        public void setProdotti(String prodotti) { this.prodotti = prodotti; }

        public String getEmailUtente() { return emailUtente; }
        public void setEmailUtente(String emailUtente) { this.emailUtente = emailUtente; }
    }
}