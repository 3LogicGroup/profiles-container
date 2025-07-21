package ru.graviton.profiles.dto.metrics;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Objects;

/**
 * Описание группы метрик
 */
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class RedfishMetricGroup {

    @NotBlank
    private String url;

    private boolean hasCollection;

    private List<RedfishMetric> metrics;

    private boolean enable = true;

    @JsonProperty("enable")
    public void setEnable(Boolean enable) {
        this.enable = Objects.requireNonNullElse(enable, true);
    }
}
