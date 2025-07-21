package ru.graviton.profiles.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import ru.graviton.profiles.dto.AbstractApiProfileData;
import ru.graviton.profiles.dto.ProfileDataExt;
import ru.graviton.profiles.dto.ProfileDto;
import ru.graviton.profiles.dto.Protocol;
import ru.graviton.profiles.dto.request.SaveProfileRequest;
import ru.graviton.profiles.dto.request.UpdateProfileRequest;
import ru.graviton.profiles.dto.response.ResultResponse;

import java.util.List;
import java.util.UUID;

/**
 * Контроллер для работы профилями
 */
@Tag(name = "Контроллер для работы c профилями")
public interface ProfileController {

    @GetMapping("/get-profiles")
    @Operation(summary = "Получение всех профилей")
    @ApiResponse(responseCode = "200", description = "Возвращается в случае успешного выполнения запроса")
    @ApiResponse(responseCode = "404", description = "Возвращается в случае если профили не найдены",
            content = @Content(schema = @Schema()))
    ResultResponse<List<ProfileDto>> getProfiles();

    @GetMapping("/get-profiles/{protocol}")
    @Operation(summary = "Получение всех профилей")
    @ApiResponse(responseCode = "200", description = "Возвращается в случае успешного выполнения запроса")
    @ApiResponse(responseCode = "404", description = "Возвращается в случае если профили не найдены",
            content = @Content(schema = @Schema()))
    ResultResponse<List<ProfileDataExt<AbstractApiProfileData<?>>>> getProfilesByProtocol(@PathVariable("protocol") Protocol protocol);


    @GetMapping(value = "get-profile/{profile-name}")
    @Operation(summary = "Получение профиля по его имени")
    @ApiResponse(responseCode = "200", description = "Возвращается в случае успешного выполнения запроса")
    @ApiResponse(responseCode = "404", description = "Возвращается в случае если профиль не найден",
            content = @Content(schema = @Schema()))
    ResultResponse<ProfileDto> getProfile(@PathVariable("profile-name") String profileName);

    @GetMapping(value = "get-profile-by-uid/{protocol}/{uuid}")
    @Operation(summary = "Получение профиля по UID и Протоколу")
    @ApiResponse(responseCode = "200", description = "Возвращается в случае успешного выполнения запроса")
    @ApiResponse(responseCode = "404", description = "Возвращается в случае если профили не найдены",
            content = @Content(schema = @Schema()))
    ResultResponse<ProfileDataExt<AbstractApiProfileData<?>>> getBaseProfile(@PathVariable("protocol") Protocol protocol, @PathVariable("uuid") UUID uuid);

    @GetMapping(value = "get-profile-by-uid/{uuid}")
    @Operation(summary = "Получение профиля по UID")
    @ApiResponse(responseCode = "200", description = "Возвращается в случае успешного выполнения запроса")
    @ApiResponse(responseCode = "404", description = "Возвращается в случае если профили не найдены",
            content = @Content(schema = @Schema()))
    ResultResponse<ProfileDto> getProfile(@PathVariable("uuid") UUID uuid);

    @PutMapping(value = "update-profile/{profileUid}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Обновление профиля")
    @ApiResponse(responseCode = "200", description = "Возвращается в случае успешного выполнения запроса")
    ResultResponse<ProfileDto> updateProfile(@PathVariable UUID profileUid, @RequestBody UpdateProfileRequest updateProfileRequest);

    @PutMapping("soft-delete/{profileUid}")
    @Operation(summary = "Мягкое удаление профиля")
    @ApiResponse(responseCode = "200", description = "Возвращается в случае успешного выполнения запроса")
    void softDeleteProfile(@PathVariable UUID profileUid);

    @PutMapping("soft-recovery/{profileUid}")
    @Operation(summary = "Восстановление профиля")
    @ApiResponse(responseCode = "200", description = "Возвращается в случае успешного выполнения запроса")
    void softRecoveryProfile(@PathVariable UUID profileUid);

    @PostMapping(value = "add-profile", consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Добавление профайла")
    @ApiResponse(responseCode = "200", description = "Возвращается в случае успешного выполнения запроса")
    ResultResponse<ProfileDto> addProfile(@RequestBody SaveProfileRequest saveProfileRequest);

    @DeleteMapping("/{uuid}")
    @Operation(summary = "Удаление профиля по UID")
    @ApiResponse(responseCode = "200", description = "Возвращается в случае успешного выполнения запроса")
    @ApiResponse(responseCode = "404", description = "Возвращается в случае если профиль не найден",
            content = @Content(schema = @Schema()))
    void deleteProfile(@PathVariable("uuid") UUID uuid);

    @GetMapping("/{protocol}/{profile-uid}/{target-uid}")
    @Operation(summary = "Проверка принадлежности устройств для указанного задания ")
    @ApiResponse(responseCode = "200", description = "Возвращается в случае успешного выполнения запроса")
    @ApiResponse(responseCode = "404", description = "Возвращается в случае если задание не найдено",
            content = @Content(schema = @Schema()))
    ResultResponse<Boolean> checkOwnerProfile(
            @PathVariable("protocol")
            Protocol protocol, @PathVariable("profile-uid")
            @Parameter(description = "UID профиля", example = "0155cca1-8cae-4327-9077-5703dc551ed2")
            UUID profileUid,
            @PathVariable("target-uid")
            @Parameter(description = "UID цели", example = "0155cca1-8cae-4327-9077-5703dc551ed1")
            UUID targetUid);
}
