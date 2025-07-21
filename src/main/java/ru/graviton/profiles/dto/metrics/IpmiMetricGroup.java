package ru.graviton.profiles.dto.metrics;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Objects;

/**
 * Группа
 */
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class IpmiMetricGroup {

    private String group;

    private List<IpmiMetric> metrics;

    private boolean enable = true;

    @JsonProperty("enable")
    public void setEnable(Boolean enable) {
        this.enable = Objects.requireNonNullElse(enable, true);
    }
}

