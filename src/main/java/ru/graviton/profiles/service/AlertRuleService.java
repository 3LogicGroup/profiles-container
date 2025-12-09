package ru.graviton.profiles.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.graviton.profiles.dao.entity.AlertGroupEntity;
import ru.graviton.profiles.dao.entity.AlertRuleEntity;
import ru.graviton.profiles.dao.entity.NotificationRuleEntity;
import ru.graviton.profiles.dao.repository.AlertGroupRepository;
import ru.graviton.profiles.dao.repository.AlertRuleRepository;
import ru.graviton.profiles.dao.repository.NotificationRuleRepository;
import ru.graviton.profiles.dto.AlertRule;
import ru.graviton.profiles.dto.AlertRuleType;
import ru.graviton.profiles.dto.Severity;
import ru.graviton.profiles.exceptions.ErrorCode;
import ru.graviton.profiles.mapper.AlertRuleMapper;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AlertRuleService {

    private final AlertRuleMapper alertRuleMapper;

    private final AlertRuleRepository alertRuleRepository;
    private final AlertGroupRepository alertGroupRepository;
    private final NotificationRuleRepository notificationRuleRepository;

    @Transactional
    public AlertRule createAlertRule(Set<UUID> alertGroupUid, String name, String description, String expression, Boolean isProQL, Severity severity,
                                     AlertRuleType alertRuleType, Set<UUID> notificationRuleUids,Set<UUID> superiorRulesUids, boolean isMasterRule) {
        //todo: validate expression
        List<AlertGroupEntity> alertGroupEntities = Optional.ofNullable(alertGroupUid)
                .map(alertGroupRepository::findAllById)
                .orElse(null);
        Set<NotificationRuleEntity> notificationRuleEntities = Optional.ofNullable(notificationRuleUids)
                .map(notificationRuleRepository::findAllById)
                .map(HashSet::new)
                .orElse(null);
        Set<AlertRuleEntity> superiorRules = Optional.ofNullable(superiorRulesUids)
                .map(alertRuleRepository::findAllById)
                .map(HashSet::new)
                .orElse(new HashSet<>());
        AlertRuleEntity alertRule = AlertRuleEntity.builder()
                .ruleName(name)
                .description(description)
                .expression(expression)
                .isPrometheusExpression(isProQL)
                .severity(severity)
                .alertRuleType(Optional.ofNullable(alertRuleType).orElse(AlertRuleType.OTHER))
                .notificationRules(notificationRuleEntities)
                .enabled(true)
                .superiorRules(superiorRules)
                .isMasterRule(isMasterRule)
                .build();
        if (!CollectionUtils.isEmpty(alertGroupEntities)) {
            alertGroupEntities.forEach(alertGroupEntity -> {
                if (alertGroupEntity.getRules() == null) {
                    alertGroupEntity.setRules(new HashSet<>());
                }
                alertGroupEntity.getRules().add(alertRule);
                if (alertRule.getAlertGroups() == null) {
                    alertRule.setAlertGroups(new HashSet<>());
                }
                alertRule.getAlertGroups().add(alertGroupEntity);
            });
        }
        return alertRuleMapper.toDto(alertRuleRepository.save(alertRule));
    }

    //    @Cacheable(value = CacheConfiguration.ALERT_RULES)
    @Transactional
    public Page<AlertRule> getAllAlertRules(String name, Boolean enabled, Integer page, Integer size) {
        return alertRuleRepository.getActiveAlertRules(enabled, name, PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "ruleName")))
                .map(alertRuleMapper::toDto);
    }

    @Transactional
    public List<AlertRule> getAllAlertRules() {
        return alertRuleRepository.findAll()
                .stream()
                .map(alertRuleMapper::toDto)
                .toList();
    }

    @Transactional
    public List<AlertRule> getAllAlertRulesByNotificationRuleUid(UUID notificationRuleUid) {
        return alertRuleRepository.getActiveAlertRules(notificationRuleUid)
                .stream().map(alertRuleMapper::toDto).toList();
    }

    public List<AlertRule> getAllAlertRulesInternal() {
        return new ArrayList<>(alertRuleRepository.getActiveAlertRules().stream().map(alertRuleMapper::toDto).toList());
    }

    @Transactional
    public AlertRule getActiveAlertRuleByUid(UUID uuid, boolean withException) {
        Optional<AlertRule> alertRule = alertRuleRepository.findById(uuid)
                .map(alertRuleMapper::toDto);
        if (withException) {
            return alertRule.orElseThrow(ErrorCode.ALERT_RULE_NOT_FOUND::generateException);
        } else {
            return alertRule.orElse(null);
        }

    }

    private AlertRuleEntity getAlertRuleEntityByUid(UUID uid) {
        return alertRuleRepository.findById(uid)
                .orElseThrow(ErrorCode.ALERT_RULE_NOT_FOUND::generateException);
    }

    @Transactional
    public void changeActivityAlertRule(UUID uid, Boolean active) {
        AlertRuleEntity alertRuleEntity = alertRuleRepository.findById(uid)
                .orElseThrow(ErrorCode.ALERT_RULE_NOT_FOUND::generateException);
        alertRuleEntity.setEnabled(active);
    }

    @Transactional
    public AlertRule updateAlertRule(UUID uid, Set<UUID> alertGroupUids, String name, String description, String expression, Boolean isPromQL,
                                     Severity severity, AlertRuleType alertRuleType,
                                     Set<UUID> superiorRulesUids, boolean isMasterRule) {
        if (superiorRulesUids.contains(uid)) {
            throw ErrorCode.ALERT_RULE_REQUEST_ERROR.generateException("Текущее правило нельзя сделать вышестоящим");
        }
        AlertRuleEntity alertRuleEntityByUid = getAlertRuleEntityByUid(uid);
        alertRuleEntityByUid.getSuperiorRules().clear();
        if (!superiorRulesUids.isEmpty()) {
            Set<AlertRuleEntity> superiorRules = Optional.of(superiorRulesUids)
                    .map(alertRuleRepository::findAllById)
                    .map(HashSet::new)
                    .orElse(new HashSet<>());
            alertRuleEntityByUid.getSuperiorRules().addAll(superiorRules);
        }
        alertRuleEntityByUid.setRuleName(name);
        alertRuleEntityByUid.setDescription(description);
        alertRuleEntityByUid.setExpression(expression);
        alertRuleEntityByUid.setSeverity(severity);
        alertRuleEntityByUid.setIsPrometheusExpression(isPromQL);
        alertRuleEntityByUid.setAlertRuleType(Optional.ofNullable(alertRuleType).orElse(AlertRuleType.OTHER));
        alertRuleEntityByUid.setIsMasterRule(isMasterRule);
        if (CollectionUtils.isNotEmpty(alertGroupUids)) {
            Set<AlertGroupEntity> cleared = alertRuleEntityByUid.getAlertGroups().stream()
                    .filter(f -> !alertGroupUids.contains(f.getUid()))
                    .collect(Collectors.toSet());
            cleared.forEach(alertGroupEntity -> alertGroupEntity.getRules().remove(alertRuleEntityByUid));
            alertRuleEntityByUid.getAlertGroups().removeAll(cleared);
            List<AlertGroupEntity> alertGroupEntities = alertGroupRepository.findAllById(alertGroupUids);
            alertGroupEntities.forEach(alertGroupEntity -> {
                if (alertGroupEntity.getRules() == null) {
                    alertGroupEntity.setRules(new HashSet<>());
                }
                alertGroupEntity.getRules().add(alertRuleEntityByUid);
                if (alertRuleEntityByUid.getAlertGroups() == null) {
                    alertRuleEntityByUid.setAlertGroups(new HashSet<>());
                }
                alertRuleEntityByUid.getAlertGroups().add(alertGroupEntity);
            });
        } else {
            alertRuleEntityByUid.getAlertGroups().forEach(a -> a.getRules().remove(alertRuleEntityByUid));
            alertRuleEntityByUid.getAlertGroups().clear();
        }
        return alertRuleMapper.toDto(alertRuleRepository.save(alertRuleEntityByUid));
    }

    @Transactional
    public void deleteAlertRule(UUID uid) {
        alertRuleRepository.deleteById(uid);
    }

    @Transactional
    public void linkNotificationRuleToAlertRule(UUID alertRuleUid, UUID notificationRuleUid) {
        NotificationRuleEntity notificationRuleEntity = notificationRuleRepository.findById(notificationRuleUid)
                .orElseThrow(ErrorCode.NOTIFICATION_RULE_NOT_FOUND::generateException);
        AlertRuleEntity alertRuleEntityByUid = getAlertRuleEntityByUid(alertRuleUid);
        alertRuleEntityByUid.getNotificationRules().add(notificationRuleEntity);
    }

    @Transactional
    public void unlinkNotificationRuleFromAlertRule(UUID alertRuleUid, UUID notificationRuleUid) {
        NotificationRuleEntity notificationRuleEntity = notificationRuleRepository.findById(notificationRuleUid)
                .orElseThrow(ErrorCode.NOTIFICATION_RULE_NOT_FOUND::generateException);
        AlertRuleEntity alertRuleEntityByUid = getAlertRuleEntityByUid(alertRuleUid);
        alertRuleEntityByUid.getNotificationRules().remove(notificationRuleEntity);
    }
}
