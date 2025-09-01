package ru.graviton.profiles.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;
import java.util.UUID;

@Getter
@Setter
public class CreateNotificationRuleRequest extends NotificationRuleRequest {

    @Schema(description = "UID правил для эскалации")
    private Set<UUID> escalationRules;


}
