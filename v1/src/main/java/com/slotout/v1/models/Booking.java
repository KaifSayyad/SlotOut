package com.slotout.v1.models;

import java.time.LocalDateTime;
import jakarta.persistence.*;
import lombok.*;


@Entity
@Table(name = "booking", uniqueConstraints = @UniqueConstraint(columnNames = {"slot_id"}))
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "tenant_id")
    private Tenant tenant;

    @ManyToOne(optional = false)
    @JoinColumn(name = "service_id")
    private Service service;

    @OneToOne(optional = false)
    @JoinColumn(name = "slot_id", unique = true)
    private TimeSlot slot;

    private String name;

    private String email;

    private String phone;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();
}
