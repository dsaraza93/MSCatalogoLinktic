package com.dfsv.servicioCatalogo.service;

import com.dfsv.servicioCatalogo.model.Product;
import com.dfsv.servicioCatalogo.repository.ProductRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    /*──────────── LISTAR ────────────*/
    @Test
    @DisplayName("findAll devuelve la lista completa")
    void getAllProducts() {
        Product p1 = buildProduct(1L, "Laptop", "Gaming", 10, 1500.0);
        Product p2 = buildProduct(2L, "Mouse",  "Wireless", 50,   25.0);
        when(productRepository.findAll()).thenReturn(List.of(p1, p2));

        List<Product> result = productService.getAllProducts();

        assertEquals(2, result.size());
        assertEquals("Laptop", result.get(0).getName());
        verify(productRepository).findAll();
    }

    /*──────────── OBTENER ────────────*/
    @Test
    @DisplayName("findById devuelve Optional con producto")
    void getProductByIdFound() {
        Product p = buildProduct(1L, "Laptop", "Gaming", 10, 1500.0);
        when(productRepository.findById(1L)).thenReturn(Optional.of(p));

        Optional<Product> result = productService.getProductById(1L);

        assertTrue(result.isPresent());
        assertEquals("Laptop", result.get().getName());
        verify(productRepository).findById(1L);
    }

    @Test
    @DisplayName("findById devuelve Optional vacío si no existe")
    void getProductByIdNotFound() {
        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<Product> result = productService.getProductById(99L);

        assertTrue(result.isEmpty());
        verify(productRepository).findById(99L);
    }

    /*──────────── CREAR ────────────*/
    @Test
    @DisplayName("save delega en el repositorio")
    void saveProduct() {
        Product toSave = buildProduct(null, "Keyboard", "Mechanical", 20, 80.0);
        Product saved  = buildProduct(3L,   "Keyboard", "Mechanical", 20, 80.0);
        when(productRepository.save(toSave)).thenReturn(saved);

        Product result = productService.saveProduct(toSave);

        assertEquals(3L, result.getId());
        verify(productRepository).save(toSave);
    }

    /*──────────── ACTUALIZAR ────────────*/
    @Test
    @DisplayName("updateProduct copia propiedades y guarda")
    void updateProduct() {
        Product existing = buildProduct(1L, "Laptop", "Gaming", 10, 1500.0);
        Product incoming = buildProduct(null,"Laptop Pro","Gaming v2", 8, 1800.0);

        when(productRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(productRepository.save(any(Product.class))).thenAnswer(inv -> inv.getArgument(0));

        Product result = productService.updateProduct(1L, incoming);

        assertEquals(1L, result.getId());                 // ID no cambia
        assertEquals("Laptop Pro", result.getName());     // Propiedades copiadas
        assertEquals(8, result.getQuantity());

        // Captura para verificar que se envía al repositorio el objeto modificado
        ArgumentCaptor<Product> captor = ArgumentCaptor.forClass(Product.class);
        verify(productRepository).save(captor.capture());
        assertEquals("Laptop Pro", captor.getValue().getName());
    }

    @Test
    @DisplayName("updateProduct lanza excepción si no existe")
    void updateProductNotFound() {
        Product incoming = buildProduct(null,"Any","Desc",1,1.0);
        when(productRepository.findById(42L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> productService.updateProduct(42L, incoming));
    }

    /*──────────── ELIMINAR ────────────*/
    @Test
    @DisplayName("deleteProduct delega en el repositorio")
    void deleteProduct() {
        productService.deleteProduct(5L);
        verify(productRepository).deleteById(5L);
    }

    /*──────────── Utilidad interna ────────────*/
    private Product buildProduct(Long id, String name, String description,
                                 int quantity, double price) {
        Product p = new Product();   // constructor vacío
        p.setId(id);
        p.setName(name);
        p.setDescription(description);
        p.setQuantity(quantity);
        p.setPrice(price);
        return p;
    }
}
