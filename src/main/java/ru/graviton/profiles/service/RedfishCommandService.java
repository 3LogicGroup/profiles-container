package ru.graviton.profiles.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import ru.graviton.profiles.dao.entity.CommandEntity;
import ru.graviton.profiles.dao.repository.CommandRepository;
import ru.graviton.profiles.dto.*;
import ru.graviton.profiles.dto.response.GenerateCommandResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class RedfishCommandService {

    private final CommandRepository commandRepository;

    private final CommandGeneratorService commandGeneratorService;
    private final GetAllRedfishService getAllRedfishService;
    private final ProfileService profileService;

    public Map<String, Object> getAllRedfish(String url, String login, String password) {
        return getAllRedfishService.fetchActions(url, login, password);
    }

    public GenerateCommandResult generateRedfishCommand(UUID profileUid, String url, String login, String password) {
        ProfileDataExt<AbstractApiProfileData<?>> profileByProtocol = profileService.getProfileByProtocol(Protocol.REDFISH, profileUid);
        RedfishApiProfile data = (RedfishApiProfile) profileByProtocol.getData();
        if (StringUtils.isEmpty(login)) {
            login = data.getLogin();
        }
        if (StringUtils.isEmpty(password)) {
            password = data.getPassword();
        }
        List<ActionDto> mappedActions = commandGeneratorService.fetchActions(url, login, password);
        List<CommandEntity> commands = new ArrayList<>(mappedActions.stream()
                .map(x -> {
                    CommandEntity commandEntity = new CommandEntity();
                    commandEntity.setBody(x);
                    commandEntity.setIsActive(true);
                    commandEntity.setTarget(x.getTarget());
                    commandEntity.setServiceName(x.getService());
                    commandEntity.setCommandName(x.getName());
                    commandEntity.setCommandType(CommandType.REDFISH);
                    commandEntity.setDescription(x.getDescription());
                    commandEntity.setProfileUid(profileUid);
                    return commandEntity;
                })
                .toList());

        List<CommandEntity> allCommandsByProfileUid = commandRepository.findAllCommandsByProfileUid(profileUid);

        commands.removeIf(commandEntity -> allCommandsByProfileUid.stream()
                .filter(f -> f.getCommandName().equals(commandEntity.getCommandName()))
                .filter(f -> f.getCommandType().equals(commandEntity.getCommandType()))
                .anyMatch(f -> f.getProfileUid().equals(commandEntity.getProfileUid())));

        List<CommandEntity> commandEntities = commandRepository.saveAll(commands);
        GenerateCommandResult generateCommandResult = new GenerateCommandResult();
        generateCommandResult.setCount(commandEntities.size());
        generateCommandResult.setCommandNames(commandEntities.stream()
                .map(CommandEntity::getCommandName)
                .toList());
        return generateCommandResult;
    }

}