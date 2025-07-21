package ru.graviton.profiles.dto.metrics;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class UiApiField {
    private String name;
    private Map<String, String> fieldDescriptors;
}
