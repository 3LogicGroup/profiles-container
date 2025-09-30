package ru.graviton.profiles.dto.license.request;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OnlineActivationRequest {

    @NotBlank
    private String requestSignature;
    @NotBlank
    private String activationData;
    @NotNull
    private UUID clientUid;

    @Getter
    @Setter
    @Builder
    public static class ActivationData {

        @NotBlank(message = "License key is required")
        private String licenseKey;
        private String hardwareFingerprint;
        private String applicationVersion;
        private Long timestamp;

        public static ActivationData fromRow(String row) {
            String[] split = row.split(":");
            return ActivationData.builder()
                    .licenseKey(split[0])
                    .hardwareFingerprint(split[1])
                    .applicationVersion(split[2])
                    .timestamp(Long.parseLong(split[3]))
                    .build();
        }
    }
}