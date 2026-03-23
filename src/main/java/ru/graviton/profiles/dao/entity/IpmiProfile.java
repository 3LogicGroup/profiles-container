package ru.graviton.profiles.dao.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.experimental.SuperBuilder;
import ru.graviton.profiles.dto.IpmiApiProfile;
import ru.graviton.profiles.dto.Protocol;

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
                .enable(enable)
                .build();
    }

    @Override
    public Protocol getType() {
        return Protocol.IPMI;
    }
}
