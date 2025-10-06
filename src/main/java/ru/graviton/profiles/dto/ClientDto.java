package ru.graviton.profiles.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;
import java.util.UUID;

@Getter
@Setter
public class ClientDto {
    @NotNull
    private UUID uid;

    private String companyName;

    @NotBlank
    private String email;

    @NotBlank
    private String publicKey;

    private Set<String> licenses;

}
