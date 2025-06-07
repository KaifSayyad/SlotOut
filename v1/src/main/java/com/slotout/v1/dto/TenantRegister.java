package com.slotout.v1.dto;

import com.slotout.v1.models.Tenant;

import lombok.Data;

@Data
public class TenantRegister {
    
    private String name;

    private String email;

    private String phone;

    private String address;

    private String password;

    public Tenant getTenantObject(){
        Tenant t = new Tenant();
        t.setName(this.name);
        t.setEmail(this.email);
        t.setPhone(this.phone);
        t.setAddress(this.address);
        t.setIsEmailVerified(false);
        t.setPassword(password);
        return t;
    }
}
