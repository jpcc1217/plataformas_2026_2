package upb.prueba.clase.model;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class Product {
    
    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    private Long id;
    private String name;
    private double price;

    public Product() {
    }

    public Product(String name, double price) {
        if (price < 0) {
            throw new IllegalArgumentException("El precio no puede ser negativo");
        }
        this.name = name;
        this.price = price;
    }

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }



    
}
