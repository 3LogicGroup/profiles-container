package ru.graviton.profiles.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.graviton.profiles.dto.HybridEncrypted;
import ru.graviton.profiles.dto.license.ActivationMethod;
import ru.graviton.profiles.dto.license.response.OnlineActivationResponse;
import ru.graviton.profiles.service.licenses.ActivationService;

@RestController
@RequestMapping("license/")
@RequiredArgsConstructor
@Slf4j
public class LicenseActivationController {

    private final ActivationService activationService;


    @PostMapping("/activate")
    public ResponseEntity<OnlineActivationResponse> activateLicense(
            @RequestBody HybridEncrypted request, HttpServletRequest httpRequest) {

        OnlineActivationResponse onlineActivationResponse = activationService.processActivation(request,
                ActivationMethod.ONLINE,
                getClientIpAddress(httpRequest), httpRequest.getHeader("User-Agent"));

        HttpStatus status = HttpStatus.OK;
        return ResponseEntity.status(status).body(onlineActivationResponse);
    }

    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }

        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty()) {
            return xRealIp;
        }

        return request.getRemoteAddr();
    }
}