package ru.graviton.profiles.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;
import java.util.UUID;

@Getter
@Setter
public class CreateAlertGroupRequest extends AlertGroupRequest{
    @Schema(description = "Правила уведомлений")
    private Set<UUID> notificationRuleUids;

    @Schema(description = "Правила событий")
    private Set<UUID> alertRuleUids;
}
