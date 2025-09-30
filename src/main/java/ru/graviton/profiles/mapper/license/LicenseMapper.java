package ru.graviton.profiles.mapper.license;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.graviton.profiles.dao.entity.LicenseEntity;
import ru.graviton.profiles.dto.license.LicenseDto;

@Mapper(componentModel = "spring")
public interface LicenseMapper {

    @Mapping(target = "isExpired", ignore = true)
    @Mapping(target = "isActive", ignore = true)
    @Mapping(target = "currentActivations", ignore = true)
    LicenseDto toDto(LicenseEntity license);

}
