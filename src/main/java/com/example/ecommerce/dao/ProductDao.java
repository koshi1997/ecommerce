package com.example.ecommerce.dao;

import com.example.ecommerce.POJO.Product;
import com.example.ecommerce.wrapper.ProductWrapper;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductDao extends JpaRepository<Product,Integer> {

    List<ProductWrapper> getAllProduct();
}