package com.slotout.v1.services;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.slotout.v1.dto.TimeSlotRegister;
import com.slotout.v1.models.TimeSlot;
import com.slotout.v1.repositories.ServiceRepo;
import com.slotout.v1.repositories.TimeSlotRepo;

@Service
public class TimeSlotService {
    
    private static final Logger logger = LoggerFactory.getLogger(TimeSlotService.class);
    
    @Autowired
    private TimeSlotRepo timeSlotRepo;
    
    @Autowired
    private ServiceRepo serviceRepo;
    
    public String createTimeSlot(Long serviceId, TimeSlotRegister timeSlotDto) {
        try {
            Optional<com.slotout.v1.models.Service> serviceOpt = serviceRepo.findById(serviceId);
            if (serviceOpt.isEmpty()) {
                return "Service not found";
            }
            
            com.slotout.v1.models.Service service = serviceOpt.get();
            TimeSlot timeSlot = timeSlotDto.getTimeSlotObject(service.getTenant(), service);
            
            // Check for overlapping slots
            if (hasOverlappingSlots(service, timeSlot.getStartTime(), timeSlot.getEndTime())) {
                return "Time slot overlaps with existing slot";
            }
            
            timeSlotRepo.save(timeSlot);
            logger.info("Time slot created successfully for service: " + service.getName());
            return "Time slot created successfully";
            
        } catch (Exception e) {
            logger.error("Error creating time slot: ", e);
            return "Failed to create time slot: " + e.getMessage();
        }
    }
    
    public List<TimeSlot> getAvailableTimeSlots(Long serviceId) {
        try {
            Optional<com.slotout.v1.models.Service> serviceOpt = serviceRepo.findById(serviceId);
            if (serviceOpt.isEmpty()) {
                return List.of();
            }
            
            List<TimeSlot> allSlots = timeSlotRepo.findByService(serviceOpt.get());
            return allSlots.stream()
                    .filter(slot -> !slot.getIsBooked())
                    .toList();
                    
        } catch (Exception e) {
            logger.error("Error fetching available time slots: ", e);
            return List.of();
        }
    }
    
    public String updateTimeSlot(Long timeSlotId, TimeSlotRegister timeSlotDto) {
        try {
            Optional<TimeSlot> timeSlotOpt = timeSlotRepo.findById(timeSlotId);
            if (timeSlotOpt.isEmpty()) {
                return "Time slot not found";
            }
            
            TimeSlot existingSlot = timeSlotOpt.get();
            
            LocalDateTime newStartTime = timeSlotDto.getStartTime() != null ? 
                LocalDateTime.parse(timeSlotDto.getStartTime()) : existingSlot.getStartTime();
            LocalDateTime newEndTime = timeSlotDto.getEndTime() != null ? 
                LocalDateTime.parse(timeSlotDto.getEndTime()) : existingSlot.getEndTime();
            
            // Check for overlapping slots (excluding current slot)
            if (hasOverlappingSlotsExcluding(existingSlot.getService(), newStartTime, newEndTime, timeSlotId)) {
                return "Updated time slot overlaps with existing slot";
            }
            
            existingSlot.setStartTime(newStartTime);
            existingSlot.setEndTime(newEndTime);
            
            timeSlotRepo.save(existingSlot);
            logger.info("Time slot updated successfully: " + timeSlotId);
            return "Time slot updated successfully";
            
        } catch (Exception e) {
            logger.error("Error updating time slot: ", e);
            return "Failed to update time slot: " + e.getMessage();
        }
    }
    
    public String deleteTimeSlot(Long timeSlotId) {
        try {
            Optional<TimeSlot> timeSlotOpt = timeSlotRepo.findById(timeSlotId);
            if (timeSlotOpt.isEmpty()) {
                return "Time slot not found";
            }
            
            TimeSlot timeSlot = timeSlotOpt.get();
            if (timeSlot.getIsBooked()) {
                return "Cannot delete booked time slot";
            }
            
            timeSlotRepo.deleteById(timeSlotId);
            logger.info("Time slot deleted successfully: " + timeSlotId);
            return "Time slot deleted successfully";
            
        } catch (Exception e) {
            logger.error("Error deleting time slot: ", e);
            return "Failed to delete time slot: " + e.getMessage();
        }
    }
    
    public TimeSlot getTimeSlotById(Long timeSlotId) {
        try {
            return timeSlotRepo.findById(timeSlotId).orElse(null);
        } catch (Exception e) {
            logger.error("Error fetching time slot by ID: ", e);
            return null;
        }
    }
    
    private boolean hasOverlappingSlots(com.slotout.v1.models.Service service, LocalDateTime startTime, LocalDateTime endTime) {
        List<TimeSlot> existingSlots = timeSlotRepo.findByService(service);
        
        return existingSlots.stream().anyMatch(slot -> 
            (startTime.isBefore(slot.getEndTime()) && endTime.isAfter(slot.getStartTime()))
        );
    }
    
    private boolean hasOverlappingSlotsExcluding(com.slotout.v1.models.Service service, LocalDateTime startTime, LocalDateTime endTime, Long excludeSlotId) {
        List<TimeSlot> existingSlots = timeSlotRepo.findByService(service);
        
        return existingSlots.stream()
            .filter(slot -> !slot.getId().equals(excludeSlotId))
            .anyMatch(slot -> 
                (startTime.isBefore(slot.getEndTime()) && endTime.isAfter(slot.getStartTime()))
            );
    }
}
