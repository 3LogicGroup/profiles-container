package ru.graviton.profiles.dto.license;

import lombok.Data;
import lombok.Builder;

import java.time.LocalDateTime;

@Data
@Builder
public class GeneratedLicense {
    
    private String licenseKey;
    
    private LocalDateTime expirationDate;
    
    private String customerName;
    
    private String customerEmail;
}