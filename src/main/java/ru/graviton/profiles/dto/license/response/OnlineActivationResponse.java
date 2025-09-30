package ru.graviton.profiles.dto.license.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OnlineActivationResponse {
    private boolean success;
    private String message;
    private String signature;
    private String licenseData;

    public static OnlineActivationResponse failure(String message) {
        return OnlineActivationResponse.builder()
                .message(message)
                .success(false)
                .build();
    }

    public static OnlineActivationResponse success(String licenseData, String signature) {
        return OnlineActivationResponse.builder()
                .signature(signature)
                .licenseData(licenseData)
                .message("Лицензия активирована успешно")
                .success(true)
                .build();
    }


}