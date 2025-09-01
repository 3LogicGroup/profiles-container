package ru.graviton.profiles.dao.repository;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import ru.graviton.profiles.dao.entity.AlertGroupEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Репозиторий для AlertGroup
 */
public interface AlertGroupRepository extends JpaRepository<AlertGroupEntity, UUID>, JpaSpecificationExecutor<AlertGroupEntity> {
    default List<AlertGroupEntity> getActiveAlertGroups() {
        var predicates = Specification.allOf(
                Specs.enabled(true));
        return findAll(predicates);
    }

    default Page<AlertGroupEntity> getActiveAlertGroups(Boolean enabled, String name, Pageable pageable) {
        var predicates = Specification.allOf(
                Specs.enabled(enabled),
                Specs.nameLike(name));
        return findAll(predicates, pageable);
    }

    @Query("select r from AlertGroupEntity r where r.enabled=true and r.uid=?1")
    Optional<AlertGroupEntity> findActiveGroupByUid(UUID uid);

    class Specs {
        static Specification<AlertGroupEntity> nameLike(String name) {
            if (StringUtils.isEmpty(name)) {
                return null;
            }
            return StringUtils.isEmpty(name) ? null : (entity, query, cb)
                    -> cb.like(cb.upper(entity.get("groupName")), "%" + name.trim().toUpperCase() + "%");
        }

        static Specification<AlertGroupEntity> enabled(Boolean enabled) {
            return enabled == null ? null :
                    (entity, query, cb)
                            -> cb.equal(entity.get("enabled"), enabled);
        }
    }
}
