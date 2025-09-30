package ru.graviton.profiles.dto.license;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class LicenseActivationHistoryDto {

    private UUID id;

    private String licenseKey;

    private String hardwareFingerprint;

    private LocalDateTime activationDate;

    private String clientIp;

    private String userAgent;

    private String applicationVersion;

    private ActivationMethod activationMethod;

    private ActivationStatus status;

    private String errorMessage;

    private LocalDateTime deactivationDate;
}