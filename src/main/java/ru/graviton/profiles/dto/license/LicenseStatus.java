package ru.graviton.profiles.dto.license;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum LicenseStatus {
    ISSUED("Выдана"),
    ACTIVATED("Активирована"), 
    EXPIRED("Истекла"),
    REVOKED("Отозвана"),
    SUSPENDED("Приостановлена");
    
    private final String displayName;
}