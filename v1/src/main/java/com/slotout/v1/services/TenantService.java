package com.slotout.v1.services;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.slotout.v1.dto.OtpRequest;
import com.slotout.v1.dto.OtpVerificationRequest;
import com.slotout.v1.dto.TenantRegister;
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
    
    public String preRegister(TenantRegister tenantDto){
        List<Tenant> tenants = repo.findByEmail(tenantDto.getEmail());
        if(tenants.size() > 0){  
            Tenant tenant = tenants.get(0);
            if(tenant.getIsEmailVerified()) return new String("User already exists!");
            if(otpService.sendAdminVerificationOtp(new OtpRequest(tenantDto.getEmail(), null))){
                return new String("OTP sent successfully!");
            }else return new String("Unable to send OTP at this moment, please try again!");
        }else{
            try{
                Tenant tenantObj = tenantDto.getTenantObject();
                repo.save(tenantObj);
                logger.info("New tenant created successfully tenant = "+tenantObj.toString());
                otpService.sendAdminVerificationOtp(new OtpRequest(tenantDto.getEmail(),  tenantDto.getPhone()));
                return new String("OTP sent successfully!!");
            }catch(Exception e){
                logger.error("Error: Exception raised in TenantService at method preRegister() : ", (Object)e.getStackTrace());
                return new String("\"Unable to send OTP at this moment, please try again!!");
            }
        }
    }

    public String verifyTenantOtp(OtpVerificationRequest otpVerificationRequest){
        boolean res = otpService.verifyOtp(otpVerificationRequest);
        if(res){
            List<Tenant> tenants = repo.findByEmail(otpVerificationRequest.getEmail());
            if(tenants.size() > 0){
                try{
                    Tenant t = tenants.get(0);
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
            
            Tenant tenant = tenants.get(0);
            
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
            return tenants.isEmpty() ? null : tenants.get(0);
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

}
