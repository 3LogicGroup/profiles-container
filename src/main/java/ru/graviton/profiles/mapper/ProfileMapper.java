package ru.graviton.profiles.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import ru.graviton.profiles.dao.entity.AbstractProfileData;
import ru.graviton.profiles.dao.entity.ProfileDataEntity;
import ru.graviton.profiles.dao.entity.ProfileEntity;
import ru.graviton.profiles.dto.*;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface ProfileMapper {

    @Mapping(target = "profileData", source = "profileDataList", qualifiedByName = "profileDataMapper")
    ProfileDto toDto(ProfileEntity profile);

    @Named("profileDataMapper")
    default Map<Protocol, AbstractApiProfileData<?>> profileDataMapper(Set<ProfileDataEntity> profileDataList) {
        return profileDataList.stream()
                .collect(Collectors.toMap(ProfileDataEntity::getProtocol, y -> y.getProfileData().toApiProfile(), (x1, x2) -> x1));

    }

    @Mapping(target = "profileName", source = "entity.profile.profileName")
    @Mapping(target = "productName", source = "entity.profile.productName")
    @Mapping(target = "model", source = "entity.profile.model")
    @Mapping(target = "productNameRus", source = "entity.profile.productNameRus")
    @Mapping(target = "uid", source = "entity.profile.uid")
    @Mapping(target = "data", source = "entity.profileData", qualifiedByName = "profileApiDataMapper")
    @Mapping(target = "active", source = "entity.profile.active")
    @Mapping(target = "deviceType", source = "entity.profile.deviceType")
    @Mapping(target = "protocol", source = "entity.protocol")
    ProfileDataExt<AbstractApiProfileData<?>> toProfileDataExt(ProfileDataEntity entity);

    @Named("profileApiDataMapper")
    default AbstractApiProfileData<?> profileApiDataMapper(AbstractProfileData<? extends AbstractApiProfileData<?>> profileData) {
        return profileData.toApiProfile();
    }
}
