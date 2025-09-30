package ru.graviton.profiles.dao.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.graviton.profiles.dao.entity.LicenseActivationHistory;
import ru.graviton.profiles.dao.entity.LicenseEntity;
import ru.graviton.profiles.dto.license.ActivationStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface LicenseActivationHistoryRepository extends JpaRepository<LicenseActivationHistory, UUID> {

    List<LicenseActivationHistory> findByLicenseIdOrderByActivationDateDesc(UUID licenseId);

    @Query("SELECT h FROM LicenseActivationHistory h WHERE h.id=:licenseId and h.hardwareFingerprint = :hardwareFingerprint AND h.status='SUCESS'")
    Optional<LicenseActivationHistory> findByLicenseIdAndHardwareFingerprintAndIsActiveTrue(
            UUID licenseId, String hardwareFingerprint);

    @Query("SELECT COUNT(h) FROM LicenseActivationHistory h WHERE h.license.id = :licenseId AND h.status = 'SUCESS'")
    int countActiveLicenses(@Param("licenseId") UUID licenseId);

    @Query("SELECT h FROM LicenseActivationHistory h WHERE h.license.id = :licenseId AND h.status = 'SUCESS'")
    List<LicenseActivationHistory> findActiveLicenses(@Param("licenseId") UUID licenseId);

    List<LicenseActivationHistory> findByStatusAndActivationDateBefore(
            ActivationStatus status, LocalDateTime date);

    @Query("SELECT h FROM LicenseActivationHistory h WHERE h.hardwareFingerprint = :hardwareFingerprint AND h.status='SUCESS'")
    List<LicenseActivationHistory> findActiveByHardwareFingerprint(String hardwareFingerprint);

    @Query("SELECT h FROM LicenseActivationHistory h WHERE h.clientIp = :ip AND h.activationDate >= :date")
    List<LicenseActivationHistory> findRecentActivationsByIp(
            @Param("ip") String ip, @Param("date") LocalDateTime date);

    List<LicenseActivationHistory> findByLicenseOrderByActivationDateDesc(LicenseEntity license);

    @Query("SELECT h FROM LicenseActivationHistory h WHERE h.license = :license AND h.status='SUCESS'")
    List<LicenseActivationHistory> findActiveByLicense(LicenseEntity license);

    long countByStatus(ActivationStatus status);

    long countByActivationDateAfterAndStatus(LocalDateTime date, ActivationStatus status);
}