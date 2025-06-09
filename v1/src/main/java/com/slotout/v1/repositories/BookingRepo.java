package com.slotout.v1.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import com.slotout.v1.models.Booking;
import java.util.List;


public interface BookingRepo extends JpaRepository<Booking, Long>{
    List<Booking> findByTenantId(Long tenantId);
    List<Booking> findByServiceId(Long serviceId);
    List<Booking> findByEmail(String email);
    List<Booking> findByPhone(String phone);
}
