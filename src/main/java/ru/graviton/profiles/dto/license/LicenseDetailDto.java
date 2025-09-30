package ru.graviton.profiles.dto.license;

import lombok.Data;
import lombok.Builder;


import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
public class LicenseDetailDto {
    
    private UUID id;
    
    private String licenseKey;

    private LicenseType licenseType;
    
    private String customerName;
    
    private String customerEmail;
    
    private LicenseStatus status;
    
    private LocalDateTime creationDate;
    
    private LocalDateTime expirationDate;
    
    private Integer maxActivations;
    
    private String notes;
    
    private List<ActivationSummaryDto> activations;
}