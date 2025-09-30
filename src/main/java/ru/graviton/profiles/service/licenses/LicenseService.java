package ru.graviton.profiles.service.licenses;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.graviton.profiles.dao.entity.LicenseActivationHistory;
import ru.graviton.profiles.dao.entity.LicenseEntity;
import ru.graviton.profiles.dao.repository.LicenseActivationHistoryRepository;
import ru.graviton.profiles.dao.repository.LicenseRepository;
import ru.graviton.profiles.dto.license.*;
import ru.graviton.profiles.dto.license.request.CreateLicenseRequest;
import ru.graviton.profiles.exceptions.LicenseException;
import ru.graviton.profiles.exceptions.LicenseNotFoundException;
import ru.graviton.profiles.mapper.license.LicenseMapper;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class LicenseService {

    private final LicenseRepository licenseRepository;
    private final LicenseActivationHistoryRepository activationHistoryRepository;
    private final LicenseMapper licenseMapper;
    private static final String LICENSE_KEY_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int LICENSE_KEY_LENGTH = 25;

    public LicenseDto createLicense(CreateLicenseRequest request) {
        log.info("Creating new license for customer: {}", request.getCustomerEmail());

        String licenseKey = generateLicenseKey();

        // Проверяем уникальность ключа
        while (licenseRepository.existsByLicenseKey(licenseKey)) {
            licenseKey = generateLicenseKey();
        }

        LicenseEntity license = LicenseEntity.builder()
                .licenseKey(licenseKey)
                .licenseType(request.getLicenseType())
                .status(LicenseStatus.ISSUED)
                .expirationDate(LocalDateTime.now().plusYears(10))
                .maxActivations(request.getMaxActivations())
                .customerEmail(request.getCustomerEmail())
                .customerName(request.getCustomerName())
                .build();

        license = licenseRepository.save(license);

        log.info("License created successfully: {}", licenseKey);
        return licenseMapper.toDto(license);
    }

    public Page<LicenseSummaryDto> getAllLicenses(
            Pageable pageable, String customerCompany, LicenseStatus status) {
        Page<LicenseEntity> licenses;

        if (customerCompany != null || status != null) {
            licenses = licenseRepository.findByCustomerNameContainingIgnoreCaseOrStatus(
                    customerCompany != null ? customerCompany : "", status, pageable);
        } else {
            licenses = licenseRepository.findAll(pageable);
        }

        return licenses.map(this::convertToSummaryDto);
    }

    @Transactional(readOnly = true)
    public LicenseDetailDto findByLicenseKey(String licenseKey) {
        LicenseEntity license = licenseRepository.findByLicenseKey(licenseKey)
                .orElseThrow(() -> new LicenseNotFoundException("License not found: " + licenseKey));

        List<LicenseActivationHistory> activations = activationHistoryRepository.findByLicenseOrderByActivationDateDesc(license);

        return convertToDetailDto(license, activations);
    }

    @Transactional
    public LicenseStatsDto getLicenseStats() {

        return LicenseStatsDto.builder()
                .totalLicenses(licenseRepository.count())
                .activeLicenses(licenseRepository.countByStatus(LicenseStatus.ACTIVATED))
                .expiredLicenses(licenseRepository.countByExpirationDateBefore(LocalDateTime.now()))
                .revokedLicenses(licenseRepository.countByStatus(LicenseStatus.REVOKED))
                .totalActivations(activationHistoryRepository.countByStatus(ActivationStatus.SUCCESS))
                .recentActivations(activationHistoryRepository.countByActivationDateAfterAndStatus(
                        LocalDateTime.now().minusDays(30), ActivationStatus.SUCCESS))
                .build();
    }

    @Transactional(readOnly = true)
    public List<LicenseDto> findByCustomerEmail(String customerEmail) {
        List<LicenseEntity> licenses = licenseRepository.findByCustomerEmail(customerEmail);
        return licenses.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    public void revokeLicense(
            String licenseKey, String reason) {

        LicenseEntity license = licenseRepository.findByLicenseKey(licenseKey)
                .orElseThrow(() -> new LicenseNotFoundException("License not found: " + licenseKey));

        license.setStatus(LicenseStatus.REVOKED);
        license.setNotes(license.getNotes() + "\nREVOKED: " + reason + " at " + LocalDateTime.now());
        license.setUpdatedAt(LocalDateTime.now());

        licenseRepository.save(license);

        // Деактивируем все активные активации
        List<LicenseActivationHistory> activeActivations =
                activationHistoryRepository.findActiveByLicense(license);
        activeActivations.forEach(activation -> {
            activation.setStatus(ActivationStatus.DEACTIVATED);
            activation.setDeactivationDate(LocalDateTime.now());
        });
        activationHistoryRepository.saveAll(activeActivations);
    }

    @Transactional(readOnly = true)
    public List<LicenseDto> findByStatus(LicenseStatus status) {
        List<LicenseEntity> licenses = licenseRepository.findByStatus(status);
        return licenses.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }


    public LicenseEntity updateLicenseStatus(UUID licenseId, LicenseStatus status) {
        LicenseEntity license = licenseRepository.findById(licenseId)
                .orElseThrow(() -> new LicenseException("License not found"));

        license.setStatus(status);
        license = licenseRepository.save(license);

        log.info("License status updated: {} -> {}", licenseId, status);
        return license;
    }


    public void expireLicense(UUID licenseId) {
        LicenseEntity license = licenseRepository.findById(licenseId)
                .orElseThrow(() -> new LicenseException("License not found"));

        license.setStatus(LicenseStatus.EXPIRED);
        licenseRepository.save(license);

        log.info("License expired: {}", licenseId);
    }

    @Transactional(readOnly = true)
    public boolean isLicenseValid(String licenseKey) {
        Optional<LicenseEntity> license = licenseRepository.findByLicenseKey(licenseKey);

        if (license.isEmpty()) {
            return false;
        }

        LicenseEntity l = license.get();

        // Проверяем статус
        if (l.getStatus() != LicenseStatus.ACTIVATED) {
            return false;
        }

        // Проверяем срок действия
        return l.getLicenseType() == LicenseType.UNLIMITED
                || !l.getCreationDate().plusDays(l.getLicenseType().getValidityDays()).isBefore(LocalDateTime.now());
    }

    //todo:
//    public void processExpiredLicenses() {
//        List<LicenseEntity> expiredLicenses = findExpiredLicenses();
//
//        for (LicenseEntity license : expiredLicenses) {
//            license.setStatus(LicenseStatus.EXPIRED);
//            licenseRepository.save(license);
//        }
//
//        // Обрабатываем истекшие триальные лицензии
//        List<LicenseEntity> expiredTrials = licenseRepository.findExpiredTrialLicenses(
//                LocalDateTime.now());
//
//        for (LicenseEntity trial : expiredTrials) {
//            if (trial.getLicenseType() == LicenseType.TRIAL &&
//                    trial.getCreationDate().plusDays(LicenseType.TRIAL.getValidityDays()).isBefore(LocalDateTime.now())) {
//                trial.setStatus(LicenseStatus.EXPIRED);
//                licenseRepository.save(trial);
//            }
//        }
//
//        log.info("Processed {} expired licenses and {} expired trials",
//                expiredLicenses.size(), expiredTrials.size());
//    }

    public String generateLicenseKey() {
        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < LICENSE_KEY_LENGTH; i++) {
            if (i > 0 && i % 5 == 0) {
                sb.append('-');
            }
            sb.append(LICENSE_KEY_CHARS.charAt(random.nextInt(LICENSE_KEY_CHARS.length())));
        }

        return sb.toString();
    }

    private LicenseDto mapToDto(LicenseEntity license) {
        int currentActivations = activationHistoryRepository.countActiveLicenses(license.getId());
        return LicenseDto.builder()
                .id(license.getId())
                .licenseKey(license.getLicenseKey())
                .licenseType(license.getLicenseType())
                .status(license.getStatus())
                .creationDate(license.getCreationDate())
                .expirationDate(license.getExpirationDate())
                .maxActivations(license.getMaxActivations())
                .currentActivations(currentActivations)
                .customerEmail(license.getCustomerEmail())
                .customerName(license.getCustomerName())
                .isExpired(license.getExpirationDate() != null && license.getExpirationDate().isBefore(LocalDateTime.now()))
                .isActive(license.getStatus() == LicenseStatus.ACTIVATED)
                .build();
    }

    private ActivationSummaryDto convertToActivationSummaryDto(LicenseActivationHistory activation) {
        return ActivationSummaryDto.builder()
                .id(activation.getId())
                .hardwareFingerprint(activation.getHardwareFingerprint())
                .activationDate(activation.getActivationDate())
                .deactivationDate(activation.getDeactivationDate())
                .clientIp(activation.getClientIp())
                .userAgent(activation.getUserAgent())
                .status(activation.getStatus())
                .build();
    }


    private LicenseDetailDto convertToDetailDto(LicenseEntity license, List<LicenseActivationHistory> activations) {
        List<ActivationSummaryDto> activationDtos = activations.stream()
                .map(this::convertToActivationSummaryDto)
                .toList();

        return LicenseDetailDto.builder()
                .id(license.getId())
                .licenseKey(license.getLicenseKey())
                .licenseType(license.getLicenseType())
                .customerName(license.getCustomerName())
                .customerEmail(license.getCustomerEmail())
                .status(license.getStatus())
                .creationDate(license.getCreationDate())
                .expirationDate(license.getExpirationDate())
                .maxActivations(license.getMaxActivations())
                .notes(license.getNotes())
                .activations(activationDtos)
                .build();
    }

    private LicenseSummaryDto convertToSummaryDto(LicenseEntity license) {
        int activeActivations = activationHistoryRepository.countActiveLicenses(license.getId());

        return LicenseSummaryDto.builder()
                .id(license.getId())
                .licenseKey(license.getLicenseKey())
                .customerName(license.getCustomerName())
                .customerEmail(license.getCustomerEmail())
                .status(license.getStatus())
                .creationDate(license.getCreationDate())
                .expirationDate(license.getExpirationDate())
                .maxActivations(license.getMaxActivations())
                .currentActivations(activeActivations)
                .build();
    }

}