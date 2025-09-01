package ru.graviton.profiles.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import ru.graviton.profiles.dto.NotificationType;

@Getter
@Setter
public abstract class NotificationRuleRequest {
    @NotBlank
    @Schema(description = "Название правила", example = "Правило")
    private String name;

    @Schema(description = "Описание правила", example = "Описание")
    private String description;

    @Schema(description = "Тип правила", example = "EMAIL")
    private NotificationType type = NotificationType.UI;

    @Valid
    @Schema(description = "Подзапрос для создания правила оповещения по EMAIL")
    private EmailNotificationRuleRequest emailNotificationRule;

    @Schema(description = "Шаблон сообщения о начале события", example = """
            ‼️ ВНИМАНИЕ!
            На следующих устройствах температура превысила допустимое значение:
            {#each by grouping labels.instanceName} Название устройства: <b>{{element}}</b>
               {#each} {{labels.name}}:{{value}}{/each by grouping}
            """)
    @NotBlank
    private String fireMessageTemplate;

    @Schema(description = "Шаблон сообщения об окончании события", example = """
            ‼️ ВНИМАНИЕ!
            На следующих устройствах температура пришла в норму:
            {#each by grouping labels.instanceName} Название устройства: <b>{{element}}</b>
               {#each} {{labels.name}}:{{value}}{/each by grouping}
            """)
    @NotBlank
    private String resolveMessageTemplate;

    @Schema(description = "Задержка перед первой отправкой уведомления (в секундах)", example = "20")
    private Integer initialDelay;

    @Schema(description = "Задержка перед повторной отправкой уведомления (в секундах)", example = "20")
    private Integer alertDelay;

}
