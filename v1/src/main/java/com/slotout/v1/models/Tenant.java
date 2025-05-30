package com.slotout.v1.models;


import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "tenant")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Tenant {

    private String name;

    @Column(unique = true, nullable = false)
    private String email;

    private String phone;

    private String address;

    private String isEmailVerifies;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();
}