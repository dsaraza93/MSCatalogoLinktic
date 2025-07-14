package com.dfsv.servicioCatalogo.service;

import com.dfsv.servicioCatalogo.model.Product;
import com.dfsv.servicioCatalogo.repository.ProductRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor         // inyección por constructor
public class ProductService {

    private final ProductRepository productRepository;

    /* ───────── LISTAR ───────── */
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    /* ──────── OBTENER ──────── */
    public Optional<Product> getProductById(Long id) {
        return productRepository.findById(id);
    }

    /* ───────── CREAR ───────── */
    public Product saveProduct(Product product) {
        return productRepository.save(product);
    }

    /* ─────── ACTUALIZAR ────── */
    public Product updateProduct(Long id, Product incoming) {
        Product existing = productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Producto " + id + " no encontrado"));

        /* Copia todas las propiedades con el mismo nombre.
           Excluimos 'id' para no sobreescribir la PK. */
        BeanUtils.copyProperties(incoming, existing, "id");

        return productRepository.save(existing);
    }

    /* ─────── ELIMINAR ─────── */
    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }
}

