package com.slotout.v1.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.slotout.v1.models.Tenant;
import java.util.List;


public interface TenantRepo extends JpaRepository<Tenant, Long>{
    List<Tenant> findByEmail(String email);
}