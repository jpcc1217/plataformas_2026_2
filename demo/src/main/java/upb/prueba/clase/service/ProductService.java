package upb.prueba.clase.service;

import upb.prueba.clase.model.Product;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.stereotype.Service;

import upb.prueba.clase.model.ProductRepository;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }




    // private final List<Product> products = new ArrayList<>();

    // private final AtomicLong counter = new AtomicLong();

    // public ProductService() {
    //     // Agregar algunos productos de ejemplo
    //     products.add(new Product(counter.incrementAndGet(), "Producto 1", 10.0));
    //     products.add(new Product(counter.incrementAndGet(), "Producto 2", 20.0));
    //     products.add(new Product(counter.incrementAndGet(), "Producto 3", 30.0));
    // }

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public Product addProduct(String name, double price) {
        Product newProduct = new Product(name, price);
        return productRepository.save(newProduct);
    }


    
}
