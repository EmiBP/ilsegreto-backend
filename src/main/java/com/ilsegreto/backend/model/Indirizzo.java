package com.ilsegreto.backend.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "tb_indirizzi")
public class Indirizzo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String via;
    private String citta;
    private String cap;
    private String provincia;
    private String nazione;

    public Indirizzo() {}

    public Indirizzo(String via, String citta, String cap, String provincia, String nazione) {
        this.via = via;
        this.citta = citta;
        this.cap = cap;
        this.provincia = provincia;
        this.nazione = nazione;
    }

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getVia() { return via; }
    public void setVia(String via) { this.via = via; }
    public String getCitta() { return citta; }
    public void setCitta(String citta) { this.citta = citta; }
    public String getCap() { return cap; }
    public void setCap(String cap) { this.cap = cap; }
    public String getProvincia() { return provincia; }
    public void setProvincia(String provincia) { this.provincia = provincia; }
    public String getNazione() { return nazione; }
    public void setNazione(String nazione) { this.nazione = nazione; }
}