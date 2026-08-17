package com.se191116.mvc.homework1.repository;

import com.se191116.mvc.homework1.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
}
