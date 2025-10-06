package ru.graviton.profiles.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.graviton.profiles.dao.entity.ClientEntity;
import ru.graviton.profiles.dao.repository.ClientRepository;
import ru.graviton.profiles.dto.ClientDto;
import ru.graviton.profiles.mapper.ClientMapper;

@Service
@RequiredArgsConstructor
@Transactional
public class ClientService {
    private final ClientRepository clientRepository;

    private final ClientMapper clientMapper;

    public ClientDto getClient(String email) {
        return clientRepository.findByEmail(email)
                .map(clientMapper::toDto)
                .orElse(null);
    }

    public ClientEntity saveClient(ClientDto client) {
        ClientEntity clientEntity = clientRepository.findByEmail(client.getEmail())
                .orElse(null);
        if (clientEntity == null) {
            clientEntity = clientMapper.toEntity(client);
        } else {
            clientEntity.setCompanyName(client.getCompanyName());
            clientEntity.setEmail(client.getEmail());
        }
        return clientEntity;
    }

    public ClientDto addClient(ClientDto clientDto) {
        ClientEntity clientEntity = saveClient(clientDto);
        return clientMapper.toDto(clientRepository.save(clientEntity));
    }
}


