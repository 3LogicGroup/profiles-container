package ru.graviton.profiles.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import ru.graviton.profiles.dao.entity.AlertRuleEntity;
import ru.graviton.profiles.dao.entity.NotificationRuleEntity;
import ru.graviton.profiles.dto.NotificationRule;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface NotificationRuleMapper {

    @Mapping(target = "alertRules", ignore = true)
    NotificationRuleEntity toEntity(NotificationRule dto);

    @Mapping(target = "alertRulesUids", source = "alertRules", qualifiedByName = "toUids")
    NotificationRule toDto(NotificationRuleEntity entity);

    @Named("toUids")
    static Set<UUID> toUids(Set<AlertRuleEntity> alertRules) {
        if (alertRules == null) {
            return new HashSet<>();
        }
        return alertRules.stream()
                .map(AlertRuleEntity::getUid)
                .collect(Collectors.toSet());
    }
}
