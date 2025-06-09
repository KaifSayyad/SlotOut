package com.slotout.v1.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.slotout.v1.dto.response.ApiResponseDto;
import com.slotout.v1.dto.request.ServiceRequest;
import com.slotout.v1.dto.response.ServiceResponseDto;
import com.slotout.v1.models.Service;
import com.slotout.v1.services.ServiceService;

import java.util.List;

@Tag(name = "Service", description = "Service management APIs")
@RestController
@RequestMapping("/api")
public class ServiceController {
    
    private static final Logger logger = LoggerFactory.getLogger(ServiceController.class);
    
    @Autowired
    private ServiceService serviceService;
    
    @Operation(summary = "Create a new service", description = "Creates a new service for a tenant.")
    @PostMapping("/tenants/{tenantId}/services")
    public ResponseEntity<?> createService(@PathVariable Long tenantId, @RequestBody ServiceRequest serviceDto) {
        try {
            ServiceResponseDto response = serviceService.createService(tenantId, serviceDto);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ApiResponseDto(null, e.getMessage()));
        } catch (Exception e) {
            logger.error("Error in ServiceController.createService: ", e);
            return ResponseEntity.internalServerError().body(new ApiResponseDto(null, "Internal server error"));
        }
    }
    
    @Operation(summary = "Get services by tenant", description = "Retrieves all services for a tenant.")
    @GetMapping("/tenants/{tenantId}/services")
    public ResponseEntity<?> getServicesByTenant(@PathVariable Long tenantId) {
        try {
            List<Service> services = serviceService.getServicesByTenant(tenantId);
            List<ServiceResponseDto> response = services.stream()
                .map(s -> new ServiceResponseDto(s.getId(), s.getName(), s.getDescription(), s.getIsActive(), null))
                .toList();
            return ResponseEntity.ok().body(response);
        } catch (Exception e) {
            logger.error("Error in ServiceController.getServicesByTenant: ", e);
            ApiResponseDto error = new ApiResponseDto(null, "Internal server error");
            return ResponseEntity.internalServerError().body(error);
        }
    }
    
    @Operation(summary = "Update a service", description = "Updates an existing service.")
    @PutMapping("/services/{serviceId}")
    public ResponseEntity<?> updateService(@PathVariable Long serviceId, @RequestBody ServiceRequest serviceDto) {
        try {
            String result = serviceService.updateService(serviceId, serviceDto);
            ServiceResponseDto response = new ServiceResponseDto(serviceId, serviceDto.getName(), serviceDto.getDescription(), true, result);
            if (result.equals("Service updated successfully")) {
                return ResponseEntity.ok().body(response);
            } else {
                return ResponseEntity.badRequest().body(response);
            }
        } catch (Exception e) {
            logger.error("Error in ServiceController.updateService: ", e);
            ApiResponseDto error = new ApiResponseDto(null, "Internal server error");
            return ResponseEntity.internalServerError().body(error);
        }
    }
    
    @Operation(summary = "Delete a service", description = "Deletes a service.")
    @DeleteMapping("/services/{serviceId}")
    public ResponseEntity<?> deleteService(@PathVariable Long serviceId) {
        try {
            String result = serviceService.deleteService(serviceId);
            ServiceResponseDto response = new ServiceResponseDto(serviceId, null, null, false, result);
            if (result.equals("Service deleted successfully")) {
                return ResponseEntity.ok().body(response);
            } else {
                return ResponseEntity.badRequest().body(response);
            }
        } catch (Exception e) {
            logger.error("Error in ServiceController.deleteService: ", e);
            ApiResponseDto error = new ApiResponseDto(null, "Internal server error");
            return ResponseEntity.internalServerError().body(error);
        }
    }
}

