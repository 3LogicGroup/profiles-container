package ru.graviton.profiles.dto.license.request;

import lombok.Data;
import lombok.Builder;

import java.time.LocalDateTime;

@Data
@Builder
public class GeneratedLicenseResponse {
    
    private String licenseKey;
    
    private LocalDateTime expirationDate;
    
    private String status;
    
    private String message;
}