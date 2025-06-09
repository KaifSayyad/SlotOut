package com.slotout.v1.dto.response;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ServiceResponseDto {
    private Long serviceId;
    private String name;
    private String description;
    private boolean isActive;
    private String message;
}
