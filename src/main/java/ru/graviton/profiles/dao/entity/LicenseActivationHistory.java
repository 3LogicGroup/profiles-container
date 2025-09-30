package ru.graviton.profiles.dao.entity;

import jakarta.persistence.*;
import lombok.*;
import ru.graviton.profiles.dto.license.ActivationMethod;
import ru.graviton.profiles.dto.license.ActivationStatus;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "license_activation_history")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LicenseActivationHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "license_id")
    private LicenseEntity license;

    @Column(name = "hardware_fingerprint")
    private String hardwareFingerprint;

    @Column(name = "activation_date")
    private LocalDateTime activationDate;

    @Column(name = "client_ip")
    private String clientIp;

    @Column(name = "user_agent")
    private String userAgent;

    @Column(name = "application_version")
    private String applicationVersion;

    @Enumerated(EnumType.STRING)
    @Column(name = "activation_method")
    private ActivationMethod activationMethod;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private ActivationStatus status;

    @Column(name = "error_message")
    private String errorMessage;

    @Column(name = "deactivation_date")
    private LocalDateTime deactivationDate;

    @PrePersist
    protected void onCreate() {
        if (activationDate == null) {
            activationDate = LocalDateTime.now();
        }
    }
}