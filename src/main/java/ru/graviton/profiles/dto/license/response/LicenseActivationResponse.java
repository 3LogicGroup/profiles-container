package ru.graviton.profiles.dto.license.response;

import lombok.Data;
import lombok.Builder;
import ru.graviton.profiles.dto.license.ActivationStatus;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class LicenseActivationResponse {
    
    private UUID activationId;
    
    private ActivationStatus status;
    
    private String message;
    
    private LocalDateTime activationDate;
    
    private LocalDateTime expirationDate;
    
    private String activationToken;
    
    private boolean success;
    
    private String errorDetails;
    
    private int remainingActivations;
}