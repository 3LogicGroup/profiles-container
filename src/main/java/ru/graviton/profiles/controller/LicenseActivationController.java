package ru.graviton.profiles.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.graviton.profiles.dto.license.request.OnlineActivationRequest;
import ru.graviton.profiles.dto.license.response.OnlineActivationResponse;
import ru.graviton.profiles.service.licenses.OnlineActivationService;

@RestController
@RequestMapping("license/")
@RequiredArgsConstructor
@Slf4j
public class LicenseActivationController {

    private final OnlineActivationService onlineActivationService;


    @PostMapping("/activate")
    public ResponseEntity<OnlineActivationResponse> activateLicense(
            @Valid @RequestBody OnlineActivationRequest request,
            HttpServletRequest httpRequest) {

        OnlineActivationResponse onlineActivationResponse = onlineActivationService.processActivation(request,
                getClientIpAddress(httpRequest), httpRequest.getHeader("User-Agent"));

        HttpStatus status = onlineActivationResponse.isSuccess() ? HttpStatus.OK : HttpStatus.BAD_REQUEST;
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