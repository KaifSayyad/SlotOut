package com.slotout.v1.dto.request;

import com.slotout.v1.models.Booking;
import com.slotout.v1.models.Service;
import com.slotout.v1.models.Tenant;
import com.slotout.v1.models.TimeSlot;
import lombok.Data;

@Data
public class BookingRequest {
    
    private Long tenantId;
    private Long serviceId;
    private Long slotId;
    private String name;
    private String email;
    private String phone;
    
    public Booking getBookingObject(Tenant tenant, Service service, TimeSlot slot) {
        Booking booking = new Booking();
        booking.setTenant(tenant);
        booking.setService(service);
        booking.setSlot(slot);
        booking.setName(this.name);
        booking.setEmail(this.email);
        booking.setPhone(this.phone);
        return booking;
    }
}

