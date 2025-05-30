package com.slotout.v1.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.slotout.v1.models.Tenant;
import com.slotout.v1.models.TimeSlot;

import java.security.Provider.Service;
import java.util.List;


public interface TimeSlotRepo extends JpaRepository<TimeSlot, Long> {
    List<TimeSlot> findByTenant(Tenant tenant);
    List<TimeSlot> findByService(Service service);
    List<TimeSlot> findByIsBooked(Boolean isBooked);
}
