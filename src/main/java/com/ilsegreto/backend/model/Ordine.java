package com.ilsegreto.backend.model;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "tb_ordini")
public class Ordine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String codiceOrdine; // Ex: #ILSEGRETO-2026-XYZ
    private LocalDateTime dataOrdine;
    private Double totale;
    private String stato; // Consegnato, In Transito, Elaborazione
    private String prodottiSommario; // Resumo dos itens comprados

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    public Ordine() {}

    public Ordine(String codiceOrdine, Double totale, String stato, String prodottiSommario, User user) {
        this.codiceOrdine = codiceOrdine;
        this.dataOrdine = LocalDateTime.now();
        this.totale = totale;
        this.stato = stato;
        this.prodottiSommario = prodottiSommario;
        this.user = user;
    }

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getCodiceOrdine() { return codiceOrdine; }
    public void setCodiceOrdine(String codiceOrdine) { this.codiceOrdine = codiceOrdine; }
    public LocalDateTime getDataOrdine() { return dataOrdine; }
    public void setDataOrdine(LocalDateTime dataOrdine) { this.dataOrdine = dataOrdine; }
    public Double getTotale() { return totale; }
    public void setTotale(Double totale) { this.totale = totale; }
    public String getStato() { return stato; }
    public void setStato(String stato) { this.stato = stato; }
    public String getProdottiSommario() { return prodottiSommario; }
    public void setProdottiSommario(String prodottiSommario) { this.prodottiSommario = prodottiSommario; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
}