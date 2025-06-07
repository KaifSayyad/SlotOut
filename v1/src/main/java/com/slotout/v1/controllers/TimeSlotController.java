package com.slotout.v1.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.slotout.v1.dto.TimeSlotRegister;
import com.slotout.v1.models.TimeSlot;
import com.slotout.v1.services.TimeSlotService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class TimeSlotController {
    
    private static final Logger logger = LoggerFactory.getLogger(TimeSlotController.class);
    
    @Autowired
    private TimeSlotService timeSlotService;
    
    @PostMapping("/services/{serviceId}/timeslots")
    public ResponseEntity<?> createTimeSlot(@PathVariable Long serviceId, @RequestBody TimeSlotRegister timeSlotDto) {
        try {
            String result = timeSlotService.createTimeSlot(serviceId, timeSlotDto);
            if (result.equals("Time slot created successfully")) {
                return ResponseEntity.ok().body(Map.of("message", result));
            } else {
                return ResponseEntity.badRequest().body(Map.of("error", result));
            }
        } catch (Exception e) {
            logger.error("Error in TimeSlotController.createTimeSlot: ", e);
            return ResponseEntity.internalServerError().body(Map.of("error", "Internal server error"));
        }
    }
    
    @GetMapping("/services/{serviceId}/timeslots/available")
    public ResponseEntity<?> getAvailableTimeSlots(@PathVariable Long serviceId) {
        try {
            List<TimeSlot> timeSlots = timeSlotService.getAvailableTimeSlots(serviceId);
            return ResponseEntity.ok().body(timeSlots);
        } catch (Exception e) {
            logger.error("Error in TimeSlotController.getAvailableTimeSlots: ", e);
            return ResponseEntity.internalServerError().body(Map.of("error", "Internal server error"));
        }
    }
    
    @PutMapping("/timeslots/{timeSlotId}")
    public ResponseEntity<?> updateTimeSlot(@PathVariable Long timeSlotId, @RequestBody TimeSlotRegister timeSlotDto) {
        try {
            String result = timeSlotService.updateTimeSlot(timeSlotId, timeSlotDto);
            if (result.equals("Time slot updated successfully")) {
                return ResponseEntity.ok().body(Map.of("message", result));
            } else {
                return ResponseEntity.badRequest().body(Map.of("error", result));
            }
        } catch (Exception e) {
            logger.error("Error in TimeSlotController.updateTimeSlot: ", e);
            return ResponseEntity.internalServerError().body(Map.of("error", "Internal server error"));
        }
    }
    
    @DeleteMapping("/timeslots/{timeSlotId}")
    public ResponseEntity<?> deleteTimeSlot(@PathVariable Long timeSlotId) {
        try {
            String result = timeSlotService.deleteTimeSlot(timeSlotId);
            if (result.equals("Time slot deleted successfully")) {
                return ResponseEntity.ok().body(Map.of("message", result));
            } else {
                return ResponseEntity.badRequest().body(Map.of("error", result));
            }
        } catch (Exception e) {
            logger.error("Error in TimeSlotController.deleteTimeSlot: ", e);
            return ResponseEntity.internalServerError().body(Map.of("error", "Internal server error"));
        }
    }
}

