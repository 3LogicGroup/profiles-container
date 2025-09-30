package ru.graviton.profiles.dto.license.request;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;

@Data
public class RevokeLicenseRequest {
    
    @NotBlank(message = "Reason is required")
    private String reason;
}