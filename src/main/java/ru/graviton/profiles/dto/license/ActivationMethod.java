package ru.graviton.profiles.dto.license;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ActivationMethod {
    ONLINE("Онлайн"),
    EMAIL("По email"),
    MANUAL("Вручную");
    
    private final String displayName;
}