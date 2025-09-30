package ru.graviton.profiles.dao.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "clients")
@Getter
@Setter
public class ClientEntity {

    @Id
    @Column(name = "uid")
    private UUID uid;

    @Column(name = "company_name")
    private String companyName;

    @Column(name = "public_key")
    private String publicKey;

    @OneToMany(mappedBy = "client", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<LicenseEntity> licenses;
}
