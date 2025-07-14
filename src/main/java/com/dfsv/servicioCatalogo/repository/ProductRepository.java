package com.dfsv.servicioCatalogo.repository;


import com.dfsv.servicioCatalogo.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
}