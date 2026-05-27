package com.ilsegreto.backend.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "tb_dati_pagamento")
public class DatiPagamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String circuito; // Visa, Mastercard, etc.
    private String cartaMascherata; // Ex: **** **** **** 1234
    private String tokenPagamento; // Simulação do Token do Stripe/PayPal

    public DatiPagamento() {}

    public DatiPagamento(String circuito, String cartaMascherata, String tokenPagamento) {
        this.circuito = circuito;
        this.cartaMascherata = cartaMascherata;
        this.tokenPagamento = tokenPagamento;
    }

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getCircuito() { return circuito; }
    public void setCircuito(String circuito) { this.circuito = circuito; }
    public String getCartaMascherata() { return cartaMascherata; }
    public void setCartaMascherata(String cartaMascherata) { this.cartaMascherata = cartaMascherata; }
    public String getTokenPagamento() { return tokenPagamento; }
    public void setTokenPagamento(String tokenPagamento) { this.tokenPagamento = tokenPagamento; }
}