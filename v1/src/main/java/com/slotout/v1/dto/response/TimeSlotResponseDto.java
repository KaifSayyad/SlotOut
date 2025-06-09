package com.slotout.v1.dto.response;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TimeSlotResponseDto {
    private Long timeSlotId;
    private String startTime;
    private String endTime;
    private boolean isBooked;
    private String message;
}
