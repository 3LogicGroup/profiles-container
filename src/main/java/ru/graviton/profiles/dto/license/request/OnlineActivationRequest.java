package ru.graviton.profiles.dto.license.request;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.*;
import ru.graviton.profiles.dto.ClientDto;

@Builder
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@JsonPropertyOrder({
        "requestSignature",
        "activationData",
        "client"
})
public class OnlineActivationRequest {

    private static final ObjectMapper MAPPER = new ObjectMapper()
            .setSerializationInclusion(JsonInclude.Include.NON_NULL)
            .configure(JsonGenerator.Feature.AUTO_CLOSE_TARGET, false);

    private String requestSignature;

    private ActivationData activationData;
    private ClientDto client;

    @Getter
    @Setter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @JsonPropertyOrder({
            "licenseKey",
            "hardwareFingerprint",
            "applicationVersion",
            "timestamp"
    })
    public static class ActivationData {
        private String licenseKey;
        private String hardwareFingerprint;
        private String applicationVersion;
        private Long timestamp;
    }
}