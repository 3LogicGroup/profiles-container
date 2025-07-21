package ru.graviton.profiles.dto;

import lombok.Getter;

/**
 * Типы шифрований протокола
 */
@Getter
public enum PrivilegeProtocol {
    DES("1.3.6.1.6.3.10.1.2.2"),
    DES_3("1.3.6.1.6.3.10.1.2.3"),
    AES_128("1.3.6.1.6.3.10.1.2.4"),
    AES_192("1.3.6.1.4.1.4976.2.2.1.1.1"),
    AES_192_3DESKeyExtension("1.3.6.1.4.1.4976.2.2.1.2.1"),
    AES_256("1.3.6.1.4.1.4976.2.2.1.1.2"),
    AES_256_3DESKeyExtension("1.3.6.1.4.1.4976.2.2.1.2.2");

    private final String oid;

    PrivilegeProtocol(String oid) {
        this.oid = oid;
    }
}
