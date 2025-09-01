package ru.graviton.profiles.dao.repository;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import ru.graviton.profiles.dao.entity.AlertRuleEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Репозиторий для AlertRule
 */
public interface AlertRuleRepository extends JpaRepository<AlertRuleEntity, UUID>, JpaSpecificationExecutor<AlertRuleEntity> {

    @Query("select g from AlertRuleEntity g where g.enabled=true")
    List<AlertRuleEntity> getActiveAlertRules();

    default Page<AlertRuleEntity> getActiveAlertRules(Boolean enabled, String name, Pageable pageable) {
        var predicates = Specification.allOf(
                Specs.enabled(enabled),
                Specs.nameLike(name));
        return findAll(predicates, pageable);
    }


    @Query("select g from AlertRuleEntity g join g.notificationRules nr where g.enabled=true and nr.enabled=true" +
            " and nr.uid=?1")
    List<AlertRuleEntity> getActiveAlertRules(UUID notificationRuleUid);

    @Query("select r from AlertRuleEntity  r where r.enabled=true and r.uid=?1")
    Optional<AlertRuleEntity> findActiveRuleByUid(UUID uid);

    class Specs {
        static Specification<AlertRuleEntity> nameLike(String name) {
            return StringUtils.isEmpty(name) ? null : (entity, query, cb)
                    -> cb.like(cb.upper(entity.get("ruleName")), "%" + name.trim().toUpperCase() + "%");
        }

        static Specification<AlertRuleEntity> enabled(Boolean enabled) {
            return enabled == null ? null :
                    (entity, query, cb)
                            -> cb.equal(entity.get("enabled"), enabled);
        }
    }
}
