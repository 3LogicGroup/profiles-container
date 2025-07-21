package ru.graviton.profiles.dao.entity;

import io.hypersistence.utils.hibernate.type.json.JsonBinaryType;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Type;
import ru.graviton.profiles.dto.AbstractApiProfileData;
import ru.graviton.profiles.dto.Protocol;

import java.io.Serializable;

@Entity
@Table(name = "profiles_data")
@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@IdClass(ProfileDataEntity.ProfileDataKey.class)
public class ProfileDataEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_uid")
    @Id
    private ProfileEntity profile;

    @Id
    @Enumerated(EnumType.STRING)
    private Protocol protocol;

    @Column(name = "scrape_interval")
    @Builder.Default
    private Integer scrapeInterval = 60;

    @Column(name = "scrape_timeout")
    @Builder.Default
    private Integer scrapeTimeout = 50;

    @Column(name = "profile_data")
    @Type(value = JsonBinaryType.class)
    private AbstractProfileData<? extends AbstractApiProfileData<?>> profileData;

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @EqualsAndHashCode
    @ToString
    public static class ProfileDataKey implements Serializable {

        private ProfileEntity profile;
        private Protocol protocol;
    }

}
