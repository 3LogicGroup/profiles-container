package ru.graviton.profiles.dao.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import ru.graviton.profiles.dto.NodeProxyApiProfile;
import ru.graviton.profiles.dto.Protocol;

import java.util.List;

/**
 * Профайл
 */
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class NodeProxyProfile extends AbstractProfileData<NodeProxyApiProfile> {


    private String login;

    private String password;

    @JsonProperty("proxy-metrics")
    private List<String> proxyMetrics;

    @Override
    public NodeProxyApiProfile toApiProfile() {
        return NodeProxyApiProfile.builder()
                .login(getLogin())
                .password(getPassword())
                .proxyMetrics(getProxyMetrics())
                .build();
    }

    @Override
    public Protocol getType() {
        return Protocol.NODE;
    }
}
