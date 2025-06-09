package com.slotout.v1.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.slotout.v1.dto.response.ApiResponseDto;
import com.slotout.v1.dto.request.TimeSlotRequest;
import com.slotout.v1.dto.response.TimeSlotResponseDto;
import com.slotout.v1.models.TimeSlot;
import com.slotout.v1.services.TimeSlotService;

import java.util.List;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.ExampleObject;

@Tag(name = "TimeSlot", description = "Time slot management APIs")
@RestController
@RequestMapping("/api")
public class TimeSlotController {
    
    private static final Logger logger = LoggerFactory.getLogger(TimeSlotController.class);
    
    @Autowired
    private TimeSlotService timeSlotService;
    
    @Operation(summary = "Create a new time slot", description = "Creates a new time slot for a service.")
    @PostMapping("/services/{serviceId}/timeslots")
    public ResponseEntity<?> createTimeSlot(@PathVariable Long serviceId, @RequestBody TimeSlotRequest timeSlotDto) {
        try {
            ResponseEntity<?> result = timeSlotService.createTimeSlot(serviceId, timeSlotDto);
            if (result.getStatusCode().is2xxSuccessful()) {
                return ResponseEntity.ok().body(result.getBody());
            } else {
                return result;
            }
        } catch (Exception e) {
            logger.error("Error in TimeSlotController.createTimeSlot: ", e);
            ApiResponseDto error = new ApiResponseDto(null, "Internal server error");
            return ResponseEntity.internalServerError().body(error);
        }
    }
    
    @Operation(summary = "Get available time slots", description = "Retrieves all available time slots for a service.")
    @GetMapping("/services/{serviceId}/timeslots/available")
    public ResponseEntity<?> getAvailableTimeSlots(@PathVariable Long serviceId) {
        try {
            List<TimeSlot> timeSlots = timeSlotService.getAvailableTimeSlots(serviceId);
            List<TimeSlotResponseDto> response = timeSlots.stream()
                .map(ts -> new TimeSlotResponseDto(ts.getId(), ts.getStartTime().toString(), ts.getEndTime().toString(), ts.getIsBooked(), null))
                .toList();
            return ResponseEntity.ok().body(response);
        } catch (Exception e) {
            logger.error("Error in TimeSlotController.getAvailableTimeSlots: ", e);
            ApiResponseDto error = new ApiResponseDto(null, "Internal server error");
            return ResponseEntity.internalServerError().body(error);
        }
    }
    
    @Operation(summary = "Update a time slot", description = "Updates an existing time slot.")
    @PutMapping("/timeslots/{timeSlotId}")
    public ResponseEntity<?> updateTimeSlot(@PathVariable Long timeSlotId, @RequestBody TimeSlotRequest timeSlotDto) {
        try {
            ResponseEntity<?> result = timeSlotService.updateTimeSlot(timeSlotId, timeSlotDto);
            System.out.println(result.getStatusCode().is2xxSuccessful());
            if (result.getStatusCode().is2xxSuccessful()) {
                return ResponseEntity.ok().body(result.getBody());
            } else {
                return result;
            }
        } catch (Exception e) {
            logger.error("Error in TimeSlotController.updateTimeSlot: ", e);
            ApiResponseDto error = new ApiResponseDto(null, "Internal server error");
            return ResponseEntity.internalServerError().body(error);
        }
    }
    
    @Operation(summary = "Delete a time slot", description = "Deletes a time slot.")
    @DeleteMapping("/timeslots/{timeSlotId}")
    public ResponseEntity<?> deleteTimeSlot(@PathVariable Long timeSlotId) {
        try {
            ResponseEntity<?> result = timeSlotService.deleteTimeSlot(timeSlotId);
            if (result.getStatusCode().is2xxSuccessful()) {
                return ResponseEntity.ok().body(result.getBody());
            } else {
                return result;
            }
        } catch (Exception e) {
            logger.error("Error in TimeSlotController.deleteTimeSlot: ", e);
            ApiResponseDto error = new ApiResponseDto(null, "Internal server error");
            return ResponseEntity.internalServerError().body(error);
        }
    }
}

