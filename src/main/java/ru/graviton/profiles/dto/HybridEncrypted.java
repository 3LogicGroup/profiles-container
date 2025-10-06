package ru.graviton.profiles.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonPropertyOrder({
        "encryptedKey",
        "iv",
        "encryptedData"
})
public class HybridEncrypted {
    public String encryptedKey;   // RSA зашифрованный AES ключ
    public String iv;             // IV для AES
    public String encryptedData;  // данные, зашифрованные AES
}