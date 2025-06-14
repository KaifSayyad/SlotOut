package com.slotout.v1.services;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.slotout.v1.dto.request.OtpRequest;
import com.slotout.v1.dto.request.OtpVerificationRequest;
import com.slotout.v1.dto.request.TenantRequest;
import com.slotout.v1.dto.response.TenantResponseDto;
import com.slotout.v1.models.Tenant;
import com.slotout.v1.repositories.TenantRepo;

@Service
public class TenantService {

    private static final Logger logger = LoggerFactory.getLogger(TenantService.class);

    @Autowired
    private TenantRepo repo;

    @Autowired
    private OtpService otpService;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    public ResponseEntity<?> preRegister(TenantRequest tenantDto){
        List<Tenant> tenants = repo.findByEmail(tenantDto.getEmail());
        if(tenants.size() > 0){  
            Tenant tenant = tenants.getFirst();
            if(tenant.getIsEmailVerified()) return ResponseEntity.badRequest().body(new String("Tenant with this email already exists, please login or use a different email!"));
            if(otpService.sendAdminVerificationOtp(new OtpRequest(tenantDto.getEmail(), null))){
                TenantResponseDto responseDto = new TenantResponseDto(
                    tenant.getId(),
                    tenant.getName(),
                    tenant.getEmail(),
                    "OTP sent successfully"
                );
                logger.info("OTP sent successfully to existing tenant: " + tenant.getEmail());
                return ResponseEntity.ok().body(responseDto);
            }else return ResponseEntity.badRequest().body(new String("Unable to send OTP at this moment, please try again!!"));
        }else{
            try{
                Tenant tenantObj = tenantDto.getTenantObject();
                Tenant newTenantObj = repo.save(tenantObj);
                logger.info("New tenant created successfully tenant = "+newTenantObj.toString());
                otpService.sendAdminVerificationOtp(new OtpRequest(tenantDto.getEmail(),  tenantDto.getPhone()));
                TenantResponseDto responseDto = new TenantResponseDto(
                    newTenantObj.getId(),
                    newTenantObj.getName(),
                    newTenantObj.getEmail(),
                    "OTP sent successfully"
                );
                logger.info("OTP sent successfully to tenant: " + newTenantObj.getEmail());
                return ResponseEntity.ok().body(responseDto);
            }catch(Exception e){
                logger.error("Error: Exception raised in TenantService at method preRegister() : ", (Object)e.getStackTrace());
                return ResponseEntity.internalServerError().body(new String("Some error occurred, please try again!!"));
            }
        }
    }

    public String verifyTenantOtp(OtpVerificationRequest otpVerificationRequest){
        boolean res = otpService.verifyOtp(otpVerificationRequest);
        if(res){
            List<Tenant> tenants = repo.findByEmail(otpVerificationRequest.getEmail());
            if(tenants.size() > 0){
                try{
                    Tenant t = tenants.getFirst();
                    t.setIsEmailVerified(true);
                    repo.save(t);
                    return new String("OTP Verified successfully!");
                }catch(Exception e){
                    logger.info("Error at TenantService : at method verifyTenantOtp() : Error = ", (Object)e.getStackTrace());
                    return new String("Some error occured, please try again!!");
                }
            }else return new String("No Tenant found with this email ID!!");
        }
        return new String("Unable to send OTP at this moment, please try again later!!");
    }

    public String verifyOtp(OtpVerificationRequest otpVerificationRequest) {
        return verifyTenantOtp(otpVerificationRequest);
    }

    public String authenticateTenant(String email, String password) {
        try {
            List<Tenant> tenants = repo.findByEmail(email);
            if (tenants.isEmpty()) {
                return "Invalid email or password";
            }
            
            Tenant tenant = tenants.getFirst();
            
            // Check if email is verified
            if (!tenant.getIsEmailVerified()) {
                return "Email not verified. Please verify your email first";
            }
            
            // Verify password
            if (passwordEncoder.matches(password, tenant.getPassword())) {
                logger.info("Tenant authenticated successfully: " + tenant.getEmail());
                return "Success";
            } else {
                return "Invalid email or password";
            }
            
        } catch (Exception e) {
            logger.error("Error at TenantService : at method authenticateTenant() : Error = ", (Object)e.getStackTrace());
            return "Authentication failed. Please try again later";
        }
    }
    
    public Tenant getTenantByEmail(String email) {
        try {
            List<Tenant> tenants = repo.findByEmail(email);
            return tenants.isEmpty() ? null : tenants.getFirst();
        } catch (Exception e) {
            logger.error("Error at TenantService : at method getTenantByEmail() : Error = ", (Object)e.getStackTrace());
            return null;
        }
    }
    
    public List<Tenant> getAllTenants() {
        try {
            return repo.findAll();
        } catch (Exception e) {
            logger.error("Error at TenantService : at method getAllTenants() : Error = ", (Object)e.getStackTrace());
            return List.of();
        }
    }

    public Tenant getTenantById(Long id) {
        try {
            return repo.findById(id).orElse(null);
        } catch (Exception e) {
            logger.error("Error at TenantService : getTenantById() : Error = ", (Object)e.getStackTrace());
            return null;
        }
    }

    public Tenant updateTenant(Long id, Map<String, String> updateRequest) {
        try {
            Tenant tenant = repo.findById(id).orElse(null);
            if (tenant == null) return null;
            if (updateRequest.containsKey("name")) {
                tenant.setName(updateRequest.get("name"));
            }
            if (updateRequest.containsKey("address")) {
                tenant.setAddress(updateRequest.get("address"));
            }
            // Add more fields as needed
            repo.save(tenant);
            return tenant;
        } catch (Exception e) {
            logger.error("Error at TenantService : updateTenant() : Error = ", (Object)e.getStackTrace());
            return null;
        }
    }

    public boolean deleteTenant(Long id) {
        try {
            if (!repo.existsById(id)) return false;
            repo.deleteById(id);
            return true;
        } catch (Exception e) {
            logger.error("Error at TenantService : deleteTenant() : Error = ", (Object)e.getStackTrace());
            return false;
        }
    }
}
