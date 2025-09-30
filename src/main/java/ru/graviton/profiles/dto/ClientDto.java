package ru.graviton.profiles.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Set;
import java.util.UUID;

@Getter
@Setter
public class ClientDto {
    private UUID uid;

    private String companyName;

    private String publicKey;

    private Set<String> licenses;

}
