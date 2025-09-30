package ru.graviton.profiles.dto.license;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ActivationStatus {
    SUCCESS("Успешно"),
    FAILED("Ошибка"),
    DEACTIVATED("Деактивирована"),
    EXPIRED("Истекла"),
    PENDING("Ожидает");
    
    private final String displayName;
}