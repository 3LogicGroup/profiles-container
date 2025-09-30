package ru.graviton.profiles.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import ru.graviton.profiles.dao.entity.ClientEntity;
import ru.graviton.profiles.dao.entity.LicenseEntity;
import ru.graviton.profiles.dto.ClientDto;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface ClientMapper {

    @Mapping(target = "licenses", source = "licenses", qualifiedByName = "licenses")
    ClientDto toDto(ClientEntity profile);

    @Named("licenses")
    default Set<String> licenses(Collection<LicenseEntity> licenses) {
        return licenses.stream()
                .map(LicenseEntity::getLicenseKey)
                .collect(Collectors.toSet());
    }

    @Mapping(target = "licenses", ignore = true)
    ClientEntity toEntity(ClientDto clientDto);
}
