package ru.graviton.profiles.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.graviton.profiles.dao.entity.ClientEntity;
import ru.graviton.profiles.dao.repository.ClientRepository;
import ru.graviton.profiles.dto.ClientDto;
import ru.graviton.profiles.mapper.ClientMapper;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class ClientService {
    private final ClientRepository clientRepository;

    private final ClientMapper clientMapper;

    public ClientDto getClient(UUID uuid) {
        return clientRepository.findById(uuid)
                .map(clientMapper::toDto)
                .orElse(null);
    }

    public void addClient(ClientDto clientDto) {
        ClientEntity clientEntity = clientRepository.findById(clientDto.getUid())
                .orElse(null);
        if (clientEntity == null) {
            clientEntity = clientMapper.toEntity(clientDto);
        } else {
            clientEntity.setCompanyName(clientDto.getCompanyName());
            clientEntity.setPublicKey(clientDto.getPublicKey());
        }
        clientRepository.save(clientEntity);
    }
}


