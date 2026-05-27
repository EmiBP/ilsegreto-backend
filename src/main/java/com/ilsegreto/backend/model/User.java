package com.ilsegreto.backend.model;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "tb_users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String name;
    private String email;
    private String provider; 
    private String providerId;
    private String telefono;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "indirizzo_id", referencedColumnName = "id")
    private Indirizzo indirizzo;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "pagamento_id", referencedColumnName = "id")
    private DatiPagamento datiPagamento;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Ordine> ordini = new ArrayList<>();

    public User() {}

    public User(String name, String email, String provider, String providerId) {
        this.name = name;
        this.email = email;
        this.provider = provider;
        this.providerId = providerId;
        this.telefono = "Non inserito";
        this.indirizzo = new Indirizzo("Da completare", "Roma", "00100", "RM", "Italia");
        this.datiPagamento = new DatiPagamento("Nessuno", "Nessuna carta salvata", "TOKEN_VUOTO");
    }

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getProvider() { return provider; }
    public void setProvider(String provider) { this.provider = provider; }
    public String getProviderId() { return providerId; }
    public void setProviderId(String providerId) { this.providerId = providerId; }
    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
    public Indirizzo getIndirizzo() { return indirizzo; }
    public void setIndirizzo(Indirizzo indirizzo) { this.indirizzo = indirizzo; }
    public DatiPagamento getDatiPagamento() { return datiPagamento; }
    public void setDatiPagamento(DatiPagamento datiPagamento) { this.datiPagamento = datiPagamento; }
    public List<Ordine> getOrdini() { return ordini; }
    public void setOrdini(List<Ordine> ordini) { this.ordini = ordini; }
}