package ru.graviton.profiles.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import lombok.experimental.SuperBuilder;
import ru.graviton.profiles.dao.entity.SshProfile;

/**
 * Профайл
 */
@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class SshApiProfile extends AbstractApiProfileData<SshProfile> {
    private String login;
    private String password;
    @Builder.Default
    private Integer port = 22;

    @Override
    public SshProfile toProfile() {
        return SshProfile.builder()
                .login(getLogin())
                .password(getPassword())
                .port(getPort())
                .build();
    }
}
