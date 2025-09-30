package ru.graviton.profiles.dto.license;

import lombok.Data;
import lombok.Builder;

@Data
@Builder
public class LicenseStatsDto {
    
    private Long totalLicenses;
    
    private Long activeLicenses;
    
    private Long expiredLicenses;
    
    private Long revokedLicenses;
    
    private Long totalActivations;
    
    private Long recentActivations;
}