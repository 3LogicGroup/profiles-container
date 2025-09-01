package ru.graviton.profiles.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.graviton.profiles.dto.AlertRule;
import ru.graviton.profiles.dto.request.CreateAlertRuleRequest;
import ru.graviton.profiles.dto.request.UpdateAlertRuleRequest;
import ru.graviton.profiles.dto.response.ResultResponse;
import ru.graviton.profiles.dto.response.VoidResultResponse;
import ru.graviton.profiles.service.AlertRuleService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/alert-rule")
@RequiredArgsConstructor
@Tag(name = "Контроллер для работы c правилами событий")
public class AlertRuleController {

    private final AlertRuleService alertRuleService;

    @PostMapping
    @Operation(summary = "Создание правила")
    @ApiResponse(responseCode = "200", description = "Возвращается в случае успешного выполнения запроса")
    @ApiResponse(responseCode = "400", description = "Возвращается в случае ошибки валидации запроса")
    public ResultResponse<AlertRule> createAlertRule(@RequestBody @Validated @Parameter(description = "Запрос на создание")
                                                     CreateAlertRuleRequest request) {
        return ResultResponse.of(alertRuleService.createAlertRule(request.getAlertGroupUids(), request.getName(),
                request.getDescription(), request.getExpression(), request.getIsPromQLExpression(), request.getSeverity(), request.getAlertRuleType(),
                request.getNotificationRuleUids()));
    }

    @GetMapping("list")
    @Operation(summary = "Получение всех правил")
    @ApiResponse(responseCode = "200", description = "Возвращается в случае успешного выполнения запроса")
    public ResultResponse<List<AlertRule>> getAllAlertRules() {
        return ResultResponse.of(alertRuleService.getAllAlertRules());
    }

    @GetMapping("all")
    @Operation(summary = "Получение всех правил")
    @ApiResponse(responseCode = "200", description = "Возвращается в случае успешного выполнения запроса")
    public ResultResponse<Page<AlertRule>> getAllAlertRules(@RequestParam(value = "name", required = false) String name,
                                                            @RequestParam(value = "enabled", required = false) Boolean enabled,
                                                            @RequestParam(value = "page", required = false, defaultValue = "0") Integer page,
                                                            @RequestParam(value = "size", required = false, defaultValue = "10") Integer size) {
        return ResultResponse.of(alertRuleService.getAllAlertRules(name, enabled, page, size));
    }

    @GetMapping("{rule-uid}")
    @Operation(summary = "Получение правила по UID")
    @ApiResponse(responseCode = "200", description = "Возвращается в случае успешного выполнения запроса")
    @ApiResponse(responseCode = "404", description = "Возвращается в случае, если правило не найдено")
    public ResultResponse<AlertRule> getAlertRule(@PathVariable("rule-uid") @Parameter(description = "UID правила") UUID ruleUid) {
        return ResultResponse.of(alertRuleService.getActiveAlertRuleByUid(ruleUid, true));
    }

    @PatchMapping("change-activity/{rule-uid}")
    @Operation(summary = "Изменение активности правила")
    @ApiResponse(responseCode = "200", description = "Возвращается в случае успешного выполнения запроса")
    @ApiResponse(responseCode = "404", description = "Возвращается в случае, если правило не найдено")
    public VoidResultResponse changeActivityAlertRule(@PathVariable("rule-uid") @Parameter(description = "UID правила") UUID ruleUid,
                                                      @RequestParam("activity") @Parameter(description = "флаг активности") Boolean activity) {
        alertRuleService.changeActivityAlertRule(ruleUid, activity);
        return new VoidResultResponse();
    }

    @DeleteMapping
    @Operation(summary = "Удаление правила")
    @ApiResponse(responseCode = "200", description = "Возвращается в случае успешного выполнения запроса")
    @ApiResponse(responseCode = "404", description = "Возвращается в случае, если правило не найдено")
    public void deleteAlertRule(@RequestParam("rule-uid") @Parameter(description = "UID правила") UUID ruleUid) {
        alertRuleService.deleteAlertRule(ruleUid);
    }

    @PatchMapping
    @Operation(summary = "Обновление правила")
    @ApiResponse(responseCode = "200", description = "Возвращается в случае успешного выполнения запроса")
    @ApiResponse(responseCode = "400", description = "Возвращается в случае ошибки валидации запроса")
    @ApiResponse(responseCode = "404", description = "Возвращается в случае, если правило не найдено")
    public ResultResponse<AlertRule> updateAlertRule(@RequestBody @Validated @Parameter(description = "Запрос на обновление")
                                                     UpdateAlertRuleRequest request) {
        return ResultResponse.of(alertRuleService.updateAlertRule(request.getUid(), request.getAlertGroupUids(), request.getName(),
                request.getDescription(), request.getExpression(), request.getIsPromQLExpression(), request.getSeverity(), request.getAlertRuleType()));
    }

    @PatchMapping("link-notification-rule")
    @Operation(summary = "Привязка правила оповещения к правилу событий")
    @ApiResponse(responseCode = "200", description = "Возвращается в случае успешного выполнения запроса")
    @ApiResponse(responseCode = "404", description = "Возвращается в случае, если правило не найдено")
    public void linkNotificationRuleToAlertRule(@RequestParam("rule-uid") @Parameter(description = "UID правила событий") UUID ruleUid,
                                                @RequestParam("notification-uid") @Parameter(description = "UID правила оповещений") UUID notificationUid) {
        alertRuleService.linkNotificationRuleToAlertRule(ruleUid, notificationUid);
    }

    @PatchMapping("unlink-notification-rule")
    @Operation(summary = "Отвязка правила оповещения от правила событий")
    @ApiResponse(responseCode = "200", description = "Возвращается в случае успешного выполнения запроса")
    @ApiResponse(responseCode = "404", description = "Возвращается в случае, если правило не найдено")
    public void unlinkNotificationRuleFromAlertRule(@RequestParam("rule-uid") @Parameter(description = "UID правила событий") UUID ruleUid,
                                                    @RequestParam("notification-uid") @Parameter(description = "UID правила оповещений") UUID notificationUid) {
        alertRuleService.unlinkNotificationRuleFromAlertRule(ruleUid, notificationUid);
    }
}
