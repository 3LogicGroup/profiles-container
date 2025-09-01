package ru.graviton.profiles.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class UpdateAlertGroupRequest extends AlertGroupRequest {
    @NotNull
    @Schema(description = "UID группы правил", example = "5946bc69-b0aa-4b7b-bf39-81e44cc50863")
    private UUID uid;
}
