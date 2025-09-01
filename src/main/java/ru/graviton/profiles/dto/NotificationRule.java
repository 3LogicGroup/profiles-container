package ru.graviton.profiles.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@EqualsAndHashCode(of = "uid")
public class NotificationRule {
    @Schema(description = "UID правила", example = "aaa09ba3-ed48-4044-bde3-5a58e4cd636d")
    private UUID uid;


    @Schema(description = "Название правила", example = "Название правила")
    private String name;

    @Schema(description = "Описание правила", example = "описание")
    private String description;

    @Schema(description = "Тип правила", example = "TELEGRAM")
    private NotificationType type;

    @Schema(description = "Задержка перед первой отправкой уведомления (в секундах)", example = "20")
    private Long initialDelay;

    @Schema(description = "Задержка перед повторной отправкой уведомления (в секундах)", example = "20")
    private Long alertDelay;

    @Schema(description = "Шаблон сообщения о начале события", example = """
            ‼️ ВНИМАНИЕ!
            На следующих устройствах температура превысила допустимое значение:
            {#each by grouping labels.instanceName} Название устройства: <b>{{element}}</b>
               {#each} {{labels.name}}:{{value}}{/each by grouping}
            """)
    private String fireMessageTemplate;

    @Schema(description = "Шаблон сообщения об окончании события", example = """
            ‼️ ВНИМАНИЕ!
            На следующих устройствах температура пришла в норму:
            {#each by grouping labels.instanceName} Название устройства: <b>{{element}}</b>
               {#each} {{labels.name}}:{{value}}{/each by grouping}
            """)
    private String resolveMessageTemplate;

    @Schema(description = "Описание правил отправки сообщения")
    private AbstractNotificationRule notificationRule;

    @Schema(description = "Правила эскалации", example = "aaa09ba3-ed48-4044-bde3-5a58e4cd636d")
    private Set<NotificationRule> escalationRules;

    @Schema(description = "Флаг активности", example = "true")
    private boolean enabled;

    @Schema(description = "Идентификаторы правил")
    private Set<UUID> alertRulesUids;

    public Long getInitialDelay() {
        return Optional.ofNullable(initialDelay).orElse(alertDelay);
    }
}
