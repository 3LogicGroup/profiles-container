package ru.graviton.profiles.dto.license;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonPropertyOrder({
        "licenseKey",
        "licenseType",
        "customerCompany",
        "issuedDate",
        "hardwareFingerprint"
})
public class LicenseData {
    private String licenseKey;
    private LicenseType licenseType;
    private String customerCompany;
    private Long issuedDate;
    private String hardwareFingerprint;
}