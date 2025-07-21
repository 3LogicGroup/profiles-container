package ru.graviton.profiles.dao.entity;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import ru.graviton.profiles.dto.AbstractApiProfileData;
import ru.graviton.profiles.dto.Protocol;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type", visible = true)
@JsonSubTypes({
        @JsonSubTypes.Type(value = IpmiProfile.class, name = "IPMI"),
        @JsonSubTypes.Type(value = RedfishProfile.class, name = "REDFISH"),
        @JsonSubTypes.Type(value = SnmpProfile.class, name = "SNMP"),
        @JsonSubTypes.Type(value = NodeProxyProfile.class, name = "NODE"),
        @JsonSubTypes.Type(value = SshProfile.class, name = "SSH"),
})
@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
public abstract class AbstractProfileData<T extends AbstractApiProfileData<?>> {
    public abstract T toApiProfile();

    public abstract Protocol getType();
}
