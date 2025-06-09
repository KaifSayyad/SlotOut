package com.slotout.v1.models;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;


@Entity
@Table(name = "tenant")
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Tenant {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;
    
    private String name;

    @Column(unique = true, nullable = false)
    private String email;

    private String phone;

    private String address;

    private Boolean isEmailVerified;

    @Column(nullable = false)
    private String password;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();


}