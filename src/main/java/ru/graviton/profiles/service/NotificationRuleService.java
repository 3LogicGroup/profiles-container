package ru.graviton.profiles.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.graviton.profiles.dao.entity.NotificationRuleEntity;
import ru.graviton.profiles.dao.repository.NotificationRuleRepository;
import ru.graviton.profiles.dto.AbstractNotificationRule;
import ru.graviton.profiles.dto.NotificationRule;
import ru.graviton.profiles.dto.NotificationType;
import ru.graviton.profiles.exceptions.ErrorCode;
import ru.graviton.profiles.mapper.NotificationRuleMapper;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationRuleService {
    private final NotificationRuleRepository notificationRuleRepository;
    private final NotificationRuleMapper notificationRuleMapper;

    @Transactional
    public <T extends AbstractNotificationRule> NotificationRule createNotificationRule(String name, String description,
                                                                                        NotificationType notificationType,
                                                                                        Integer initialDelay,
                                                                                        Integer alertDelay,
                                                                                        String fireMessageTemplate,
                                                                                        String resolveMessageTemplate,
                                                                                        T notificationRule,
                                                                                        Set<UUID> notificationEscalationRules) {
        Set<NotificationRuleEntity> escalationRules = Optional.ofNullable(notificationEscalationRules)
                .map(notificationRuleRepository::findAllById)
                .map(HashSet::new)
                .orElse(null);
        NotificationRuleEntity build = NotificationRuleEntity.builder()
                .name(name)
                .description(description)
                .alertDelay(alertDelay)
                .initialDelay(initialDelay)
                .fireMessageTemplate(fireMessageTemplate)
                .resolveMessageTemplate(resolveMessageTemplate)
                .type(notificationType)
                .notificationRule(notificationRule)
                .enabled(true)
                .build();
        NotificationRuleEntity save = notificationRuleRepository.save(build);

        return notificationRuleMapper.toDto(save);
    }

    @Transactional
    public <T extends AbstractNotificationRule> NotificationRule updateNotificationRule(UUID uid, String name, String description,
                                                                                        T rule, Integer initialDelay, Integer alertDelay) {
        NotificationRuleEntity notificationRuleEntity = getNotificationRuleEntity(uid);
        notificationRuleEntity.setName(name);
        notificationRuleEntity.setDescription(description);
        notificationRuleEntity.setAlertDelay(alertDelay);
        notificationRuleEntity.setNotificationRule(rule);
        notificationRuleEntity.setInitialDelay(initialDelay);
        return notificationRuleMapper.toDto(notificationRuleRepository.save(notificationRuleEntity));
    }

    @Transactional
    public NotificationRule getActiveNotificationRule(UUID uid) {
        return notificationRuleMapper.toDto(getNotificationRuleEntity(uid));
    }

    @Transactional
    public NotificationRule getNotificationRule(UUID uid) {
        return notificationRuleMapper.toDto(notificationRuleRepository.findById(uid).orElse(null));
    }

    @Transactional
    public Page<NotificationRule> getNotificationRules(String name, Boolean enabled, Integer page, Integer size) {
        return notificationRuleRepository.findAllEnabledNotificationRules(enabled, name, PageRequest.of(page, size,
                        Sort.by(Sort.Direction.ASC, "name")))
                .map(notificationRuleMapper::toDto);
    }

    @Transactional
    public List<NotificationRule> getNotificationRules() {
        return notificationRuleRepository.findAll()
                .stream()
                .map(notificationRuleMapper::toDto)
                .toList();
    }

    @Transactional
    public List<NotificationRule> getNotificationRules(Set<UUID> uids) {
        return notificationRuleRepository.findAllEnabledNotificationRules(uids)
                .stream()
                .map(notificationRuleMapper::toDto)
                .toList();
    }

    private NotificationRuleEntity getNotificationRuleEntity(UUID uid) {
        return notificationRuleRepository.findActiveNotificationRuleByUid(uid).orElseThrow(() -> ErrorCode.NOTIFICATION_RULE_NOT_FOUND.generateException("uid правила: " + uid));
    }

    @Transactional
    public List<NotificationRule> getNotificationRulesByAlertRuleUid(UUID alertUid) {
        return notificationRuleRepository.getNotificationRuleEntitiesByAlertUid(alertUid)
                .stream()
                .map(notificationRuleMapper::toDto)
                .toList();
    }

    @Transactional
    public void deleteNotificationRule(UUID uid) {
        notificationRuleRepository.findById(uid)
                .ifPresent(notificationRuleEntity -> {
                    notificationRuleEntity.getAlertRules()
                            .forEach(f -> f.getNotificationRules().remove(notificationRuleEntity));
                    notificationRuleEntity.getAlertGroups()
                            .forEach(f -> f.getNotificationRules().remove(notificationRuleEntity));
                    notificationRuleEntity.getAlertRules().clear();
                    notificationRuleEntity.getAlertGroups().clear();
                    log.error("");
                });
        notificationRuleRepository.deleteById(uid);
    }

    @Transactional
    public void changeActivityNotificationRule(UUID uid, Boolean active) {
        NotificationRuleEntity notificationRuleEntity = notificationRuleRepository.findById(uid)
                .orElseThrow(ErrorCode.NOTIFICATION_RULE_NOT_FOUND::generateException);
        notificationRuleEntity.setEnabled(active);
    }
}
