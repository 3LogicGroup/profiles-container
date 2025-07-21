package ru.graviton.profiles.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProfileBaseDto {
    @Schema(description = "Идентификатор профиля", example = "443d6056-6970-4d46-82d2-d5c02e3b8ad8")
    private UUID uid;

    @Schema(description = "Имя загружаемого профиля", example = "taiga")
    private String profileName;

    @Schema(description = "Название продукта, для которого создается профиль", example = "taiga")
    private String productName;

    @Schema(description = "Модель", example = "taiga2132T")
    private String model;

    @Schema(description = "Название продукта, для которого создается профиль (Русский)", example = "taiga")
    private String productNameRus;

    @Schema(description = "is_active")
    private boolean active;

    @Schema(description = "Тип устройства")
    private DeviceType deviceType;
}
