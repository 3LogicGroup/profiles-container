package ru.graviton.profiles.dao.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.experimental.SuperBuilder;
import ru.graviton.profiles.dto.IpmiApiProfile;
import ru.graviton.profiles.dto.Protocol;
import ru.graviton.profiles.dto.metrics.IpmiMetricGroup;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Профайл
 */
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class IpmiProfile extends AbstractProfileData<IpmiApiProfile> {

    private String login;

    private String password;

    private Map<String, String> info;

    @Builder.Default
    private List<IpmiMetricGroup> metricsGroups = new ArrayList<>();

    @Builder.Default
    private boolean enable = true;

    @JsonProperty("enable")
    public void setEnable(Boolean enable) {
        this.enable = Objects.requireNonNullElse(enable, true);
    }


    @Override
    public IpmiApiProfile toApiProfile() {
        return IpmiApiProfile.builder()
                .login(getLogin())
                .password(getPassword())
                .info(getInfo())
                .metricsGroups(getMetricsGroups())
                .enable(enable)
                .build();
    }

    @Override
    public Protocol getType() {
        return Protocol.IPMI;
    }
}
