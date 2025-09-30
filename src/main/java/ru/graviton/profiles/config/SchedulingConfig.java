package ru.graviton.profiles.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableScheduling
@RequiredArgsConstructor
@Slf4j
public class SchedulingConfig {
    
//    private final LicenseActivationService licenseActivationService;
//
//    @Scheduled(cron = "0 0 2 * * ?") // Каждый день в 2:00
//    public void cleanupExpiredActivations() {
//        log.info("Starting scheduled cleanup of expired activations");
//        licenseActivationService.cleanupExpiredActivations();
//        log.info("Completed scheduled cleanup of expired activations");
//    }
}