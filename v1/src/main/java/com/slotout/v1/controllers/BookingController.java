package com.slotout.v1.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.slotout.v1.dto.request.BookingRequest;
import com.slotout.v1.dto.request.OtpVerificationRequest;
import com.slotout.v1.dto.response.ApiResponseDto;
import com.slotout.v1.dto.response.BookingResponseDto;
import com.slotout.v1.services.BookingService;

import java.util.List;

@Tag(name = "Booking", description = "Booking management APIs")
@RestController
@RequestMapping("/api")
public class BookingController {
    
    private static final Logger logger = LoggerFactory.getLogger(BookingController.class);
    
    @Autowired
    private BookingService bookingService;
    
    @Operation(summary = "Create a new booking", description = "Creates a new booking and sends OTP for confirmation.")
    @PostMapping("/bookings")
    public ResponseEntity<?> createBooking(@RequestBody BookingRequest bookingDto) {
        try {
            BookingResponseDto response = bookingService.createBooking(bookingDto);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ApiResponseDto(null, e.getMessage()));
        } catch (Exception e) {
            logger.error("Error in BookingController.createBooking: ", e);
            return ResponseEntity.internalServerError().body(new ApiResponseDto(null, "Internal server error"));
        }
    }
    
    @Operation(summary = "Confirm booking with OTP", description = "Confirms a booking using OTP.")
    @PostMapping("/bookings/{bookingId}/confirm")
    public ResponseEntity<?> confirmBooking(@PathVariable Long bookingId, @RequestBody OtpVerificationRequest otpRequest) {
        try {
            BookingResponseDto response = bookingService.confirmBookingWithOtp(bookingId, otpRequest);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ApiResponseDto(null, e.getMessage()));
        } catch (Exception e) {
            logger.error("Error in BookingController.confirmBooking: ", e);
            return ResponseEntity.internalServerError().body(new ApiResponseDto(null, "Internal server error"));
        }
    }
    
    @Operation(summary = "Get bookings by tenant", description = "Retrieves all bookings for a tenant.")
    @GetMapping("/tenants/{tenantId}/bookings")
    public ResponseEntity<?> getBookingsByTenant(@PathVariable Long tenantId) {
        try {
            List<BookingResponseDto> response = bookingService.getBookingsByTenant(tenantId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error in BookingController.getBookingsByTenant: ", e);
            return ResponseEntity.internalServerError().body(new ApiResponseDto(null, "Internal server error"));
        }
    }
    
    @Operation(summary = "Cancel a booking", description = "Cancels a booking and frees the slot.")
    @DeleteMapping("/bookings/{bookingId}")
    public ResponseEntity<?> cancelBooking(@PathVariable Long bookingId) {
        try {
            BookingResponseDto response = bookingService.cancelBooking(bookingId);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ApiResponseDto(null, e.getMessage()));
        } catch (Exception e) {
            logger.error("Error in BookingController.cancelBooking: ", e);
            return ResponseEntity.internalServerError().body(new ApiResponseDto(null, "Internal server error"));
        }
    }
}

