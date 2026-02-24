package ru.graviton.profiles.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.graviton.profiles.service.AlertSqlService;
import ru.graviton.profiles.service.ProfileService;

@RestController
@RequestMapping("sql")
@RequiredArgsConstructor
public class SqlController {

    private final ProfileService profileService;
    private final AlertSqlService alertSqlService;

    @GetMapping("/profiles")
    @Operation(summary = "Получение всех профилей(SQL)")
    @ApiResponse(responseCode = "200", description = "Возвращается в случае успешного выполнения запроса")
    @ApiResponse(responseCode = "404", description = "Возвращается в случае если профили не найдены",
            content = @Content(schema = @Schema()))
    public String getProfilesSql() {
        return profileService.getProfilesSQL();
    }

    @GetMapping("/alerts")
    @Operation(summary = "Получение всех уведомлений(SQL)")
    @ApiResponse(responseCode = "200", description = "Возвращается в случае успешного выполнения запроса")
    @ApiResponse(responseCode = "404", description = "Возвращается в случае если профили не найдены",
            content = @Content(schema = @Schema()))
    public String getAlertsSql() {
        return alertSqlService.generateSql();
    }
}
