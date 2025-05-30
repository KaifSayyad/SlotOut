package com.slotout.v1.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.slotout.v1.models.Tenant;
import com.slotout.v1.repositories.TenantRepo;

@Service
public class TenantService {

    @Autowired
    private TenantRepo repo;
    
    public boolean preRegister(Tenant tenant){
        
    }
}
