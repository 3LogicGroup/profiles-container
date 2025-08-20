package ru.graviton.profiles.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.graviton.profiles.dao.repository.CommandRepository;
import ru.graviton.profiles.dto.CommandDto;
import ru.graviton.profiles.exceptions.ErrorCode;
import ru.graviton.profiles.mapper.CommandMapper;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommandAdminService {

    private final CommandRepository commandRepository;

    private final CommandMapper commandMapper;

    @Transactional
    public List<CommandDto> getCommands() {
        return commandRepository.findAll().stream()
                .map(commandMapper::toDto).collect(Collectors.toList());
    }

    @Transactional
    public List<CommandDto> getCommandsByProfile(UUID profileUid) {
        return commandRepository.findAllCommandsByProfileUid(profileUid)
                .stream().map(commandMapper::toDto).toList();
    }

    @Transactional
    public CommandDto getCommand(UUID commandUid) {
        return commandRepository.findById(commandUid)
                .map(commandMapper::toDto)
                .orElseThrow(ErrorCode.COMMAND_NOT_FOUND::generateException);
    }

    @Transactional
    public void removeCommand(UUID commandUuid) {
        commandRepository.deleteById(commandUuid);
    }
}
