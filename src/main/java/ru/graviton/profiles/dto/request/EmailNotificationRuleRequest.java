package ru.graviton.profiles.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class EmailNotificationRuleRequest {
    @NotEmpty
    @Schema(description = "Получатели писем")
    private List<String> sendTo;
}