package com.slotout.v1.controllers;

import com.slotout.v1.dto.response.ApiResponseDto;
import com.slotout.v1.dto.response.TenantResponseDto;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.slotout.v1.dto.request.OtpVerificationRequest;
import com.slotout.v1.dto.request.TenantRequest;
import com.slotout.v1.models.Tenant;
import com.slotout.v1.services.TenantService;
import com.slotout.v1.utils.JwtUtil;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;


@Tag(name = "Tenant", description = "Tenant management APIs")
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

    @Operation(summary = "Get all tenants", description = "Returns a list of all tenants.")
    @GetMapping("/all")
    public ResponseEntity<?> getAllTenants() {
        try {
            return ResponseEntity.ok().body(service.getAllTenants());
        } catch (Exception e) {
            logger.error("Error at TenantController : route '/all' Error = ", (Object)e.getStackTrace());
            return ResponseEntity.internalServerError().body(new ApiResponseDto(null, "Some error occurred, please try again!!"));
        }
    }
    
    @Operation(summary = "Pre-register tenant", description = "Registers a new tenant and sends OTP.")
    @PostMapping("/preRegister")
    public ResponseEntity<?> preRegister(@RequestBody Map<String, String> entity) {
        TenantRequest tenantDto = new TenantRequest();
        try{
            tenantDto.setName(entity.get("name"));
            tenantDto.setEmail(entity.get("email"));
            tenantDto.setPhone(entity.getOrDefault("phone", null));
            tenantDto.setAddress(entity.getOrDefault("address", null));
            
            // Hash the password before storing
            String rawPassword = entity.get("password");
            if (rawPassword == null || rawPassword.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(new ApiResponseDto(null, "Password is required"));
            }
            String hashedPassword = passwordEncoder.encode(rawPassword);
            tenantDto.setPassword(hashedPassword);
            
        }catch(Exception e){
            logger.error("Error at TenantController : at route '/preRegister' Error = ", (Object)e.getStackTrace());
            return ResponseEntity.internalServerError().body(new ApiResponseDto(null, "Some error occured, please try again!!"));
        }
        ResponseEntity<?> res = service.preRegister(tenantDto);
        if(res.getStatusCode().is2xxSuccessful()){
            TenantResponseDto response = (TenantResponseDto) res.getBody();
            return ResponseEntity.ok().body(response);
        }else{
            return ResponseEntity.badRequest().body(new ApiResponseDto(null, "Unable to pre-register tenant, please try again!"));
        }
    }


    @Operation(summary = "Verify tenant OTP", description = "Verifies OTP for tenant registration.")
    @PostMapping("/verifyOtp")
    public ResponseEntity<?> verifyOtp(@RequestBody Map<String, String> entity){
        OtpVerificationRequest otpVerificationRequest = new OtpVerificationRequest();
        try{
            otpVerificationRequest.setEmail(entity.getOrDefault("email", null));
            otpVerificationRequest.setPhone(entity.getOrDefault("phone", null));
            otpVerificationRequest.setOtp(entity.get("otp"));

            if(otpVerificationRequest.getEmail() == null && otpVerificationRequest.getPhone() == null) {
                return ResponseEntity.badRequest().body(new ApiResponseDto(null, "Please enter either Email or Phone No. Or Both!"));
            }

            String res = service.verifyOtp(otpVerificationRequest);
            if(res.contains("Verified")) {
                return ResponseEntity.ok().body(new ApiResponseDto("Tenant registered successfully", null));
            } else {
                return ResponseEntity.badRequest().body(new ApiResponseDto(null, res));
            }
            
        }catch(Exception e){
            logger.error("Error at TenantController : route '/verifyOtp' Error = ", (Object)e.getStackTrace());
            return ResponseEntity.internalServerError().body(new ApiResponseDto(null, "Some error occured, please try again!!"));
        }
    }
    
    @Operation(summary = "Tenant login", description = "Authenticates tenant and returns JWT token.")
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> loginRequest) {
        try {
            String email = loginRequest.get("email");
            String password = loginRequest.get("password");
            
            if (email == null || password == null) {
                return ResponseEntity.badRequest().body(new ApiResponseDto(null, "Email and password are required"));
            }
            
            String result = service.authenticateTenant(email, password);
            if (result.equals("Success")) {
                // Get tenant details for JWT generation
                Tenant tenant = service.getTenantByEmail(email);
                if (tenant != null) {
                    // Generate JWT token
                    String token = jwtUtil.generateToken(email, tenant.getId().toString());
                    
                    // Optionally, you can create a new LoginResponseDto to include token if needed
                    Map<String, Object> response = new HashMap<>();
                    response.put("id", tenant.getId());
                    response.put("name", tenant.getName());
                    response.put("email", tenant.getEmail());
                    response.put("token", token);
                    return ResponseEntity.ok().body(response);
                } else {
                    return ResponseEntity.internalServerError().body(new ApiResponseDto(null, "Error retrieving tenant details"));
                }
            } else {
                return ResponseEntity.badRequest().body(new ApiResponseDto(null, result));
            }
            
        } catch (Exception e) {
            logger.error("Error at TenantController : route '/login' Error = ", (Object)e.getStackTrace());
            return ResponseEntity.internalServerError().body(new ApiResponseDto(null, "Some error occurred, please try again!!"));
        }
    }
    
    @Operation(summary = "Get tenant by ID", description = "Returns tenant details by ID.")
    @GetMapping("/{id}")
    public ResponseEntity<?> getTenantById(@PathVariable Long id, @RequestHeader("Authorization") String authHeader) {
        try {
            Tenant tenant = service.getTenantById(id);
            if (tenant == null) {
                return ResponseEntity.status(404).body(new ApiResponseDto(null, "Tenant not found"));
            }
            TenantResponseDto response = new TenantResponseDto(tenant.getId(), tenant.getName(), tenant.getEmail(), null);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error at TenantController : route '/{id}' Error = ", (Object)e.getStackTrace());
            return ResponseEntity.internalServerError().body(new ApiResponseDto(null, "Internal server error"));
        }
    }

    @Operation(summary = "Update tenant", description = "Updates tenant details.")
    @PatchMapping("/{id}")
    public ResponseEntity<?> updateTenant(@PathVariable Long id, @RequestHeader("Authorization") String authHeader, @RequestBody Map<String, String> updateRequest) {
        try {
            Tenant updatedTenant = service.updateTenant(id, updateRequest);
            if (updatedTenant == null) {
                return ResponseEntity.status(404).body(new ApiResponseDto(null, "Tenant not found"));
            }
            TenantResponseDto response = new TenantResponseDto(updatedTenant.getId(), updatedTenant.getName(), updatedTenant.getEmail(), "Tenant updated successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error at TenantController : route '/{id}' PATCH Error = ", (Object)e.getStackTrace());
            return ResponseEntity.internalServerError().body(new ApiResponseDto(null, "Internal server error"));
        }
    }

    @Operation(summary = "Delete tenant", description = "Deletes a tenant by ID.")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTenant(@PathVariable Long id, @RequestHeader("Authorization") String authHeader) {
        try {
            boolean deleted = service.deleteTenant(id);
            if (!deleted) {
                return ResponseEntity.status(404).body(new ApiResponseDto(null, "Tenant not found"));
            }
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Tenant deleted successfully");
            response.put("error", null);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error at TenantController : route '/{id}' DELETE Error = ", (Object)e.getStackTrace());
            return ResponseEntity.internalServerError().body(new ApiResponseDto(null, "Internal server error"));
        }
    }
}
