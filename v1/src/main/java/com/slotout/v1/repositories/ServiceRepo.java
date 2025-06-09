package com.slotout.v1.repositories;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.slotout.v1.models.Service;
import com.slotout.v1.models.Tenant;

public interface ServiceRepo extends JpaRepository<Service, Long>{
    List<Service> findByTenant(Tenant tenant);
}
