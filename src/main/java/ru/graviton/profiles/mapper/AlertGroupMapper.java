package ru.graviton.profiles.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.graviton.profiles.dao.entity.AlertGroupEntity;
import ru.graviton.profiles.dto.AlertGroup;


/**
 * Маппер для AlertRule
 */
@Mapper(componentModel = "spring", uses = NotificationRuleMapper.class)
public interface AlertGroupMapper {

    @Mapping(target = "alertRules", source = "rules")
    AlertGroup toDto(AlertGroupEntity entity);
}
