package ru.graviton.profiles.dto;

import lombok.Getter;

@Getter
public enum DeviceType {
    SERVER("Сервер"),
    PC("ПК"),
    PRINTER("МФУ"),
    STORAGE("СХД"),
    VIRTUAL("Виртуальный сервер"),
    HYPERVISOR("Гипервизор");

    private final String title;

    DeviceType(String title) {
        this.title = title;
    }
}
