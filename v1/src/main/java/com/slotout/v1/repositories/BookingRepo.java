package com.slotout.v1.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.slotout.v1.models.Booking;
import com.slotout.v1.models.Tenant;

import java.security.Provider.Service;
import java.util.List;


public interface BookingRepo extends JpaRepository<Booking, Long>{
    List<Booking> findByTenant(Tenant tenant);
    List<Booking> findByService(Service service);
    List<Booking> findByEmail(String email);
    List<Booking> findByPhone(String phone);
}
