package com.example.ecommerce.servicelmpl;

import com.example.ecommerce.dao.BillDao;
import com.example.ecommerce.dao.CategoryDao;
import com.example.ecommerce.dao.ProductDao;
import com.example.ecommerce.service.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class DashboardServiceImpl implements DashboardService {

    @Autowired
    ProductDao productDao;

    @Autowired
    BillDao billDao;

    @Autowired
    CategoryDao categoryDao;

    @Override
    public ResponseEntity<Map<String, Object>> getCount() {
        Map<String,Object> map = new HashMap<>();
        map.put("category", categoryDao.count());
        map.put("bill", billDao.count());
        map.put("product", productDao.count());

        return new ResponseEntity<>(map, HttpStatus.OK);
    }
}
