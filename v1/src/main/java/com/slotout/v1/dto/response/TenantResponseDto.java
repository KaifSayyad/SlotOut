package com.slotout.v1.dto.response;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TenantResponseDto {
    private Long tenantId;
    private String name;
    private String email;
    private String message;
}
