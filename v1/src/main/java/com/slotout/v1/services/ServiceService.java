package com.slotout.v1.services;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.slotout.v1.dto.request.ServiceRequest;
import com.slotout.v1.dto.response.ServiceResponseDto;
import com.slotout.v1.models.Tenant;
import com.slotout.v1.repositories.ServiceRepo;
import com.slotout.v1.repositories.TenantRepo;

@Service
public class ServiceService {
    
    private static final Logger logger = LoggerFactory.getLogger(ServiceService.class);
    
    @Autowired
    private ServiceRepo serviceRepo;
    
    @Autowired
    private TenantRepo tenantRepo;
    
    public ServiceResponseDto createService(Long tenantId, ServiceRequest serviceDto) {
        try {
            Optional<Tenant> tenantOpt = tenantRepo.findById(tenantId);
            if (tenantOpt.isEmpty()) {
                throw new IllegalArgumentException("Tenant not found");
            }
            Tenant tenant = tenantOpt.get();
            com.slotout.v1.models.Service service = serviceDto.getServiceObject(tenant);
            serviceRepo.save(service);
            logger.info("Service created successfully: " + service.getName() + " for tenant: " + tenant.getEmail());
            return new ServiceResponseDto(service.getId(), service.getName(), service.getDescription(), Boolean.TRUE.equals(service.getIsActive()), "Service created successfully");
        } catch (Exception e) {
            logger.error("Error creating service: ", e);
            throw e;
        }
    }
    
    public List<com.slotout.v1.models.Service> getServicesByTenant(Long tenantId) {
        try {
            Optional<Tenant> tenantOpt = tenantRepo.findById(tenantId);
            if (tenantOpt.isEmpty()) {
                return List.of();
            }
            
            return serviceRepo.findByTenant(tenantOpt.get());
            
        } catch (Exception e) {
            logger.error("Error fetching services for tenant: ", e);
            return List.of();
        }
    }
    
    public String updateService(Long serviceId, ServiceRequest serviceDto) {
        try {
            Optional<com.slotout.v1.models.Service> serviceOpt = serviceRepo.findById(serviceId);
            if (serviceOpt.isEmpty()) {
                return "Service not found";
            }
            
            com.slotout.v1.models.Service existingService = serviceOpt.get();
            
            // Update fields
            if (serviceDto.getName() != null) {
                existingService.setName(serviceDto.getName());
            }
            if (serviceDto.getDescription() != null) {
                existingService.setDescription(serviceDto.getDescription());
            }
            if (serviceDto.getPrice() != null) {
                existingService.setPrice(serviceDto.getPrice());
            }
            if (serviceDto.getDuration() != null) {
                com.slotout.v1.models.Service tempService = serviceDto.getServiceObject(existingService.getTenant());
                existingService.setDuration(tempService.getDuration());
            }
            
            serviceRepo.save(existingService);
            logger.info("Service updated successfully: " + existingService.getName());
            return "Service updated successfully";
            
        } catch (Exception e) {
            logger.error("Error updating service: ", e);
            return "Failed to update service: " + e.getMessage();
        }
    }
    
    public String deleteService(Long serviceId) {
        try {
            Optional<com.slotout.v1.models.Service> serviceOpt = serviceRepo.findById(serviceId);
            if (serviceOpt.isEmpty()) {
                return "Service not found";
            }
            
            serviceRepo.deleteById(serviceId);
            logger.info("Service deleted successfully: " + serviceId);
            return "Service deleted successfully";
            
        } catch (Exception e) {
            logger.error("Error deleting service: ", e);
            return "Failed to delete service: " + e.getMessage();
        }
    }
    
    public com.slotout.v1.models.Service getServiceById(Long serviceId) {
        try {
            return serviceRepo.findById(serviceId).orElse(null);
        } catch (Exception e) {
            logger.error("Error fetching service by ID: ", e);
            return null;
        }
    }
}
