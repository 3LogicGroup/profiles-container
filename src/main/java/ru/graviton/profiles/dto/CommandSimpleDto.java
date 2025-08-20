package ru.graviton.profiles.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.UUID;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@Schema(description = "Полная информация о команде")
public class CommandSimpleDto {

    @Schema(description = "Идентификатор команды")
    private UUID uid;

    @Schema(description = "Имя команды")
    @NotBlank
    private String commandName;

    @Schema(description = "Описание")
    private String description;

    @Schema(description = "Активна ли команда в настоящее время")
    @Builder.Default
    private Boolean isActive = true;

    @Schema(description = "Профиль оборудования с которым связана команда")
    @NotBlank
    private UUID profileUid;

    private String serviceName;

    private String rusName;

    private CommandType commandType;

}
