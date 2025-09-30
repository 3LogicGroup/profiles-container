package ru.graviton.profiles.service.licenses;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.graviton.profiles.dao.entity.ClientEntity;
import ru.graviton.profiles.dao.entity.LicenseActivationHistory;
import ru.graviton.profiles.dao.entity.LicenseEntity;
import ru.graviton.profiles.dao.repository.ClientRepository;
import ru.graviton.profiles.dao.repository.LicenseActivationHistoryRepository;
import ru.graviton.profiles.dao.repository.LicenseRepository;
import ru.graviton.profiles.dto.license.*;
import ru.graviton.profiles.dto.license.request.OnlineActivationRequest;
import ru.graviton.profiles.dto.license.response.OnlineActivationResponse;

import java.security.PublicKey;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class OnlineActivationService {

    private final LicenseRepository licenseRepository;
    private final LicenseActivationHistoryRepository activationHistoryRepository;
    private final CryptographyService cryptographyService;
    private final NotificationService notificationService;
    private final RateLimitService rateLimitService;
    private final ClientRepository clientRepository;


    @Transactional
    public OnlineActivationResponse processActivation(OnlineActivationRequest request, String clientIp, String userAgent) {
        String decrypt;
        PublicKey publicKey;
        ClientEntity client;
        try {
            client = clientRepository.findById(request.getClientUid())
                    .orElse(null);
            if (client == null) {
                log.warn("client not registered: {}", request.getClientUid());
                return OnlineActivationResponse.failure("Нет информации о клиенте");
            }
            publicKey = cryptographyService.getPublicKey(client.getPublicKey());
            // Проверяем подпись запроса
            if (!cryptographyService.verifyActivationRequest(publicKey, request.getActivationData(), request.getRequestSignature())) {
                log.warn("Invalid activation request signature from IP: {}", clientIp);
                return OnlineActivationResponse.failure("Неверная подпись запроса");
            }
            decrypt = cryptographyService.decrypt(request.getActivationData());

        } catch (Exception e) {
            log.error("Error during license activation", e);
            recordFailedActivation(null, clientIp, userAgent, "Внутренняя ошибка сервера");
            return OnlineActivationResponse.failure("Внутренняя ошибка активации");
        }
        OnlineActivationRequest.ActivationData activationData = OnlineActivationRequest.ActivationData.fromRow(decrypt);
        try {
            // Проверяем rate limiting
            if (!rateLimitService.isAllowed(clientIp, "activation", 5, Duration.ofMinutes(10))) {
                log.warn("Rate limit exceeded for IP: {}", clientIp);
                return OnlineActivationResponse.failure("Слишком много попыток активации. Попробуйте позже.");
            }


            // Ищем лицензию
            Optional<LicenseEntity> licenseOpt = licenseRepository.findByLicenseKey(activationData.getLicenseKey());
            if (licenseOpt.isEmpty()) {
                log.warn("License key not found: {} from IP: {}", activationData.getLicenseKey(), clientIp);
                recordFailedActivation(activationData, clientIp, userAgent, "Лицензионный ключ не найден");
                return OnlineActivationResponse.failure("Лицензионный ключ не найден");
            }

            LicenseEntity license = licenseOpt.get();
            license.setClient(client);
            // Проверяем статус лицензии
            ValidationResult validation = validateLicenseForActivation(license, activationData);
            if (!validation.isValid()) {
                recordFailedActivation(activationData, clientIp, userAgent, validation.getErrorMessage());
                return OnlineActivationResponse.failure(validation.getErrorMessage());
            }

            // Проверяем лимит активаций
            int currentActivations = activationHistoryRepository.countActiveLicenses(license.getId());
            if (currentActivations >= license.getMaxActivations()) {
                // Проверяем, может это реактивация на том же железе
                Optional<LicenseActivationHistory> existingActivation =
                        activationHistoryRepository.findByLicenseIdAndHardwareFingerprintAndIsActiveTrue(
                                license.getId(), activationData.getHardwareFingerprint());

                if (existingActivation.isEmpty()) {
                    recordFailedActivation(activationData, clientIp, userAgent, "Превышен лимит активаций");
                    return OnlineActivationResponse.failure("Превышен лимит активаций для данной лицензии");
                }
            }

            // Записываем успешную активацию
            recordSuccessfulActivation(license, activationData, clientIp, userAgent);

            // Обновляем статус лицензии
            if (license.getStatus() != LicenseStatus.ACTIVATED) {
                license.setStatus(LicenseStatus.ACTIVATED);
            }
            license.setUpdatedAt(LocalDateTime.now());
            licenseRepository.save(license);
            // Уведомляем об активации
            notificationService.notifyActivation(license, clientIp);

            log.info("Successfully activated license {} for hardware {}",
                    license.getLicenseKey(), activationData.getHardwareFingerprint());
            String encrypt = cryptographyService.encrypt(LicenseData.builder()
                    .licenseKey(license.getLicenseKey())
                    .licenseType(license.getLicenseType())
                    .issuedDate(license.getCreationDate().atZone(ZoneId.systemDefault())
                            .toInstant()
                            .toEpochMilli())
                    .customerCompany(license.getCustomerName())
                    .hardwareFingerprint(activationData.getHardwareFingerprint())
                    .build()
                    .toRow(), publicKey);

            return OnlineActivationResponse.success(encrypt, cryptographyService.signData(encrypt));
        } catch (Exception e) {
            log.error("Error during license activation", e);
            recordFailedActivation(activationData, clientIp, userAgent, "Внутренняя ошибка сервера");
            return OnlineActivationResponse.failure("Внутренняя ошибка активации");
        }
    }

    private ValidationResult validateLicenseForActivation(LicenseEntity license, OnlineActivationRequest.ActivationData request) {

        // Проверяем статус лицензии
        if (license.getStatus() == LicenseStatus.REVOKED) {
            return ValidationResult.invalid("Лицензия отозвана");
        }
        if (license.getStatus() == LicenseStatus.SUSPENDED) {
            return ValidationResult.invalid("Лицензия приостановлена");
        }

        // Проверяем срок действия
        if (license.getExpirationDate() != null && license.getExpirationDate().isBefore(LocalDateTime.now())) {
            return ValidationResult.invalid("Срок действия лицензии истек");
        }

        // Проверяем версию приложения (если указана)
        if (request.getApplicationVersion() != null) {
            if (!isApplicationVersionSupported(request.getApplicationVersion())) {
                return ValidationResult.invalid("Неподдерживаемая версия приложения");
            }
        }

        return ValidationResult.valid();
    }

    private boolean isApplicationVersionSupported(String version) {
        // Простая проверка версии - можно расширить
        if (version == null || version.trim().isEmpty()) {
            return false;
        }

        // Например, поддерживаем версии начиная с 1.0.0
        try {
            String[] parts = version.split("\\.");
            if (parts.length >= 2) {
                int major = Integer.parseInt(parts[0]);
                return major >= 1;
            }
        } catch (NumberFormatException e) {
            log.warn("Invalid version format: {}", version);
        }

        return true; // По умолчанию разрешаем
    }

    private void recordSuccessfulActivation(LicenseEntity license, OnlineActivationRequest.ActivationData request,
                                            String clientIp, String userAgent) {
        LicenseActivationHistory history = LicenseActivationHistory.builder()
                .license(license)
                .hardwareFingerprint(request.getHardwareFingerprint())
                .activationDate(LocalDateTime.now())
                .clientIp(clientIp)
                .userAgent(userAgent)
                .applicationVersion(request.getApplicationVersion())
                .activationMethod(ActivationMethod.ONLINE)
                .status(ActivationStatus.SUCCESS)
                .build();

        activationHistoryRepository.save(history);
    }

    private void recordFailedActivation(OnlineActivationRequest.ActivationData request, String clientIp,
                                        String userAgent, String errorMessage) {
        LicenseActivationHistory history = LicenseActivationHistory.builder()
                .hardwareFingerprint(Optional.ofNullable(request)
                        .map(OnlineActivationRequest.ActivationData::getHardwareFingerprint)
                        .orElse(null))
                .activationDate(LocalDateTime.now())
                .clientIp(clientIp)
                .userAgent(userAgent)
                .applicationVersion(Optional.ofNullable(request)
                        .map(OnlineActivationRequest.ActivationData::getApplicationVersion)
                        .orElse(null))
                .activationMethod(ActivationMethod.ONLINE)
                .status(ActivationStatus.FAILED)
                .errorMessage(errorMessage)
                .build();

        activationHistoryRepository.save(history);
    }
}