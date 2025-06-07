package com.slotout.v1.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.slotout.v1.dto.BookingRegister;
import com.slotout.v1.dto.OtpVerificationRequest;
import com.slotout.v1.models.Booking;
import com.slotout.v1.services.BookingService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class BookingController {
    
    private static final Logger logger = LoggerFactory.getLogger(BookingController.class);
    
    @Autowired
    private BookingService bookingService;
    
    @PostMapping("/bookings")
    public ResponseEntity<?> createBooking(@RequestBody BookingRegister bookingDto) {
        try {
            String result = bookingService.createBooking(bookingDto);
            return ResponseEntity.ok().body(Map.of("message", result));
        } catch (Exception e) {
            logger.error("Error in BookingController.createBooking: ", e);
            return ResponseEntity.internalServerError().body(Map.of("error", "Internal server error"));
        }
    }
    
    @PostMapping("/bookings/{bookingId}/confirm")
    public ResponseEntity<?> confirmBooking(@PathVariable Long bookingId, @RequestBody OtpVerificationRequest otpRequest) {
        try {
            String result = bookingService.confirmBookingWithOtp(bookingId, otpRequest);
            if (result.equals("Booking confirmed successfully")) {
                return ResponseEntity.ok().body(Map.of("message", result));
            } else {
                return ResponseEntity.badRequest().body(Map.of("error", result));
            }
        } catch (Exception e) {
            logger.error("Error in BookingController.confirmBooking: ", e);
            return ResponseEntity.internalServerError().body(Map.of("error", "Internal server error"));
        }
    }
    
    @GetMapping("/tenants/{tenantId}/bookings")
    public ResponseEntity<?> getBookingsByTenant(@PathVariable Long tenantId) {
        try {
            List<Booking> bookings = bookingService.getBookingsByTenant(tenantId);
            return ResponseEntity.ok().body(bookings);
        } catch (Exception e) {
            logger.error("Error in BookingController.getBookingsByTenant: ", e);
            return ResponseEntity.internalServerError().body(Map.of("error", "Internal server error"));
        }
    }
    
    @DeleteMapping("/bookings/{bookingId}")
    public ResponseEntity<?> cancelBooking(@PathVariable Long bookingId) {
        try {
            String result = bookingService.cancelBooking(bookingId);
            if (result.equals("Booking cancelled successfully")) {
                return ResponseEntity.ok().body(Map.of("message", result));
            } else {
                return ResponseEntity.badRequest().body(Map.of("error", result));
            }
        } catch (Exception e) {
            logger.error("Error in BookingController.cancelBooking: ", e);
            return ResponseEntity.internalServerError().body(Map.of("error", "Internal server error"));
        }
    }
}

