package ru.graviton.profiles.dto.metrics;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;
import java.util.Objects;

/**
 * Метрика, которую собираем по IPMI
 */
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class IpmiMetric {

    @NotBlank
    private String sensorType;

    private String metricName;

    private String valueField;

    @NotBlank
    private Type type;

    private String description;

    private Map<String, String> tags;

    private boolean enable = true;

    @JsonProperty("enable")
    public void setEnable(Boolean enable) {
        this.enable = Objects.requireNonNullElse(enable, true);
    }
}