package com.bajrix.marketplace.repository;

import com.bajrix.marketplace.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
    Page<Product> findByNameContainingIgnoreCaseOrCategoryContainingIgnoreCase(String name, String category, Pageable pageable);
}

