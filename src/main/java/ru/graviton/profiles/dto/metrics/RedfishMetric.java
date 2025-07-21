package ru.graviton.profiles.dto.metrics;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;
import java.util.Objects;

/**
 * Описание метрики
 */
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class RedfishMetric {

    @NotBlank
    private String name;

    @NotBlank
    private String group;

    @NotBlank
    private Type type;

    private String description;

    @JsonProperty("value")
    @NotBlank
    private String valueField;

    private Map<String, String> tags;

    private boolean enable = true;

    @JsonProperty("enable")
    public void setEnable(Boolean enable) {
        this.enable = Objects.requireNonNullElse(enable, true);
    }
}