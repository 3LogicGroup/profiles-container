package ru.graviton.profiles.dto.license;

import lombok.Data;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class ActivationSummaryDto {
    
    private UUID id;
    
    private String hardwareFingerprint;
    
    private LocalDateTime activationDate;
    
    private LocalDateTime deactivationDate;
    
    private String clientIp;
    
    private String userAgent;
    
    private ActivationStatus status;
}