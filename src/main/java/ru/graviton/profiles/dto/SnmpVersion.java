package ru.graviton.profiles.dto;

import lombok.Getter;

/**
 * Версии протокола SNMP
 */
@Getter
public enum SnmpVersion {
    VERSION_1(0),
    VERSION_2c(1),
    VERSION_3(3),
    ;

    private final int version;

    SnmpVersion(int version) {
        this.version = version;
    }

}
