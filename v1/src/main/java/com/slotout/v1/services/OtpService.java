package com.slotout.v1.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.slotout.v1.dto.OtpRequest;
import com.slotout.v1.dto.OtpVerificationRequest;
import com.slotout.v1.repositories.EmailRepo;

import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service
public class OtpService {

    private static final Logger logger = LoggerFactory.getLogger(OtpService.class);

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private EmailRepo emailRepo;

    private static final int OTP_LENGTH = 6;
    private static final long OTP_EXPIRY_MINUTES = 10;

    private String generateOtp() {
        Random random = new Random();
        int otp = (int) (Math.pow(10, OTP_LENGTH)) + random.nextInt(900000);
        logger.debug("Generated OTP: {}", otp);
        return String.valueOf(otp);
    }

    public boolean sendAdminVerificationOtp(OtpRequest request) {
        if (request.getEmail() == null) {
            logger.warn("sendAdminVerificationOtp called with null email");
            return false;
        }
        try{
            String otp = generateOtp();
            redisTemplate.opsForValue().set(request.getEmail(), otp, OTP_EXPIRY_MINUTES, TimeUnit.MINUTES);
            emailRepo.sendOtpEmail(
                request.getEmail(),
                "<SlotOut> Do not share this OTP with anyone else",
                "Your OTP is: " + otp + "\nIt will automatically expire in " + OTP_EXPIRY_MINUTES + " minutes."
            );
            logger.info("Admin verification OTP sent to email: {} (expires in {} minutes)", request.getEmail(), OTP_EXPIRY_MINUTES);
            return true;
        }catch(Exception e){
            logger.error("Error: Exception raised in OtpService at method sendAdminVerificationOtp() : ", (Object)e.getStackTrace());
            return false;
        }
    }

    public boolean sendCustomerEmailConfirmationOtp(OtpRequest request) {
        if (request.getEmail() == null) {
            logger.warn("sendEmailConfirmationOtp called with null email");
            return false;
        }
        try{
            String otp = generateOtp();
            redisTemplate.opsForValue().set(request.getEmail(), otp, OTP_EXPIRY_MINUTES, TimeUnit.MINUTES);
            emailRepo.sendOtpEmail(
                request.getEmail(),
                "<SlotOut> Please confirm your email address",
                "Your OTP is: " + otp
            );
            logger.info("Email confirmation OTP sent to email: {} (expires in {} minutes)", request.getEmail(), OTP_EXPIRY_MINUTES);
            return true;
        }catch(Exception e){
            logger.error("Error: Exception raised in OtpService at method sendCustomerEmailConfirmationOtp() : ", (Object)e.getStackTrace());
            return false;
        }
    }

    public boolean verifyOtp(OtpVerificationRequest verification) {
        if (verification.getEmail() == null || verification.getOtp() == null) {
            logger.warn("verifyEmailConfirmationOtp called with null email or otp");
            return false;
        }
        try {
            String storedOtp = redisTemplate.opsForValue().get(verification.getEmail());
            if (storedOtp == null) {
                logger.info("No OTP found in Redis for email: {}", verification.getEmail());
                return false;
            }
            boolean result = verification.getOtp().equals(storedOtp);
            
            logger.info("Email confirmation OTP verification for email: {} - {}", verification.getEmail(), result ? "SUCCESS" : "FAILURE");

            if(result){
                redisTemplate.delete(verification.getEmail());    
            }

            return result;
        } catch (Exception e) {
            logger.error("Exception during email confirmation OTP verification for email: {}", verification.getEmail(), e);
            return false;
        }
    }
}
