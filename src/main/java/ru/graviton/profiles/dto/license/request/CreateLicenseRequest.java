package ru.graviton.profiles.dto.license.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import ru.graviton.profiles.dto.license.LicenseType;

@Data
public class CreateLicenseRequest {

    private LicenseType licenseType;

    @NotNull(message = "Max activations is required")
    @Min(value = 1, message = "Max activations must be at least 1")
    private Integer maxActivations;

    @Email(message = "Valid email is required")
    private String customerEmail;

    private String customerName;

}