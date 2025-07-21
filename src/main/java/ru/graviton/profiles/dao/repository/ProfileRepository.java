package ru.graviton.profiles.dao.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.graviton.profiles.dao.entity.ProfileDataEntity;
import ru.graviton.profiles.dao.entity.ProfileEntity;
import ru.graviton.profiles.dto.Protocol;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProfileRepository extends JpaRepository<ProfileEntity, UUID> {

    @Query("select p from ProfileEntity p where p.profileName=?1 and p.active=true")
    Optional<ProfileEntity> getProfileEntityByProfileName(String profileName);


    @Modifying
    @Query("update ProfileEntity p set p.active =:isActive  where p.uid = :uid")
    void setIsActive(UUID uid, @Param("isActive") boolean isActive);

    @Query("select p from ProfileDataEntity p where p.profile.uid=?1 and p.protocol=?2")
    Optional<ProfileDataEntity> findProfileData(UUID profileUid, Protocol protocol);

    @Query(nativeQuery = true, value = """
            select exists(select * from profiles_data pd
            join target_credentials tc on tc.target_uid=tc.target_uid and tc.protocol=pd.protocol
            where pd.profile_uid=:profileUid and tc.target_uid=:targetUid and pd.protocol=:#{#protocol.name()})
            """)
    boolean existTargetProfileLink(@Param("profileUid") UUID profileUid,
                                   @Param("targetUid") UUID targetUid,
                                   @Param("protocol") Protocol protocol);

    @Query("select pd from ProfileDataEntity pd join pd.profile p  where pd.protocol=?1 and p.active=true")
    List<ProfileDataEntity> findAllByProtocol(Protocol protocol);

//    @Query(value = "select p from ProfileDataEntity p join TargetCredentialEntity t where p.protocol=:protocol and t.target.uid=:targetUid")

    @Query(nativeQuery = true, value = "select p.profile_uid from profiles_data p join target_credentials t on p.profile_uid=t.profile_uid" +
            " and p.protocol=t.protocol where p.protocol=:#{#protocol.name()} and t.target_uid=:targetUid")
    UUID getProfileUidByTargetUidAndProtocol(@Param("targetUid") UUID targetUid,
                                             @Param("protocol") Protocol protocol);
}
