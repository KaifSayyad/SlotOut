package com.slotout.v1.services;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.slotout.v1.dto.request.TimeSlotRequest;
import com.slotout.v1.dto.response.TimeSlotResponseDto;
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
    
    public ResponseEntity<?> createTimeSlot(Long serviceId, TimeSlotRequest timeSlotDto) {
        try {
            Optional<com.slotout.v1.models.Service> serviceOpt = serviceRepo.findById(serviceId);
            if (serviceOpt.isEmpty()) {
                return ResponseEntity.badRequest().body("Service not found");
            }
            
            com.slotout.v1.models.Service service = serviceOpt.get();
            TimeSlot timeSlot = timeSlotDto.getTimeSlotObject(service.getTenant(), service);
            
            // Check for overlapping slots
            if (hasOverlappingSlots(service, timeSlot.getStartTime(), timeSlot.getEndTime())) {
                return ResponseEntity.badRequest().body("Time slot overlaps with existing slot");
            }

            TimeSlot newTimeSlot = timeSlotRepo.save(timeSlot);
            TimeSlotResponseDto responseDto = new TimeSlotResponseDto(
                newTimeSlot.getId(),
                newTimeSlot.getStartTime().toString(),
                newTimeSlot.getEndTime().toString(),
                newTimeSlot.getIsBooked(),
                "Time slot created successfully"
            );
            logger.info("Time slot created successfully for service: " + service.getName());
            return ResponseEntity.ok().body(responseDto);

        } catch (Exception e) {
            logger.error("Error creating time slot: ", e);
            return ResponseEntity.internalServerError().body("Failed to create time slot: " + e.getMessage());
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
    
    public ResponseEntity<?> updateTimeSlot(Long timeSlotId, TimeSlotRequest timeSlotDto) {
        try {
            Optional<TimeSlot> timeSlotOpt = timeSlotRepo.findById(timeSlotId);
            if (timeSlotOpt.isEmpty()) {
                return ResponseEntity.badRequest().body("Time slot not found");
            }
            
            TimeSlot existingSlot = timeSlotOpt.get();
            
            LocalDateTime newStartTime = timeSlotDto.getStartTime() != null ? 
                LocalDateTime.parse(timeSlotDto.getStartTime()) : existingSlot.getStartTime();
            LocalDateTime newEndTime = timeSlotDto.getEndTime() != null ? 
                LocalDateTime.parse(timeSlotDto.getEndTime()) : existingSlot.getEndTime();
            
            // Check for overlapping slots (excluding current slot)
            if (hasOverlappingSlotsExcluding(existingSlot.getService(), newStartTime, newEndTime, timeSlotId)) {
                return ResponseEntity.badRequest().body("Updated time slot overlaps with existing slot");
            }
            
            existingSlot.setStartTime(newStartTime);
            existingSlot.setEndTime(newEndTime);
            
            TimeSlot updatedTimeSlot = timeSlotRepo.save(existingSlot);
            TimeSlotResponseDto responseDto = new TimeSlotResponseDto(
                updatedTimeSlot.getId(),
                updatedTimeSlot.getStartTime().toString(),
                updatedTimeSlot.getEndTime().toString(),
                updatedTimeSlot.getIsBooked(),
                "Time slot updated successfully"
            );
            logger.info("Time slot updated successfully: " + updatedTimeSlot.getId());
            return ResponseEntity.ok().body(responseDto);

        } catch (Exception e) {
            logger.error("Error updating time slot: ", e);
            return ResponseEntity.internalServerError().body("Failed to update time slot: " + e.getMessage());
        }
    }

    public ResponseEntity<?> deleteTimeSlot(Long timeSlotId) {
        try {
            Optional<TimeSlot> timeSlotOpt = timeSlotRepo.findById(timeSlotId);
            if (timeSlotOpt.isEmpty()) {
                return ResponseEntity.badRequest().body("Time slot not found");
            }
            
            TimeSlot timeSlot = timeSlotOpt.get();
            if (timeSlot.getIsBooked()) {
                return ResponseEntity.badRequest().body("Cannot delete booked time slot");
            }
            
            timeSlotRepo.deleteById(timeSlotId);
            logger.info("Time slot deleted successfully: " + timeSlotId);
            return ResponseEntity.ok().body("Time slot deleted successfully");
            
        } catch (Exception e) {
            logger.error("Error deleting time slot: ", e);
            return ResponseEntity.internalServerError().body("Failed to delete time slot: " + e.getMessage());
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
