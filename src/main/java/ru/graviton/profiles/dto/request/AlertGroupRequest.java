package ru.graviton.profiles.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class AlertGroupRequest {
    @Schema(description = "Название группы правил", example = "Название группы правил")
    @NotBlank
    private String groupName;

    @Schema(description = "Описание группы правил", example = "описание")
    private String description;
}
