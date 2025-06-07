package com.slotout.v1.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.slotout.v1.dto.ServiceRegister;
import com.slotout.v1.models.Service;
import com.slotout.v1.services.ServiceService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class ServiceController {
    
    private static final Logger logger = LoggerFactory.getLogger(ServiceController.class);
    
    @Autowired
    private ServiceService serviceService;
    
    @PostMapping("/tenants/{tenantId}/services")
    public ResponseEntity<?> createService(@PathVariable Long tenantId, @RequestBody ServiceRegister serviceDto) {
        try {
            String result = serviceService.createService(tenantId, serviceDto);
            if (result.equals("Service created successfully")) {
                return ResponseEntity.ok().body(Map.of("message", result));
            } else {
                return ResponseEntity.badRequest().body(Map.of("error", result));
            }
        } catch (Exception e) {
            logger.error("Error in ServiceController.createService: ", e);
            return ResponseEntity.internalServerError().body(Map.of("error", "Internal server error"));
        }
    }
    
    @GetMapping("/tenants/{tenantId}/services")
    public ResponseEntity<?> getServicesByTenant(@PathVariable Long tenantId) {
        try {
            List<Service> services = serviceService.getServicesByTenant(tenantId);
            return ResponseEntity.ok().body(services);
        } catch (Exception e) {
            logger.error("Error in ServiceController.getServicesByTenant: ", e);
            return ResponseEntity.internalServerError().body(Map.of("error", "Internal server error"));
        }
    }
    
    @PutMapping("/services/{serviceId}")
    public ResponseEntity<?> updateService(@PathVariable Long serviceId, @RequestBody ServiceRegister serviceDto) {
        try {
            String result = serviceService.updateService(serviceId, serviceDto);
            if (result.equals("Service updated successfully")) {
                return ResponseEntity.ok().body(Map.of("message", result));
            } else {
                return ResponseEntity.badRequest().body(Map.of("error", result));
            }
        } catch (Exception e) {
            logger.error("Error in ServiceController.updateService: ", e);
            return ResponseEntity.internalServerError().body(Map.of("error", "Internal server error"));
        }
    }
    
    @DeleteMapping("/services/{serviceId}")
    public ResponseEntity<?> deleteService(@PathVariable Long serviceId) {
        try {
            String result = serviceService.deleteService(serviceId);
            if (result.equals("Service deleted successfully")) {
                return ResponseEntity.ok().body(Map.of("message", result));
            } else {
                return ResponseEntity.badRequest().body(Map.of("error", result));
            }
        } catch (Exception e) {
            logger.error("Error in ServiceController.deleteService: ", e);
            return ResponseEntity.internalServerError().body(Map.of("error", "Internal server error"));
        }
    }
}

