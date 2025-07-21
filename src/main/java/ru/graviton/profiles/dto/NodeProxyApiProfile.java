package ru.graviton.profiles.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import ru.graviton.profiles.dao.entity.NodeProxyProfile;

import java.util.List;

/**
 * Профайл
 */
@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class NodeProxyApiProfile extends AbstractApiProfileData<NodeProxyProfile> {
    private String login;
    private String password;
    @JsonProperty("proxy-metrics")
    private List<String> proxyMetrics;

    @Override
    public NodeProxyProfile toProfile() {
        return NodeProxyProfile.builder()
                .login(getLogin())
                .password(getPassword())
                .proxyMetrics(getProxyMetrics())
                .build();
    }
}
