package com.example.ecommerce.dao;

import com.example.ecommerce.POJO.Bill;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BillDao extends JpaRepository <Bill,Integer>{
}
