package ru.graviton.profiles.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.graviton.profiles.dto.AlertGroup;
import ru.graviton.profiles.dto.request.CreateAlertGroupRequest;
import ru.graviton.profiles.dto.request.UpdateAlertGroupRequest;
import ru.graviton.profiles.dto.response.ResultResponse;
import ru.graviton.profiles.dto.response.VoidResultResponse;
import ru.graviton.profiles.service.AlertGroupService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("alert-group")
@RequiredArgsConstructor
@Tag(name = "Контроллер для работы c группами правилам событий")
public class AlertGroupController {

    private final AlertGroupService alertGroupService;

    @PostMapping
    @Operation(summary = "Создание группы правила")
    @ApiResponse(responseCode = "200", description = "Возвращается в случае успешного выполнения запроса")
    @ApiResponse(responseCode = "400", description = "Возвращается в случае ошибки валидации запроса")
    public ResultResponse<AlertGroup> createAlertGroup(@RequestBody @Validated @Parameter(description = "Запрос на создание")
                                                       CreateAlertGroupRequest request) {
        return ResultResponse.of(alertGroupService.createAlertGroup(request.getGroupName(), request.getDescription(),
                request.getNotificationRuleUids(), request.getAlertRuleUids()));
    }
    @GetMapping("list")
    @Operation(summary = "Получение всех групп правил")
    @ApiResponse(responseCode = "200", description = "Возвращается в случае успешного выполнения запроса")
    public ResultResponse<List<AlertGroup>> getListAlertGroups() {
        return ResultResponse.of(alertGroupService.getAllAlertGroups());
    }

    @GetMapping("all")
    @Operation(summary = "Получение всех групп правил")
    @ApiResponse(responseCode = "200", description = "Возвращается в случае успешного выполнения запроса")
    public ResultResponse<Page<AlertGroup>> getAllAlertGroups(@RequestParam(value = "name", required = false) String name,
                                                              @RequestParam(value = "enabled", required = false) Boolean enabled,
                                                              @RequestParam(value = "page", required = false, defaultValue = "0") Integer page,
                                                              @RequestParam(value = "size", required = false, defaultValue = "10") Integer size) {
        return ResultResponse.of(alertGroupService.getAllAlertGroups(name, enabled, page, size));
    }

    @GetMapping("{group-uid}")
    @Operation(summary = "Получение группы правил по UID")
    @ApiResponse(responseCode = "200", description = "Возвращается в случае успешного выполнения запроса")
    @ApiResponse(responseCode = "404", description = "Возвращается в случае, если правило не найдено")
    public ResultResponse<AlertGroup> getAlertRule(@PathVariable("group-uid") @Parameter(description = "UID правила") UUID ruleUid) {
        return ResultResponse.of(alertGroupService.getActiveAlertGroupByUid(ruleUid));
    }

    @PatchMapping("change-activity/{group-uid}")
    @Operation(summary = "Изменение активности группы правил")
    @ApiResponse(responseCode = "200", description = "Возвращается в случае успешного выполнения запроса")
    @ApiResponse(responseCode = "404", description = "Возвращается в случае, если правило не найдено")
    public VoidResultResponse changeActivityAlertRule(@PathVariable("group-uid") @Parameter(description = "UID группы правил") UUID groupUid,
                                                      @RequestParam("activity") @Parameter(description = "флаг активности") Boolean activity) {
        alertGroupService.changeActivityAlertGroup(groupUid, activity);
        return new VoidResultResponse();
    }

    @DeleteMapping
    @Operation(summary = "Удаление группы правил")
    @ApiResponse(responseCode = "200", description = "Возвращается в случае успешного выполнения запроса")
    @ApiResponse(responseCode = "404", description = "Возвращается в случае, если правило не найдено")
    public void deleteAlertGroup(@RequestParam("group-uid") @Parameter(description = "UID группы правил") UUID groupUid) {
        alertGroupService.deleteAlertGroup(groupUid);
    }

    @PatchMapping
    @Operation(summary = "Обновление группы правил")
    @ApiResponse(responseCode = "200", description = "Возвращается в случае успешного выполнения запроса")
    @ApiResponse(responseCode = "400", description = "Возвращается в случае ошибки валидации запроса")
    @ApiResponse(responseCode = "404", description = "Возвращается в случае, если правило не найдено")
    public ResultResponse<AlertGroup> updateAlertRule(@RequestBody @Validated @Parameter(description = "Запрос на обновление")
                                                      UpdateAlertGroupRequest request) {
        return ResultResponse.of(alertGroupService.updateAlertGroup(request.getUid(), request.getGroupName(), request.getDescription()));
    }

    @PatchMapping("link-notification-rule")
    @Operation(summary = "Привязка правила оповещения к группе правил")
    @ApiResponse(responseCode = "200", description = "Возвращается в случае успешного выполнения запроса")
    @ApiResponse(responseCode = "404", description = "Возвращается в случае, если правило не найдено")
    public void linkNotificationRuleToAlertRule(@RequestParam("group-uid") @Parameter(description = "UID группы правил") UUID ruleUid,
                                                @RequestParam("notification-uid") @Parameter(description = "UID правила оповещений") UUID notificationUid) {
        alertGroupService.linkNotificationRuleToAlertGroup(ruleUid, notificationUid);
    }

    @PatchMapping("unlink-notification-rule")
    @Operation(summary = "Отвязка правила оповещения к группе правил")
    @ApiResponse(responseCode = "200", description = "Возвращается в случае успешного выполнения запроса")
    @ApiResponse(responseCode = "404", description = "Возвращается в случае, если правило не найдено")
    public void unlinkNotificationRuleFromAlertRule(@RequestParam("group-uid") @Parameter(description = "UID группы правил") UUID ruleUid,
                                                    @RequestParam("notification-uid") @Parameter(description = "UID правила оповещений") UUID notificationUid) {
        alertGroupService.unlinkNotificationRuleFromAlertGroup(ruleUid, notificationUid);
    }

    @PatchMapping("link-alert-rule")
    @Operation(summary = "Привязка правила к группе правил")
    @ApiResponse(responseCode = "200", description = "Возвращается в случае успешного выполнения запроса")
    @ApiResponse(responseCode = "404", description = "Возвращается в случае, если правило не найдено")
    public void linkAlertRuleToAlertRule(@RequestParam("group-uid") @Parameter(description = "UID группы правил") UUID groupUid,
                                         @RequestParam("ruleUid") @Parameter(description = "UID правила событий") UUID ruleUid) {
        alertGroupService.linkAlertRuleToAlertGroup(groupUid, ruleUid);
    }

    @PatchMapping("unlink-alert-rule")
    @Operation(summary = "Отвязка правила от группы правил")
    @ApiResponse(responseCode = "200", description = "Возвращается в случае успешного выполнения запроса")
    @ApiResponse(responseCode = "404", description = "Возвращается в случае, если правило не найдено")
    public void unlinkAlertRuleFromAlertRule(@RequestParam("group-uid") @Parameter(description = "UID группы правил") UUID groupUid,
                                             @RequestParam("rule-uid") @Parameter(description = "UID правила событий") UUID rule) {
        alertGroupService.unlinkAlertRuleFromAlertGroup(groupUid, rule);
    }
}
