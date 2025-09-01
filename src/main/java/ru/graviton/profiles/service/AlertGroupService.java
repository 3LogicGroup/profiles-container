package ru.graviton.profiles.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
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
import ru.graviton.profiles.dto.AlertGroup;
import ru.graviton.profiles.exceptions.ErrorCode;
import ru.graviton.profiles.mapper.AlertGroupMapper;

import java.util.*;

@Service
@RequiredArgsConstructor
public class AlertGroupService {
    private final AlertGroupRepository alertGroupRepository;
    private final AlertRuleRepository alertRuleRepository;
    private final NotificationRuleRepository notificationRuleRepository;
    private final AlertGroupMapper alertGroupMapper;

    @Transactional
    public AlertGroup createAlertGroup(String name, String description, Set<UUID> notificationRuleUids, Set<UUID> alertRuleUids) {

        AlertGroupEntity alertGroupEntity = new AlertGroupEntity();
        alertGroupEntity.setEnabled(true);
        alertGroupEntity.setGroupName(name);
        alertGroupEntity.setDescription(description);

        Set<NotificationRuleEntity> notificationRuleEntities = Optional.ofNullable(notificationRuleUids)
                .map(notificationRuleRepository::findAllById)
                .map(HashSet::new)
                .orElse(null);
        alertGroupEntity.setNotificationRules(notificationRuleEntities);
        Set<AlertRuleEntity> alertRuleEntities = Optional.ofNullable(alertRuleUids)
                .map(alertRuleRepository::findAllById)
                .map(HashSet::new)
                .orElse(null);
        if (alertRuleEntities != null) {
            alertRuleEntities.forEach(a -> {
                if (a.getAlertGroups() == null) {
                    a.setAlertGroups(new HashSet<>());
                }
                a.getAlertGroups().add(alertGroupEntity);
                alertGroupEntity.getRules().add(a);
            });
        }
        alertGroupEntity.setRules(alertRuleEntities);
        return alertGroupMapper.toDto(alertGroupRepository.save(alertGroupEntity));
    }


    @Transactional
    public List<AlertGroup> getAllAlertGroups() {
        return alertGroupRepository.findAll()
                .stream()
                .map(alertGroupMapper::toDto)
                .toList();
    }

    @Transactional
    public Page<AlertGroup> getAllAlertGroups(String name, Boolean enabled, Integer page, Integer size) {
        return alertGroupRepository.getActiveAlertGroups(enabled, name, PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "groupName")))
                .map(alertGroupMapper::toDto);
    }

    @Transactional
    public AlertGroup getActiveAlertGroupByUid(UUID uuid) {
        return alertGroupRepository.findActiveGroupByUid(uuid)
                .map(alertGroupMapper::toDto)
                .orElseThrow(ErrorCode.ALERT_GROUP_NOT_FOUND::generateException);
    }

    private AlertGroupEntity getAlertGroupEntityByUid(UUID uid) {
        return alertGroupRepository.findById(uid)
                .orElseThrow(ErrorCode.ALERT_GROUP_NOT_FOUND::generateException);
    }

    @Transactional
    public void changeActivityAlertGroup(UUID uid, Boolean active) {
        AlertGroupEntity alertGroupEntity = alertGroupRepository.findById(uid)
                .orElseThrow(ErrorCode.ALERT_GROUP_NOT_FOUND::generateException);
        alertGroupEntity.getRules()
                .forEach(r -> r.setEnabled(active));
        alertGroupEntity.setEnabled(active);
    }

    @Transactional
    public AlertGroup updateAlertGroup(UUID uid, String name, String description) {
        AlertGroupEntity alertGroupEntityByUid = getAlertGroupEntityByUid(uid);
        alertGroupEntityByUid.setGroupName(name);
        alertGroupEntityByUid.setDescription(description);

        return alertGroupMapper.toDto(alertGroupRepository.save(alertGroupEntityByUid));
    }

    @Transactional
    public void deleteAlertGroup(UUID uid) {
        alertGroupRepository.deleteById(uid);
    }

    @Transactional
    public void linkNotificationRuleToAlertGroup(UUID alertGroupUid, UUID notificationRuleUid) {
        NotificationRuleEntity notificationRuleEntity = notificationRuleRepository.findById(notificationRuleUid)
                .orElseThrow(ErrorCode.NOTIFICATION_RULE_NOT_FOUND::generateException);
        AlertGroupEntity alertGroupEntityByUid = getAlertGroupEntityByUid(alertGroupUid);
        alertGroupEntityByUid.getNotificationRules().add(notificationRuleEntity);
    }

    @Transactional
    public void unlinkNotificationRuleFromAlertGroup(UUID alertGroupUid, UUID notificationRuleUid) {
        NotificationRuleEntity notificationRuleEntity = notificationRuleRepository.findById(notificationRuleUid)
                .orElseThrow(ErrorCode.NOTIFICATION_RULE_NOT_FOUND::generateException);
        AlertGroupEntity alertGroupEntityByUid = getAlertGroupEntityByUid(alertGroupUid);
        alertGroupEntityByUid.getNotificationRules().remove(notificationRuleEntity);
    }

    @Transactional
    public void linkAlertRuleToAlertGroup(UUID alertGroupUid, UUID alertRuleUid) {
        AlertRuleEntity alertRuleEntity = alertRuleRepository.findById(alertRuleUid)
                .orElseThrow(ErrorCode.ALERT_RULE_NOT_FOUND::generateException);
        AlertGroupEntity alertGroupEntityByUid = getAlertGroupEntityByUid(alertGroupUid);
        alertRuleEntity.getAlertGroups().add(alertGroupEntityByUid);
        alertGroupEntityByUid.getRules().add(alertRuleEntity);
    }

    @Transactional
    public void unlinkAlertRuleFromAlertGroup(UUID alertGroupUid, UUID alertRuleUid) {
        AlertRuleEntity alertRuleEntity = alertRuleRepository.findById(alertRuleUid)
                .orElseThrow(ErrorCode.ALERT_RULE_NOT_FOUND::generateException);
        AlertGroupEntity alertGroupEntityByUid = getAlertGroupEntityByUid(alertGroupUid);
        alertRuleEntity.getAlertGroups().remove(alertGroupEntityByUid);
        alertGroupEntityByUid.getRules().remove(alertRuleEntity);
    }
}
