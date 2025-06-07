package com.slotout.v1.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.slotout.v1.configurations.JwtUtil;
import com.slotout.v1.dto.OtpVerificationRequest;
import com.slotout.v1.dto.TenantRegister;
import com.slotout.v1.models.Tenant;
import com.slotout.v1.services.TenantService;

import java.util.HashMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/api/tenants")
public class TenantController {

    private static final Logger logger = LoggerFactory.getLogger(TenantController.class);

    @Autowired
    private TenantService service;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @PostMapping("/preRegister")
    public ResponseEntity<?> preRegister(@RequestBody Map<String, String> entity) {
        TenantRegister tenantDto = new TenantRegister();
        try{
            tenantDto.setName(entity.get("name"));
            tenantDto.setEmail(entity.get("email"));
            tenantDto.setPhone(entity.getOrDefault("phone", null));
            tenantDto.setAddress(entity.getOrDefault("address", null));
            
            // Hash the password before storing
            String rawPassword = entity.get("password");
            if (rawPassword == null || rawPassword.trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Password is required");
            }
            String hashedPassword = passwordEncoder.encode(rawPassword);
            tenantDto.setPassword(hashedPassword);
            
        }catch(Exception e){
            logger.error("Error at TenantController : at route '/preRegister' Error = ", (Object)e.getStackTrace());
            return ResponseEntity.internalServerError().body("Some error occured, please try again!!");
        }
        String res = service.preRegister(tenantDto);
        if(!res.contentEquals("Unable")){
            return ResponseEntity.ok().body(res);
        }else{
            return ResponseEntity.internalServerError().body(res);
        }
    }
    

    @PostMapping("/verifyOtp")
    public ResponseEntity<?> verifyOtp(@RequestBody Map<String, String> entity){
        OtpVerificationRequest otpVerificationRequest = new OtpVerificationRequest();
        try{
            otpVerificationRequest.setEmail(entity.getOrDefault("email", null));
            otpVerificationRequest.setPhone(entity.getOrDefault("phone", null));
            otpVerificationRequest.setOtp(entity.get("otp"));

            if(otpVerificationRequest.getEmail() == null && otpVerificationRequest.getPhone() == null) {
                return ResponseEntity.badRequest().body("Please enter either Email or Phone No. Or Both!");
            }

            String res = service.verifyOtp(otpVerificationRequest);
            if(res.contentEquals("Verified successfully!")) {
                return ResponseEntity.ok().body("Tenant registered successfully");
            } else {
                return ResponseEntity.badRequest().body(res);
            }
            
        }catch(Exception e){
            logger.error("Error at TenantController : route '/verifyOtp' Error = ", (Object)e.getStackTrace());
            return ResponseEntity.internalServerError().body("Some error occured, please try again!!");
        }
    }
    
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> loginRequest) {
        try {
            String email = loginRequest.get("email");
            String password = loginRequest.get("password");
            
            if (email == null || password == null) {
                return ResponseEntity.badRequest().body("Email and password are required");
            }
            
            String result = service.authenticateTenant(email, password);
            if (result.equals("Success")) {
                // Get tenant details for JWT generation
                Tenant tenant = service.getTenantByEmail(email);
                if (tenant != null) {
                    // Generate JWT token
                    String token = jwtUtil.generateToken(email, tenant.getId().toString());
                    
                    // Prepare response with token
                    Map<String, Object> response = new HashMap<>();
                    response.put("message", "Login successful");
                    response.put("token", token);
                    response.put("tenantId", tenant.getId());
                    response.put("email", tenant.getEmail());
                    response.put("name", tenant.getName());
                    
                    return ResponseEntity.ok().body(response);
                } else {
                    return ResponseEntity.internalServerError().body("Error retrieving tenant details");
                }
            } else {
                return ResponseEntity.badRequest().body(result);
            }
            
        } catch (Exception e) {
            logger.error("Error at TenantController : route '/login' Error = ", (Object)e.getStackTrace());
            return ResponseEntity.internalServerError().body("Some error occurred, please try again!!");
        }
    }
    
}
