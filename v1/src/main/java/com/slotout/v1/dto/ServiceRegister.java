package com.slotout.v1.dto;

import com.slotout.v1.models.Service;
import com.slotout.v1.models.Tenant;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Duration;

@Data
public class ServiceRegister {
    
    private String name;
    private String description;
    private String duration; // Format: "HH:MM:SS" or "PT30M" (ISO 8601)
    private BigDecimal price;
    
    public Service getServiceObject(Tenant tenant) {
        Service service = new Service();
        service.setTenant(tenant);
        service.setName(this.name);
        service.setDescription(this.description);
        service.setPrice(this.price);
        service.setIsActive(true);
        
        // Parse duration string
        if (this.duration != null) {
            try {
                // Try ISO 8601 format first (e.g., "PT30M" for 30 minutes)
                if (this.duration.startsWith("PT")) {
                    service.setDuration(Duration.parse(this.duration));
                } else {
                    // Try HH:MM:SS format
                    String[] parts = this.duration.split(":");
                    if (parts.length == 3) {
                        long hours = Long.parseLong(parts[0]);
                        long minutes = Long.parseLong(parts[1]);
                        long seconds = Long.parseLong(parts[2]);
                        service.setDuration(Duration.ofHours(hours).plusMinutes(minutes).plusSeconds(seconds));
                    } else {
                        throw new IllegalArgumentException("Invalid duration format");
                    }
                }
            } catch (Exception e) {
                throw new IllegalArgumentException("Invalid duration format. Use HH:MM:SS or ISO 8601 format (e.g., PT30M)");
            }
        }
        
        return service;
    }
}
