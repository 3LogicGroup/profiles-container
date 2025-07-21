package ru.graviton.profiles.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.experimental.SuperBuilder;
import ru.graviton.profiles.dao.entity.IpmiProfile;
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
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class IpmiApiProfile extends AbstractApiProfileData<IpmiProfile> {

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
    public IpmiProfile toProfile() {
        return IpmiProfile.builder()
                .login(getLogin())
                .password(getPassword())
                .info(getInfo())
                .metricsGroups(getMetricsGroups())
                .build();
    }
}
