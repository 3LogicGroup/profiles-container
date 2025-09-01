package ru.graviton.profiles.mapper;

import org.mapstruct.Mapper;
import ru.graviton.profiles.dao.entity.AlertRuleEntity;
import ru.graviton.profiles.dto.AlertRule;
import ru.graviton.profiles.dto.AlertRuleLight;

/**
 * Маппер для AlertRule
 */
@Mapper(componentModel = "spring", uses = NotificationRuleMapper.class)
public interface AlertRuleMapper {

    AlertRule toDto(AlertRuleEntity entity);

}
