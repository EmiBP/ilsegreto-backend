// src/main/java/com/ilsegreto/backend/model/Product.java
package com.ilsegreto.backend.model;

public class Product {
    private Long id;
    private String name;
    private String category;
    private String brand;
    private double price;
    private String image;

    // Construtor padrão
    public Product() {}

    // Construtor completo para criarmos os produtos de teste
    public Product(Long id, String name, String category, String brand, double price, String image) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.brand = brand;
        this.price = price;
        this.image = image;
    }

    // Getters e Setters (Necessários para o Spring transformar o objeto em texto/JSON)
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getBrand() { return brand; }
    public void setBrand(String brand) { this.brand = brand; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }
}