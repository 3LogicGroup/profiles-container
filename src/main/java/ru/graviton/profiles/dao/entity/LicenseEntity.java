package ru.graviton.profiles.dao.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.graviton.profiles.dto.license.LicenseStatus;
import ru.graviton.profiles.dto.license.LicenseType;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "license")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LicenseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "license_key", unique = true, nullable = false)
    private String licenseKey;

    @Column(name = "license_type")
    @Enumerated(EnumType.STRING)
    private LicenseType licenseType;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private LicenseStatus status;

    @Column(name = "creation_date")
    private LocalDateTime creationDate;

    @Column(name = "max_activations")
    private Integer maxActivations = 1;

    @Column(name = "customer_email")
    private String customerEmail;

    @Column(name = "customer_name")
    private String customerName;

    @Column(name = "notes")
    private String notes;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "expiration_date")
    private LocalDateTime expirationDate;

    @OneToMany(mappedBy = "license", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<LicenseActivationHistory> activationHistory;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id")
    private ClientEntity client;

    @PrePersist
    protected void onCreate() {
        if (creationDate == null) {
            creationDate = LocalDateTime.now();
        }
        if (status == null) {
            status = LicenseStatus.ISSUED;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public LocalDateTime getExpirationDate() {
        return getLicenseType() == LicenseType.UNLIMITED ? null : getCreationDate().plusDays(getLicenseType().getValidityDays());
    }
}