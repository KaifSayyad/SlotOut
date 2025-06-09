package com.slotout.v1.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.slotout.v1.dto.request.OtpRequest;
import com.slotout.v1.dto.request.OtpVerificationRequest;
import com.slotout.v1.dto.response.OtpResponseDto;
import com.slotout.v1.services.OtpService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.ExampleObject;


@Tag(name = "OTP", description = "OTP management APIs")
@RestController
@RequestMapping("/api/otp")
public class OtpController {
    @Autowired
    private OtpService otpService;

    @Operation(summary = "Send admin verification OTP", description = "Sends an OTP to admin's email for verification.")
    @PostMapping("/admin/send")
    public ResponseEntity<OtpResponseDto> sendAdminVerificationOtp(@RequestBody OtpRequest request) {
        try {
            otpService.sendAdminVerificationOtp(request);
            return ResponseEntity.ok(new OtpResponseDto(true, "Admin verification OTP sent.", null));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new OtpResponseDto(false, null, e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(new OtpResponseDto(false, null, "Internal server error: " + e.getMessage()));
        }
    }

    @Operation(summary = "Send customer email confirmation OTP", description = "Sends an OTP to customer's email for confirmation.")
    @PostMapping("/customer/send")
    public ResponseEntity<OtpResponseDto> sendEmailConfirmationOtp(@RequestBody OtpRequest request) {
        try {
            otpService.sendCustomerEmailConfirmationOtp(request);
            return ResponseEntity.ok(new OtpResponseDto(true, "Email confirmation OTP sent.", null));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new OtpResponseDto(false, null, e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(new OtpResponseDto(false, null, "Internal server error: " + e.getMessage()));
        }
    }

    @Operation(summary = "Verify OTP", description = "Verifies the OTP sent to email.")
    @PostMapping("/verify")
    public ResponseEntity<OtpResponseDto> verifyEmailConfirmationOtp(@RequestBody OtpVerificationRequest verification) {
        try {
            otpService.verifyOtp(verification);
            return ResponseEntity.ok(new OtpResponseDto(true, "Email confirmation OTP verified successfully.", null));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new OtpResponseDto(false, null, e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(new OtpResponseDto(false, null, "Internal server error: " + e.getMessage()));
        }
    }

}
