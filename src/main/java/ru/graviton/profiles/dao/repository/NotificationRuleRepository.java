package ru.graviton.profiles.dao.repository;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import ru.graviton.profiles.dao.entity.NotificationRuleEntity;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

/**
 * Репозиторий для AlertGroup
 */
public interface NotificationRuleRepository extends JpaRepository<NotificationRuleEntity, UUID>, JpaSpecificationExecutor<NotificationRuleEntity> {


    @Query(nativeQuery = true, value = "select nr.* from notification_rules nr join alert_rules_notifications arn on nr.uid =arn.notification_uid where arn.rule_uid=?1")
    List<NotificationRuleEntity> getNotificationRuleEntitiesByAlertUid(UUID alertUid);

    @Query("select n from NotificationRuleEntity n where n.uid=?1")
    Optional<NotificationRuleEntity> findActiveNotificationRuleByUid(UUID ruleUid);

    default Page<NotificationRuleEntity> findAllEnabledNotificationRules(Boolean enabled, String name, Pageable pageable) {
        var predicates = Specification.allOf(
                Specs.enabled(enabled),
                Specs.nameLike(name));
        return findAll(predicates, pageable);
    }

    @Query("select r from NotificationRuleEntity r where r.uid in (?1)")
    List<NotificationRuleEntity> findAllEnabledNotificationRules(Set<UUID> uids);

    class Specs {
        static Specification<NotificationRuleEntity> nameLike(String name) {
            return StringUtils.isEmpty(name) ? null : (entity, query, cb)
                    -> cb.like(cb.upper(entity.get("name")), "%" + name.trim().toUpperCase() + "%");
        }

        static Specification<NotificationRuleEntity> enabled(Boolean enabled) {
            return enabled == null ? null :
                    (entity, query, cb)
                            -> cb.equal(entity.get("enabled"), enabled);
        }
    }
}
