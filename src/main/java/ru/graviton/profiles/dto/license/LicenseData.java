package ru.graviton.profiles.dto.license;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LicenseData {
    private String licenseKey;
    private LicenseType licenseType;
    private String customerCompany;
    private Long issuedDate;
    private String hardwareFingerprint;

    public String toRow() {
        return licenseKey + ":" + licenseType + ":" + customerCompany + ":" + issuedDate + ":" + hardwareFingerprint;
    }
}