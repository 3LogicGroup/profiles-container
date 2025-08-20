package ru.graviton.profiles.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.graviton.profiles.dto.CommandDto;
import ru.graviton.profiles.dto.request.LoadCommandsRequest;
import ru.graviton.profiles.dto.response.GenerateCommandResult;
import ru.graviton.profiles.dto.response.ResultResponse;
import ru.graviton.profiles.service.CommandAdminService;
import ru.graviton.profiles.service.RedfishCommandService;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("redfish-commands")
@RequiredArgsConstructor
public class RedfishCommandsController {

    private final RedfishCommandService redfishCommandService;

    private final CommandAdminService commandAdminService;

    @PostMapping("load/{profile-uid}")
    public GenerateCommandResult loadCommands(@PathVariable("profile-uid") UUID profileUid, @RequestBody LoadCommandsRequest loadCommandsRequest) {
        return redfishCommandService.generateRedfishCommand(profileUid, loadCommandsRequest.getUrl(),
                loadCommandsRequest.getLogin(), loadCommandsRequest.getPassword());
    }

    @PostMapping("/get-all")
    public ResultResponse<Map<String, Object>> getAll(@RequestBody LoadCommandsRequest loadCommandsRequest) {
        return ResultResponse.of(redfishCommandService.getAllRedfish(loadCommandsRequest.getUrl(),
                loadCommandsRequest.getLogin(), loadCommandsRequest.getPassword()));
    }


    @GetMapping
    @Operation(summary = "Получение всех команд")
    @ApiResponse(responseCode = "200", description = "Возвращается в случае успешного выполнения запроса")
    @ApiResponse(responseCode = "400", description = "Возвращается в случае если передан некорректный запрос",
            content = @Content(schema = @Schema()))
    public ResultResponse<List<CommandDto>> getCommands() {
        return ResultResponse.of(commandAdminService.getCommands());
    }

    @GetMapping("/commands-by-target/{profileUid}")
    @Operation(summary = "Получение команд профиля по profile uid")
    @ApiResponse(responseCode = "200", description = "Возвращается в случае успешного выполнения запроса")
    @ApiResponse(responseCode = "400", description = "Возвращается в случае если передан некорректный запрос",
            content = @Content(schema = @Schema()))
    public ResultResponse<List<CommandDto>> getCommandsByProfile(@PathVariable UUID profileUid) {
        return ResultResponse.of(commandAdminService.getCommandsByProfile(profileUid));
    }

    @GetMapping("/{commandUid}")
    @Operation(summary = "Получение команды")
    @ApiResponse(responseCode = "200", description = "Возвращается в случае успешного выполнения запроса")
    @ApiResponse(responseCode = "400", description = "Возвращается в случае если передан некорректный запрос",
            content = @Content(schema = @Schema()))
    public ResultResponse<CommandDto> getCommand(@PathVariable UUID commandUid) {
        return ResultResponse.of(commandAdminService.getCommand(commandUid));
    }

    @DeleteMapping("/{commandUid}")
    @Operation(summary = "Удаление команды")
    @ApiResponse(responseCode = "200", description = "Возвращается в случае успешного выполнения запроса")
    @ApiResponse(responseCode = "400", description = "Возвращается в случае если передан некорректный запрос",
            content = @Content(schema = @Schema()))
    public void removeCommand(
            @Schema(description = "Идентификатор команды")
            @PathVariable UUID commandUid) {
        commandAdminService.removeCommand(commandUid);
    }
}
