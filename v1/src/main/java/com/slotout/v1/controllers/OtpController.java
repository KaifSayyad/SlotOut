package com.slotout.v1.controllers;

// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.http.ResponseEntity;
// import org.springframework.web.bind.annotation.PostMapping;
// import org.springframework.web.bind.annotation.RequestBody;
// import org.springframework.web.bind.annotation.RequestMapping;
// import org.springframework.web.bind.annotation.RestController;

// import com.slotout.v1.dto.OtpRequest;
// import com.slotout.v1.dto.OtpVerificationRequest;
// import com.slotout.v1.services.OtpService;


// @RestController
// @RequestMapping("/api/otp")
public class OtpController {
    // @Autowired
    // private OtpService otpService;

    // @PostMapping("/admin/send")
    // public ResponseEntity<String> sendAdminVerificationOtp(@RequestBody OtpRequest request) {
    //     otpService.sendAdminVerificationOtp(request);
    //     return ResponseEntity.ok("Admin verification OTP sent.");
    // }

    // @PostMapping("/customer/send")
    // public ResponseEntity<String> sendEmailConfirmationOtp(@RequestBody OtpRequest request) {
    //     otpService.sendCustomerEmailConfirmationOtp(request);
    //     return ResponseEntity.ok("Email confirmation OTP sent.");
    // }

    // @PostMapping("/verify")
    // public ResponseEntity<String> verifyEmailConfirmationOtp(@RequestBody OtpVerificationRequest verification) {
    //     boolean isValid = otpService.verifyOtp(verification);
    //     if (isValid) {
    //         return ResponseEntity.ok("Email confirmation OTP verified successfully.");
    //     } else {
    //         return ResponseEntity.badRequest().body("Invalid or expired OTP.");
    //     }
    // }

}
