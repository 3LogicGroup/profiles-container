package ru.graviton.profiles.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProfileDataExt<T extends AbstractApiProfileData<?>> extends ProfileBaseDto {
    private UUID uid;

    private T data;

    private Protocol protocol;
}
