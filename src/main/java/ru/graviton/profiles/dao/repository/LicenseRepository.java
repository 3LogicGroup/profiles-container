package ru.graviton.profiles.dao.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.graviton.profiles.dao.entity.LicenseEntity;
import ru.graviton.profiles.dto.license.LicenseStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface LicenseRepository extends JpaRepository<LicenseEntity, UUID> {
    
    Optional<LicenseEntity> findByLicenseKey(String licenseKey);
    
    List<LicenseEntity> findByStatus(LicenseStatus status);
    
    List<LicenseEntity> findByCustomerEmail(String customerEmail);

    @Query("SELECT l FROM LicenseEntity l WHERE l.expirationDate < :date AND l.status = :status")
    List<LicenseEntity> findExpiredLicenses(
            @Param("date") LocalDateTime date,
            @Param("status") LicenseStatus status);

    @Query("SELECT l FROM LicenseEntity l WHERE l.licenseType = 'TRIAL' AND l.creationDate < :date")
    List<LicenseEntity> findExpiredTrialLicenses(@Param("date") LocalDateTime date);
    
    @Query("SELECT COUNT(l) FROM LicenseEntity l WHERE l.status = :status")
    long countByStatus(@Param("status") LicenseStatus status);
    
    @Query("SELECT l FROM LicenseEntity l WHERE l.customerName LIKE %:name% OR l.customerEmail LIKE %:email%")
    List<LicenseEntity> findByCustomerInfo(
            @Param("name") String name, 
            @Param("email") String email);
    
    boolean existsByLicenseKey(String licenseKey);

    long countByExpirationDateBefore(LocalDateTime date);

    Page<LicenseEntity> findByCustomerNameContainingIgnoreCaseOrStatus(
            String customerCompany, LicenseStatus status, Pageable pageable);
}