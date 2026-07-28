package upb.prueba.clase.model;

public record Product(Long id, String name,  double price) {
    
    public Product {
        if (price < 0) {
            throw new IllegalArgumentException("El precio no puede ser negativo");
        }
    }
}
