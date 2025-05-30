package com.slotout.v1.dto;

import io.micrometer.common.lang.Nullable;
import lombok.Data;

@Data
public class OtpVerificationRequest {

    @Nullable
    private String email;

    @Nullable
    private String phone;

    private String otp;
}
