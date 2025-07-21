package ru.graviton.profiles.dao.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import ru.graviton.profiles.dto.DeviceType;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "profiles")
@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class ProfileEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID uid;

    @Column(name = "profile_name")
    private String profileName;

    @Column(name = "model")
    private String model;


    @Column(name = "product_name")
    private String productName;

    @Column(name = "product_name_rus")
    private String productNameRus;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "profile")
    @Builder.Default
    private Set<ProfileDataEntity> profileDataList = new HashSet<>();

    @Builder.Default
    private boolean active = true;

    @Column(name = "device_type")
    @Enumerated(EnumType.STRING)
    private DeviceType deviceType;

    @Column(name = "date_update")
    private LocalDateTime dateUpdate;

    @Column(name = "date_create")
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
