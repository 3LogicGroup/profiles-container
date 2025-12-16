package ru.graviton.profiles.dto.license;

import lombok.Getter;
import ru.graviton.profiles.dto.Protocol;

import java.util.Set;

@Getter
public enum LicenseType {
    TRIAL("Trial", 30, 10, Set.of(Protocol.SNMP)),
    BASIC("Basic", 365, Integer.MAX_VALUE, Set.of(Protocol.SNMP, Protocol.SSH)),
    PROFESSIONAL("Professional", 365, 200, Set.of(Protocol.values())),
    ENTERPRISE("Enterprise", 365, Integer.MAX_VALUE, Set.of(Protocol.values())),
    UNLIMITED("Unlimited", Integer.MAX_VALUE, Integer.MAX_VALUE, Set.of(Protocol.values()));

    private final String displayName;
    private final int validityDays;
    private final int maxTargets;
    private final Set<Protocol> allowedProtocols;

    LicenseType(String displayName, int validityDays, int maxTargets, Set<Protocol> allowedProtocols) {
        this.displayName = displayName;
        this.validityDays = validityDays;
        this.maxTargets = maxTargets;
        this.allowedProtocols = allowedProtocols;
    }
}