package ru.graviton.profiles.dto.license;

import lombok.Data;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class LicenseDto {
    
    private UUID id;
    
    private String licenseKey;
    
    private LicenseType licenseType;
    
    private LicenseStatus status;
    
    private LocalDateTime creationDate;
    
    private LocalDateTime expirationDate;
    
    private Integer maxActivations;
    
    private Integer currentActivations;
    
    private String customerEmail;
    
    private String customerName;
    
    private boolean isExpired;
    
    private boolean isActive;
}