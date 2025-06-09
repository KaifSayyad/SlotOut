package com.slotout.v1.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Tenant login request")
public class TenantLoginRequest {
    @Schema(description = "Tenant email", example = "john@example.com")
    private String email;

    @Schema(description = "Tenant password", example = "password123")
    private String password;

    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
}
