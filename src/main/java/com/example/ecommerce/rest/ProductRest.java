package com.example.ecommerce.rest;

import com.example.ecommerce.wrapper.ProductWrapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RequestMapping(path = "/product")
public interface ProductRest {

    @PostMapping(path = "/add")
    ResponseEntity<String> addNewProduct(@RequestBody(required = true) Map<String,String> requestMap);

    @GetMapping(path = "/get")
    ResponseEntity<List<ProductWrapper>> getAllProduct();

    @PostMapping("/update")
    ResponseEntity<String> updateProduct(@RequestBody(required = true) Map<String,String> requestMap);

    @PostMapping(path = "/delete/{id}")
    ResponseEntity<String> deleteProduct(@PathVariable(required = true) Integer id);

    @PostMapping(path = "/updateStatus")
    ResponseEntity<String> updateStatus(@RequestBody(required = true) Map<String,String> requestMap);

    @GetMapping(path = "/getByCategory/{id}")
    ResponseEntity<List<ProductWrapper>> getByCategory(@PathVariable Integer id);

    @GetMapping(path = "/getById/{id}")
    ResponseEntity<ProductWrapper> getProductById(@PathVariable Integer id);





}
