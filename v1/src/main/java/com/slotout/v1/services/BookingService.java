package com.slotout.v1.services;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.slotout.v1.dto.request.BookingRequest;
import com.slotout.v1.dto.response.BookingResponseDto;
import com.slotout.v1.dto.request.OtpRequest;
import com.slotout.v1.dto.request.OtpVerificationRequest;
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
    public BookingResponseDto createBooking(BookingRequest bookingDto) {
        try {
            Optional<Tenant> tenantOpt = tenantRepo.findById(bookingDto.getTenantId());
            if (tenantOpt.isEmpty()) {
                throw new IllegalArgumentException("Tenant not found");
            }
            Optional<com.slotout.v1.models.Service> serviceOpt = serviceRepo.findById(bookingDto.getServiceId());
            if (serviceOpt.isEmpty()) {
                throw new IllegalArgumentException("Service not found");
            }
            Optional<TimeSlot> slotOpt = timeSlotRepo.findById(bookingDto.getSlotId());
            if (slotOpt.isEmpty()) {
                throw new IllegalArgumentException("Time slot not found");
            }
            TimeSlot slot = slotOpt.get();
            if (slot.getIsBooked()) {
                throw new IllegalArgumentException("Time slot is already booked");
            }
            OtpRequest otpRequest = new OtpRequest(bookingDto.getEmail(), bookingDto.getPhone());
            otpService.sendCustomerEmailConfirmationOtp(otpRequest);
            Booking booking = bookingDto.getBookingObject(tenantOpt.get(), serviceOpt.get(), slot);
            bookingRepo.save(booking);
            logger.info("Booking created (pending OTP verification): " + booking.getId());
            return new BookingResponseDto(booking.getId(), "OTP sent to your email. Please verify to confirm booking");
        } catch (Exception e) {
            logger.error("Error creating booking: ", e);
            throw e;
        }
    }
    
    @Transactional
    public BookingResponseDto confirmBookingWithOtp(Long bookingId, OtpVerificationRequest otpRequest) {
        try {
            Optional<Booking> bookingOpt = bookingRepo.findById(bookingId);
            if (bookingOpt.isEmpty()) {
                throw new IllegalArgumentException("Booking not found");
            }
            Booking booking = bookingOpt.get();
            otpService.verifyOtp(otpRequest);
            booking.getSlot().setIsBooked(true);
            bookingRepo.save(booking);
            logger.info("Booking confirmed: " + booking.getId());
            return new BookingResponseDto(booking.getId(), "Booking confirmed successfully");
        } catch (Exception e) {
            logger.error("Error confirming booking: ", e);
            throw e;
        }
    }
    
    public List<BookingResponseDto> getBookingsByTenant(Long tenantId) {
        try {
            List<Booking> bookings = bookingRepo.findByTenantId(tenantId);
            return bookings.stream()
                .map(b -> new BookingResponseDto(b.getId(), b.getSlot().getIsBooked() ? "CONFIRMED" : "PENDING"))
                .toList();
        } catch (Exception e) {
            logger.error("Error fetching bookings for tenant: ", e);
            throw e;
        }
    }
    
    @Transactional
    public BookingResponseDto cancelBooking(Long bookingId) {
        try {
            Optional<Booking> bookingOpt = bookingRepo.findById(bookingId);
            if (bookingOpt.isEmpty()) {
                throw new IllegalArgumentException("Booking not found");
            }
            Booking booking = bookingOpt.get();
            TimeSlot slot = booking.getSlot();
            slot.setIsBooked(false);
            timeSlotRepo.save(slot);
            bookingRepo.deleteById(bookingId);
            logger.info("Booking cancelled: " + bookingId);
            return new BookingResponseDto(bookingId, "Booking cancelled successfully");
        } catch (Exception e) {
            logger.error("Error cancelling booking: ", e);
            throw e;
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
