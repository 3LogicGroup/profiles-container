package ru.graviton.profiles.dto.license;

import lombok.Data;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class LicenseSummaryDto {
    
    private UUID id;
    
    private String licenseKey;
    
    private String customerName;
    
    private String customerEmail;
    
    private LicenseStatus status;
    
    private LocalDateTime creationDate;
    
    private LocalDateTime expirationDate;
    
    private Integer maxActivations;
    
    private Integer currentActivations;
}