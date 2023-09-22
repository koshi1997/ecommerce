package com.example.ecommerce.restlmpl;

import com.example.ecommerce.contants.CafeContants;
import com.example.ecommerce.rest.DashboardRest;
import com.example.ecommerce.service.DashboardService;
import com.example.ecommerce.utils.CafeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class DashboardRestImpl implements DashboardRest {

    @Autowired
    DashboardService dashboardService;

    @Override
    public ResponseEntity<Map<String, Object>> getCount() {
        return dashboardService.getCount();
    }
}