package ru.graviton.profiles.service.licenses;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.graviton.profiles.dto.license.GeneratedLicense;
import ru.graviton.profiles.dto.license.LicenseDto;
import ru.graviton.profiles.dto.license.request.CreateLicenseRequest;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class LicenseGenerationService {

    private final LicenseService licenseService;

    public GeneratedLicense generateLicense(CreateLicenseRequest request) {
        log.info("Generating new license for customer: {}", request.getCustomerEmail());

        LicenseDto license = licenseService.createLicense(request);

        return GeneratedLicense.builder()
                .licenseKey(license.getLicenseKey())
                .expirationDate(license.getExpirationDate())
                .customerName(license.getCustomerName())
                .customerEmail(license.getCustomerEmail())
                .build();
    }
}