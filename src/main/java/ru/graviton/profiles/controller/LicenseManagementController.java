
package ru.graviton.profiles.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.graviton.profiles.dto.license.*;
import ru.graviton.profiles.dto.license.request.CreateLicenseRequest;
import ru.graviton.profiles.dto.license.request.GeneratedLicenseResponse;
import ru.graviton.profiles.dto.license.request.RevokeLicenseRequest;
import ru.graviton.profiles.dto.license.response.StatusResponse;
import ru.graviton.profiles.dto.response.ResultResponse;
import ru.graviton.profiles.service.licenses.LicenseGenerationService;
import ru.graviton.profiles.service.licenses.LicenseService;

@RestController
@RequestMapping("/licenses")
@RequiredArgsConstructor
//@PreAuthorize("hasRole('LICENSE_ADMIN')")
@Tag(name = "Сервис для управления лицензиями")
public class LicenseManagementController {

    private final LicenseGenerationService licenseGenerationService;
    private final LicenseService licenseService;

    @PostMapping
    @Operation(summary = "Генерация новой лицензии")
    public ResponseEntity<GeneratedLicenseResponse> generateLicense(
            @RequestBody @Valid CreateLicenseRequest request) {

        GeneratedLicense license = licenseGenerationService.generateLicense(request);

        GeneratedLicenseResponse response = GeneratedLicenseResponse.builder()
                .licenseKey(license.getLicenseKey())
                .expirationDate(license.getExpirationDate())
                .status("SUCCESS")
                .message("License generated successfully")
                .build();

        return ResponseEntity.ok(response);
    }

    @GetMapping
    @Operation(summary = "Получить все лицензии")
    public ResultResponse<Page<LicenseSummaryDto>> getAllLicenses(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String customerCompany,
            @RequestParam(required = false) LicenseStatus status) {
        return ResultResponse.of(licenseService.getAllLicenses(PageRequest.of(page, size), customerCompany, status));
    }

    @GetMapping("/{licenseKey}")
    @Operation(summary = "Получить информацию о лицензии")
    public ResultResponse<LicenseDetailDto> getLicenseDetails(@PathVariable String licenseKey) {
        return ResultResponse.of(licenseService.findByLicenseKey(licenseKey));
    }

    @PutMapping("/{licenseKey}/revoke")
    @Operation(summary = "Отозвать лицензию")
    public ResultResponse<StatusResponse> revokeLicense(
            @PathVariable String licenseKey,
            @RequestBody @Valid RevokeLicenseRequest request) {
        licenseService.revokeLicense(licenseKey, request.getReason());
        return ResultResponse.of(StatusResponse.success("License revoked successfully"));
    }

    @GetMapping("/stats")
    @Operation(summary = "Получить статистику по лицензиям")
    public ResultResponse<LicenseStatsDto> getLicenseStats() {
        return ResultResponse.of(licenseService.getLicenseStats());
    }


}