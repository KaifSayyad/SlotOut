package com.slotout.v1.dto;

import com.slotout.v1.models.Service;
import com.slotout.v1.models.Tenant;
import com.slotout.v1.models.TimeSlot;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TimeSlotRegister {
    
    private String startTime; // ISO 8601 format: "2025-05-27T10:00:00"
    private String endTime;   // ISO 8601 format: "2025-05-27T10:30:00"
    
    public TimeSlot getTimeSlotObject(Tenant tenant, Service service) {
        TimeSlot timeSlot = new TimeSlot();
        timeSlot.setTenant(tenant);
        timeSlot.setService(service);
        timeSlot.setIsBooked(false);
        
        if (this.startTime != null) {
            timeSlot.setStartTime(LocalDateTime.parse(this.startTime));
        }
        
        if (this.endTime != null) {
            timeSlot.setEndTime(LocalDateTime.parse(this.endTime));
        }
        
        return timeSlot;
    }
}

