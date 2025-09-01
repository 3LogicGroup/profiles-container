package ru.graviton.profiles.dao.entity;

import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Абстрактный класс для сущности в БД
 */
@MappedSuperclass
@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public abstract class AbstractEntity implements Serializable {

    private LocalDateTime dateUpdate;

    private LocalDateTime dateCreate;

    @PrePersist
    private void prePersist() {
        dateCreate = LocalDateTime.now();
    }

    @PreUpdate
    private void preUpdate() {
        dateUpdate = LocalDateTime.now();
    }
}
