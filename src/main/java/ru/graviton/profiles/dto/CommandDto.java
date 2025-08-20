package ru.graviton.profiles.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@Schema(description = "Полная информация о команде")
public class CommandDto extends CommandSimpleDto {

    @Schema(description = "Тело команды")
    @NotBlank
    private ActionDto body;

}
