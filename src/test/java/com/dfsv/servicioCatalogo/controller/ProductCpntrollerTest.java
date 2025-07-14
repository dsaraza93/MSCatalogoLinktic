package com.dfsv.servicioCatalogo.controller;

import com.dfsv.servicioCatalogo.model.Product;
import com.dfsv.servicioCatalogo.service.ProductService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@ExtendWith(SpringExtension.class)
@WebMvcTest(ProductCpntroller.class)
class ProductCpntrollerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService productService;

    private final ObjectMapper mapper = new ObjectMapper();

    /*──────────── GET /api/products ────────────*/
    @Test
    @DisplayName("Devuelve todos los productos")
    void getAllProducts() throws Exception {
        Product p1 = buildProduct(1L, "Laptop",   "Gaming",   10, 1500.0);
        Product p2 = buildProduct(2L, "Mouse",    "Wireless", 50,   25.0);
        Mockito.when(productService.getAllProducts()).thenReturn(List.of(p1, p2));

        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].name", is("Laptop")))
                .andExpect(jsonPath("$[1].id", is(2)));
    }

    /*──────────── GET /api/products/{id} ────────────*/
    @Test
    @DisplayName("Devuelve 200 cuando existe el producto")
    void getProductByIdFound() throws Exception {
        Product p = buildProduct(1L, "Laptop", "Gaming", 10, 1500.0);
        Mockito.when(productService.getProductById(1L)).thenReturn(Optional.of(p));

        mockMvc.perform(get("/api/products/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Laptop")));
    }

    @Test
    @DisplayName("Devuelve 404 cuando no existe el producto")
    void getProductByIdNotFound() throws Exception {
        Mockito.when(productService.getProductById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/products/99"))
                .andExpect(status().isNotFound());
    }

    /*──────────── POST /api/products ────────────*/
    @Test
    @DisplayName("Crea y devuelve el producto")
    void createProduct() throws Exception {
        Product request = buildProduct(null, "Keyboard", "Mechanical", 20,  80.0);
        Product saved   = buildProduct(3L,  "Keyboard", "Mechanical", 20,  80.0);
        Mockito.when(productService.saveProduct(any(Product.class))).thenReturn(saved);

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isOk())          // El controlador responde 200
                .andExpect(jsonPath("$.id", is(3)))
                .andExpect(jsonPath("$.name", is("Keyboard")));
    }

    /*──────────── PUT /api/products/{id} ────────────*/
    @Test
    @DisplayName("Actualiza y devuelve el producto")
    void updateProduct() throws Exception {
        Product request = buildProduct(null, "Laptop Pro", "Gaming v2", 8, 1800.0);
        Product updated = buildProduct(1L,  "Laptop Pro", "Gaming v2", 8, 1800.0);
        Mockito.when(productService.updateProduct(eq(1L), any(Product.class))).thenReturn(updated);

        mockMvc.perform(put("/api/products/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Laptop Pro")))
                .andExpect(jsonPath("$.quantity", is(8)));
    }

    /*──────────── DELETE /api/products/{id} ────────────*/
    @Test
    @DisplayName("Elimina el producto y devuelve 204")
    void deleteProduct() throws Exception {
        mockMvc.perform(delete("/api/products/1"))
                .andExpect(status().isNoContent());

        Mockito.verify(productService).deleteProduct(1L);
    }

    /*──────────── Utilidad interna ────────────*/
    private Product buildProduct(Long id, String name, String description,
                                 int quantity, double price) {
        Product p = new Product();     // constructor vacío
        p.setId(id);
        p.setName(name);
        p.setDescription(description);
        p.setQuantity(quantity);
        p.setPrice(price);
        return p;
    }
}
