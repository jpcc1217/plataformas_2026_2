package upb.prueba.clase.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import upb.prueba.clase.service.ProductService;
import upb.prueba.clase.model.Product;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@RestController
@RequestMapping("api/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public List<Product> getAllProducts() {
        return productService.getAllProducts();
    }

    @PostMapping
    @ResponseStatus(org.springframework.http.HttpStatus.CREATED)
    public Product createProduct(@RequestBody CreateProductRequest request) {
        return productService.addProduct(request.name(), request.price());
    }
    
}

record CreateProductRequest(String name, double price) {}