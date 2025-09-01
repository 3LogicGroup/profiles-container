package ru.graviton.profiles.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@EqualsAndHashCode(of = "uid")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AlertRuleLight {

    @Schema(description = "UID правила", example = "aaa09ba3-ed48-4044-bde3-5a58e4cd636d")
    private UUID uid;

    @Schema(description = "Название правила", example = "Название правила")
    private String ruleName;

    @Schema(description = "Критичность", example = "CRITICAL")
    @Builder.Default
    private Severity severity = Severity.INFO;

    private AlertRuleType alertRuleType;

}
