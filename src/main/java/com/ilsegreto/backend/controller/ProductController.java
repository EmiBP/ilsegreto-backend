// src/main/java/com/ilsegreto/backend/controller/ProductController.java
package com.ilsegreto.backend.controller;

import java.util.Arrays;
import java.util.List;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ilsegreto.backend.model.Product;

@RestController
@RequestMapping("/api/products")
@CrossOrigin(origins = "http://localhost:5173") // Permite que o seu React (porta 5173) acesse o Java (porta 8080)
public class ProductController {

    @GetMapping
    public List<Product> getProducts() {
        // Simulando a lista que enviamos antes no React, agora saindo diretamente do servidor Java!
        return Arrays.asList(
            new Product(1L, "Abito Elegante in Seta", "Abbigliamento", "Gucci", 129.00, "https://images.unsplash.com/photo-1595777457583-95e059d581b8?w=400"),
            new Product(2L, "Borsa a Tracolla in Pelle", "Accessori", "Prada", 89.90, "https://images.unsplash.com/photo-1584917865442-de89df76afd3?w=400"),
            new Product(3L, "Profumo Luxury Incanto 50ml", "Profumi", "Chanel", 75.00, "https://images.unsplash.com/photo-1541643600914-78b084683601?w=400"),
            new Product(4L, "Pantaloni Casual Slim", "Abbigliamento", "Zara", 49.90, "https://images.unsplash.com/photo-1624378439575-d8705ad7ae80?w=400"),
            new Product(5L, "Calze in Cotone Premium", "Abbigliamento", "Armani", 15.00, "https://images.unsplash.com/photo-1582966772680-860e372bb558?w=400"),
            new Product(6L, "Scarpe col Tacco Eleganti", "Abbigliamento", "Prada", 199.00, "https://images.unsplash.com/photo-1543163521-1bf539c55dd2?w=400"),
            new Product(7L, "Completo Intimo Pizzo", "Abbigliamento", "Yamamay", 39.90, "https://images.unsplash.com/photo-1616150638538-ffb0679a3fc4?w=400")
        );
    }
}