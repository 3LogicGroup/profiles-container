package ru.graviton.profiles.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Collection;

@AllArgsConstructor
@Getter
public enum Severity {
    INFO("Незначительный"),
    WARNING("Серьезный"),
    CRITICAL("Критичный");

    private final String description;

}
