package ru.graviton.profiles.mapper;

import org.mapstruct.Mapper;
import ru.graviton.profiles.dao.entity.CommandEntity;
import ru.graviton.profiles.dto.CommandDto;
import ru.graviton.profiles.dto.CommandSimpleDto;


@Mapper(componentModel = "spring")
public interface CommandMapper {

    CommandDto toDto(CommandEntity commandEntity);

    CommandSimpleDto toSimpleDto(CommandEntity commandEntity);

}
