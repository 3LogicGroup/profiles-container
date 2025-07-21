package ru.graviton.profiles.dao.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import ru.graviton.profiles.dto.*;
import ru.graviton.profiles.dto.metrics.SnmpMetric;

import java.util.List;
import java.util.Map;

/**
 * Описание профиля
 */
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class SnmpProfile extends AbstractProfileData<SnmpApiProfile> {

    private Map<String, String> info;

    private String productNameOid;
    private String serialNumberOid;

    private List<SnmpMetric> metrics;

    private SnmpVersion snmpVersion;

    private PrivilegeProtocol privilegeProtocol;

    private AuthorityProtocol authorityProtocol;

    private String privPass;

    private String login;

    private String password;

    private String community;

    @Override
    public SnmpApiProfile toApiProfile() {
        return SnmpApiProfile.builder()
                .productNameOid(getProductNameOid())
                .serialNumberOid(getSerialNumberOid())
                .info(getInfo())
                .metrics(getMetrics())
                .community(getCommunity())
                .snmpVersion(getSnmpVersion())
                .privilegeProtocol(getPrivilegeProtocol())
                .authorityProtocol(getAuthorityProtocol())
                .privPass(getPrivPass())
                .login(getLogin())
                .password(getPassword())
                .build();
    }

    @Override
    public Protocol getType() {
        return Protocol.SNMP;
    }
}
