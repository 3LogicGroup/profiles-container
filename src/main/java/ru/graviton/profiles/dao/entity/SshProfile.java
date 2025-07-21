package ru.graviton.profiles.dao.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import lombok.experimental.SuperBuilder;
import ru.graviton.profiles.dto.Protocol;
import ru.graviton.profiles.dto.SshApiProfile;
/**
 * Профайл
 */
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class SshProfile extends AbstractProfileData<SshApiProfile> {


    private String login;

    private String password;

    @Builder.Default
    private Integer port = 22;

    @Override
    public SshApiProfile toApiProfile() {
        return SshApiProfile.builder()
                .login(getLogin())
                .password(getPassword())
                .port(getPort())
                .build();
    }

    @Override
    public Protocol getType() {
        return Protocol.SSH;
    }
}
