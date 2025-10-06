package ru.graviton.profiles.dto.license.response;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.*;
import ru.graviton.profiles.dto.HybridEncrypted;

@Data
@Builder
public class OnlineActivationResponse {
    private boolean success;
    private String message;
    private HybridEncrypted encrypted;


    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @JsonPropertyOrder({
            "signature",
            "licenseData"
    })
    public static class ActivationResult {
        private String signature;
        private HybridEncrypted licenseData;
    }

    public static OnlineActivationResponse failure(String message) {
        return OnlineActivationResponse.builder()
                .message(message)
                .success(false)
                .build();
    }

    public static OnlineActivationResponse success(HybridEncrypted encrypted) {
        return OnlineActivationResponse.builder()
                .encrypted(encrypted)
                .message("Лицензия активирована успешно")
                .success(true)
                .build();
    }


}