package ru.graviton.profiles.dto;

import lombok.AllArgsConstructor;
import lombok.experimental.SuperBuilder;
import ru.graviton.profiles.dao.entity.AbstractProfileData;


@SuperBuilder
@AllArgsConstructor
public abstract class AbstractApiProfileData<T extends AbstractProfileData<? extends AbstractApiProfileData<?>>> {
    public abstract T toProfile();
}
