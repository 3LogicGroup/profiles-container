package ru.graviton.profiles.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

/**
 * DTO профиля
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Schema(description = "Профиль")
public class ProfileDto extends ProfileBaseDto {

    @Schema(description = "Содержимое профилей")
    private Map<Protocol, ? extends AbstractApiProfileData<?>> profileData;

}
