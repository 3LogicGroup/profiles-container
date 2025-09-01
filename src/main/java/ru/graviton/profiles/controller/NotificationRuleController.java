package ru.graviton.profiles.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.graviton.profiles.dto.*;
import ru.graviton.profiles.dto.request.CreateNotificationRuleRequest;
import ru.graviton.profiles.dto.request.EmailNotificationRuleRequest;
import ru.graviton.profiles.dto.request.NotificationRuleRequest;
import ru.graviton.profiles.dto.request.UpdateNotificationRuleRequest;
import ru.graviton.profiles.dto.response.ResultResponse;
import ru.graviton.profiles.dto.response.VoidResultResponse;
import ru.graviton.profiles.exceptions.ErrorCode;
import ru.graviton.profiles.service.NotificationRuleService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/notification-rule")
@RequiredArgsConstructor
@Tag(name = "Контроллер для работы c правилами оповещений")
public class NotificationRuleController {

    private final NotificationRuleService notificationRuleService;

    @PostMapping
    @Operation(summary = "Создание правила")
    @ApiResponse(responseCode = "200", description = "Возвращается в случае успешного выполнения запроса")
    @ApiResponse(responseCode = "400", description = "Возвращается в случае ошибки валидации запроса")
    public ResultResponse<NotificationRule> createNotificationRule(@RequestBody @Validated CreateNotificationRuleRequest request) {
        validateCreateNotificationRuleRequest(request);
        return ResultResponse.of(notificationRuleService.createNotificationRule(request.getName(), request.getDescription(),
                request.getType(), request.getInitialDelay(), request.getAlertDelay(), request.getFireMessageTemplate(),
                request.getResolveMessageTemplate(), getNotificationRule(request.getType(), request.getEmailNotificationRule()),
                request.getEscalationRules()));
    }

    @PatchMapping
    @Operation(summary = "Обновление правила")
    @ApiResponse(responseCode = "200", description = "Возвращается в случае успешного выполнения запроса")
    @ApiResponse(responseCode = "400", description = "Возвращается в случае ошибки валидации запроса")
    @ApiResponse(responseCode = "404", description = "Возвращается в случае, если правило не найдено")
    public ResultResponse<NotificationRule> updateNotificationRule(@RequestBody @Validated @Parameter(description = "Запрос на обновление")
                                                                   UpdateNotificationRuleRequest request) {
        return ResultResponse.of(notificationRuleService.updateNotificationRule(request.getUuid(), request.getName(), request.getDescription(),
                getNotificationRule(request.getType(), request.getEmailNotificationRule()), request.getInitialDelay(), request.getAlertDelay()));
    }

    @PatchMapping("change-activity/{rule-uid}")
    @Operation(summary = "Изменение активности правила")
    @ApiResponse(responseCode = "200", description = "Возвращается в случае успешного выполнения запроса")
    @ApiResponse(responseCode = "404", description = "Возвращается в случае, если правило не найдено")
    public VoidResultResponse changeActivityNotificationRule(@PathVariable("rule-uid") @Parameter(description = "UID правила") UUID ruleUid,
                                                             @RequestParam("activity") @Parameter(description = "флаг активности") Boolean activity) {
        notificationRuleService.changeActivityNotificationRule(ruleUid, activity);
        return new VoidResultResponse();
    }

    @DeleteMapping
    @Operation(summary = "Удаление правила")
    @ApiResponse(responseCode = "200", description = "Возвращается в случае успешного выполнения запроса")
    @ApiResponse(responseCode = "404", description = "Возвращается в случае, если правило не найдено")
    public void deleteNotificationRule(@RequestParam("rule-uid") @Parameter(description = "UID правила") UUID ruleUid) {
        notificationRuleService.deleteNotificationRule(ruleUid);
    }

    @GetMapping("list")
    @Operation(summary = "Получение всех правил")
    @ApiResponse(responseCode = "200", description = "Возвращается в случае успешного выполнения запроса")
    public ResultResponse<List<NotificationRule>> getAllNotificationRules() {
        return ResultResponse.of(notificationRuleService.getNotificationRules());
    }
    @GetMapping("all")
    @Operation(summary = "Получение всех правил")
    @ApiResponse(responseCode = "200", description = "Возвращается в случае успешного выполнения запроса")
    public ResultResponse<Page<NotificationRule>> getAllNotificationRules(@RequestParam(value = "name", required = false) String name,
                                                                          @RequestParam(value = "enabled", required = false) Boolean enabled,
                                                                          @RequestParam(value = "page", required = false, defaultValue = "0") Integer page,
                                                                          @RequestParam(value = "size", required = false, defaultValue = "10") Integer size) {
        return ResultResponse.of(notificationRuleService.getNotificationRules(name, enabled, page, size));
    }


    @GetMapping("by-rule/{rule-uid}")
    @Operation(summary = "Получение правил оповещения по правилу событий")
    @ApiResponse(responseCode = "200", description = "Возвращается в случае успешного выполнения запроса")
    public ResultResponse<List<NotificationRule>> getNotificationRulesByRuleUid(@PathVariable("rule-uid") @Parameter(description = "UID правила событий") UUID ruleUid) {
        return ResultResponse.of(notificationRuleService.getNotificationRulesByAlertRuleUid(ruleUid));
    }

    @GetMapping("{rule-uid}")
    @Operation(summary = "Получение правила по UID")
    @ApiResponse(responseCode = "200", description = "Возвращается в случае успешного выполнения запроса")
    @ApiResponse(responseCode = "404", description = "Возвращается в случае, если правило не найдено")
    public ResultResponse<NotificationRule> getNotificationRule(@PathVariable("rule-uid") @Parameter(description = "UID правила") UUID ruleUid) {
        return ResultResponse.of(notificationRuleService.getActiveNotificationRule(ruleUid));
    }


    private AbstractNotificationRule getNotificationRule(NotificationType notificationType, EmailNotificationRuleRequest emailNotificationRuleRequest) {
        if (notificationType == NotificationType.EMAIL && emailNotificationRuleRequest != null) {
            return EmailNotificationRule.builder()
                    .sendTo(emailNotificationRuleRequest.getSendTo())
                    .build();
        } else if (notificationType == NotificationType.TELEGRAM) {
            return TelegramNotificationRule.builder().build();
        } else {
            return new UiNotificationRule();
        }
    }

    private void validateCreateNotificationRuleRequest(NotificationRuleRequest request) {
        if (request.getType() == NotificationType.EMAIL && request.getEmailNotificationRule() == null) {
            throw ErrorCode.CREATE_NOTIFICATION_RULE_REQUEST_NOT_VALID.generateException("Для типа EMAIL необходимо указать адресатов");
        }
    }

}
