package ru.graviton.profiles.service.licenses;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.graviton.profiles.dao.entity.LicenseEntity;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    public void notifyActivation(LicenseEntity license, String clientIp) {
        log.info("License activated: {} from IP: {}", license.getLicenseKey(), clientIp);
        // Здесь можно добавить отправку уведомлений по email, webhook и т.д.
    }

    public void notifyDeactivation(LicenseEntity license, String reason) {
        log.info("License deactivated: {}, reason: {}", license.getLicenseKey(), reason);
    }

    public void notifyExpiration(LicenseEntity license) {
        log.info("License expired: {}", license.getLicenseKey());
    }

    public void notifySuspiciousActivity(String licenseKey, String clientIp, String reason) {
        log.warn("Suspicious activity detected for license: {} from IP: {}, reason: {}",
                licenseKey, clientIp, reason);
    }
}