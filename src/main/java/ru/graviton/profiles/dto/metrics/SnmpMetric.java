package ru.graviton.profiles.dto.metrics;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

/**
     * Метрика
     */
    @Getter
    @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public  class SnmpMetric {

        @NotBlank
        private String name;

        @NotBlank
        private Type type;

        private String description;

        @NotBlank
        private String oid;

        private boolean enable = true;

        @JsonProperty("enable")
        public void setEnable(Boolean enable) {
            this.enable = Objects.requireNonNullElse(enable, true);
        }
    }
