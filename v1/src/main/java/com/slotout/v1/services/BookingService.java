package com.slotout.v1.services;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.slotout.v1.dto.BookingRegister;
import com.slotout.v1.dto.OtpRequest;
import com.slotout.v1.dto.OtpVerificationRequest;
import com.slotout.v1.models.Booking;
import com.slotout.v1.models.Tenant;
import com.slotout.v1.models.TimeSlot;
import com.slotout.v1.repositories.BookingRepo;
import com.slotout.v1.repositories.ServiceRepo;
import com.slotout.v1.repositories.TenantRepo;
import com.slotout.v1.repositories.TimeSlotRepo;

@Service
public class BookingService {
    
    private static final Logger logger = LoggerFactory.getLogger(BookingService.class);
    
    @Autowired
    private BookingRepo bookingRepo;
    
    @Autowired
    private TenantRepo tenantRepo;
    
    @Autowired
    private ServiceRepo serviceRepo;
    
    @Autowired
    private TimeSlotRepo timeSlotRepo;
    
    @Autowired
    private OtpService otpService;
    
    @Transactional
    public String createBooking(BookingRegister bookingDto) {
        try {
            // Validate tenant
            Optional<Tenant> tenantOpt = tenantRepo.findById(bookingDto.getTenantId());
            if (tenantOpt.isEmpty()) {
                return "Tenant not found";
            }
            
            // Validate service
            Optional<com.slotout.v1.models.Service> serviceOpt = serviceRepo.findById(bookingDto.getServiceId());
            if (serviceOpt.isEmpty()) {
                return "Service not found";
            }
            
            // Validate time slot
            Optional<TimeSlot> slotOpt = timeSlotRepo.findById(bookingDto.getSlotId());
            if (slotOpt.isEmpty()) {
                return "Time slot not found";
            }
            
            TimeSlot slot = slotOpt.get();
            if (slot.getIsBooked()) {
                return "Time slot is already booked";
            }
            
            // Send OTP for verification
            OtpRequest otpRequest = new OtpRequest(bookingDto.getEmail(), bookingDto.getPhone());
            boolean otpSent = otpService.sendCustomerEmailConfirmationOtp(otpRequest);
            
            if (!otpSent) {
                return "Failed to send OTP. Please try again";
            }
            
            // Save booking in pending state (we'll complete it after OTP verification)
            Booking booking = bookingDto.getBookingObject(tenantOpt.get(), serviceOpt.get(), slot);
            bookingRepo.save(booking);
            
            logger.info("Booking created (pending OTP verification): " + booking.getId());
            return "OTP sent to your email. Please verify to confirm booking";
            
        } catch (Exception e) {
            logger.error("Error creating booking: ", e);
            return "Failed to create booking: " + e.getMessage();
        }
    }
    
    @Transactional
    public String confirmBookingWithOtp(Long bookingId, OtpVerificationRequest otpRequest) {
        try {
            Optional<Booking> bookingOpt = bookingRepo.findById(bookingId);
            if (bookingOpt.isEmpty()) {
                return "Booking not found";
            }
            
            // Verify OTP
            boolean otpValid = otpService.verifyOtp(otpRequest);
            if (!otpValid) {
                return "Invalid or expired OTP";
            }
            
            Booking booking = bookingOpt.get();
            TimeSlot slot = booking.getSlot();
            
            // Mark slot as booked
            slot.setIsBooked(true);
            timeSlotRepo.save(slot);
            
            logger.info("Booking confirmed: " + booking.getId());
            return "Booking confirmed successfully";
            
        } catch (Exception e) {
            logger.error("Error confirming booking: ", e);
            return "Failed to confirm booking: " + e.getMessage();
        }
    }
    
    public List<Booking> getBookingsByTenant(Long tenantId) {
        try {
            Optional<Tenant> tenantOpt = tenantRepo.findById(tenantId);
            if (tenantOpt.isEmpty()) {
                return List.of();
            }
            
            return bookingRepo.findByTenant(tenantOpt.get());
            
        } catch (Exception e) {
            logger.error("Error fetching bookings for tenant: ", e);
            return List.of();
        }
    }
    
    @Transactional
    public String cancelBooking(Long bookingId) {
        try {
            Optional<Booking> bookingOpt = bookingRepo.findById(bookingId);
            if (bookingOpt.isEmpty()) {
                return "Booking not found";
            }
            
            Booking booking = bookingOpt.get();
            TimeSlot slot = booking.getSlot();
            
            // Mark slot as available
            slot.setIsBooked(false);
            timeSlotRepo.save(slot);
            
            // Delete booking
            bookingRepo.deleteById(bookingId);
            
            logger.info("Booking cancelled: " + bookingId);
            return "Booking cancelled successfully";
            
        } catch (Exception e) {
            logger.error("Error cancelling booking: ", e);
            return "Failed to cancel booking: " + e.getMessage();
        }
    }
    
    public Booking getBookingById(Long bookingId) {
        try {
            return bookingRepo.findById(bookingId).orElse(null);
        } catch (Exception e) {
            logger.error("Error fetching booking by ID: ", e);
            return null;
        }
    }
}
