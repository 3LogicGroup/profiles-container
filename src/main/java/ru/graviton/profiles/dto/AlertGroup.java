package ru.graviton.profiles.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class AlertGroup {

    @Schema(description = "UID группы правил", example = "aaa09ba3-ed48-4044-bde3-5a58e4cd636d")
    private UUID uid;

    @Schema(description = "Флаг активности", example = "true")
    private boolean enabled;

    @Schema(description = "Название группы правил", example = "Название группы правил")
    private String groupName;

    @Schema(description = "Описание группы правил", example = "описание")
    private String description;

    @Schema(description = "Правила, содержащиеся в группе")
    private List<AlertRule> alertRules = new ArrayList<>();

    @Schema(description = "Правила уведомлений")
    private List<NotificationRule> notificationRules = new ArrayList<>();

    private LocalDateTime dateUpdate;

    private LocalDateTime dateCreate;
}
