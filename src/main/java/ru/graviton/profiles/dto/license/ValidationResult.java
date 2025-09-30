package ru.graviton.profiles.dto.license;

import lombok.Data;
import lombok.Builder;

@Data
@Builder
public class ValidationResult {
    
    private boolean valid;
    
    private String errorMessage;
    
    public static ValidationResult valid() {
        return ValidationResult.builder()
                .valid(true)
                .build();
    }
    
    public static ValidationResult invalid(String errorMessage) {
        return ValidationResult.builder()
                .valid(false)
                .errorMessage(errorMessage)
                .build();
    }
}