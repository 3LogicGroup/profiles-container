package ru.graviton.profiles.dto;

import lombok.Getter;

/**
 * Типы разрешений
 */
@Getter
public enum AuthorityProtocol {
    AUTH_HMAC128SHA224("1.3.6.1.6.3.10.1.1.4"),
    AUTH_HMAC192SHA256("1.3.6.1.6.3.10.1.1.5"),
    AUTH_HMAC256SHA384("1.3.6.1.6.3.10.1.1.6"),
    AUTH_HMAC384SHA512("1.3.6.1.6.3.10.1.1.7"),
    AES_192_3DESKeyExtension("1.3.6.1.4.1.4976.2.2.1.2.1"),
    AUTH_MD5("1.3.6.1.6.3.10.1.1.2"),
    AUTH_SHA("1.3.6.1.6.3.10.1.1.3");


    private final String oid;

    AuthorityProtocol(String oid) {
        this.oid = oid;
    }
}
