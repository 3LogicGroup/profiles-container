package ru.graviton.profiles.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import ru.graviton.profiles.dto.AlertRuleType;
import ru.graviton.profiles.dto.Severity;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
public abstract class AlertRuleRequest {
    @NotBlank
    @Schema(description = "Название правила", example = "Правило")
    private String name;

    @Schema(description = "UID групп правил")
    private Set<UUID> alertGroupUids;

    @Schema(description = "Описание правила", example = "Описание")
    private String description;

    @NotBlank
    @Schema(description = "Выражение", example = "temperatures>$me.upperThresholdCritical")
    private String expression;

    @Schema(description = "Выражение является выражением PromQL")
    private Boolean isPromQLExpression;

    @NotNull
    @Schema(description = "Критичность", example = "CRITICAL")
    private Severity severity;

    @Schema(description = "Тип правила", example = "OTHER")
    private AlertRuleType alertRuleType;

    private Set<UUID> superiorRules = new HashSet<>();

    private boolean isMasterRule = false;
}
