package org.example.market.dto.response;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class ResponseSettingsUser {
    @Email
    private String email;
    @Min(4)
    private String username;
    private String role;
}
