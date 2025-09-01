package ru.graviton.profiles.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@EqualsAndHashCode(of = "uid")
public class AlertRule {

    @Schema(description = "UID правила", example = "aaa09ba3-ed48-4044-bde3-5a58e4cd636d")
    private UUID uid;

    @Schema(description = "Название правила", example = "Название правила")
    private String ruleName;

    @Schema(description = "Описание правила", example = "описание")
    private String description;

    @Schema(description = "Флаг активности", example = "true")
    private boolean enabled;

    @Schema(description = "Выражение", example = "temperatures>$me.upperThresholdCritical")
    private String expression;

    @Schema(description = "Выражение на языке PromQL", example = "false")
    private Boolean isPrometheusExpression;

    @Schema(description = "Критичность", example = "CRITICAL")
    private Severity severity = Severity.INFO;

    private AlertRuleType alertRuleType;

    @Schema(description = "Правила уведомлений")
    private List<NotificationRule> notificationRules = new ArrayList<>();

    private Set<UUID> targetUids;
}
