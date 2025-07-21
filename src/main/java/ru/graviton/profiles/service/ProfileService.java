package ru.graviton.profiles.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.graviton.profiles.dao.entity.ProfileDataEntity;
import ru.graviton.profiles.dao.entity.ProfileEntity;
import ru.graviton.profiles.dao.repository.ProfileRepository;
import ru.graviton.profiles.dto.*;
import ru.graviton.profiles.dto.request.SaveProfileRequest;
import ru.graviton.profiles.exceptions.ErrorCode;
import ru.graviton.profiles.mapper.ProfileMapper;

import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProfileService {

    private final ProfileRepository profileRepository;

    private final ProfileMapper profileMapper;


    public boolean existTargetProfileLink(UUID profileUid, UUID targetUid, Protocol protocol) {
        return profileRepository.existTargetProfileLink(profileUid, targetUid, protocol);
    }

    @Transactional
    public List<ProfileDto> getProfiles() {
        return profileRepository.findAll().stream().map(profileMapper::toDto).toList();
    }

    @Transactional
    public List<ProfileDataExt<AbstractApiProfileData<?>>> getProfiles(Protocol protocol) {
        return profileRepository.findAllByProtocol(protocol)
                .stream()
                .map(profileMapper::toProfileDataExt)
                .toList();
    }

    @Transactional
    public ProfileDto getProfile(String name) {
        return profileRepository.getProfileEntityByProfileName(name).map(profileMapper::toDto)
                .orElseThrow(ErrorCode.PROFILE_NOT_FOUND::generateException);
    }

    @Transactional
    public ProfileDto getProfile(UUID profileUid) {
        return profileRepository.findById(profileUid).map(profileMapper::toDto)
                .orElseThrow(ErrorCode.PROFILE_NOT_FOUND::generateException);
    }

    @Transactional
    public ProfileDataExt<AbstractApiProfileData<?>> getProfileByProtocol(Protocol protocol, UUID profileUid) {
        return profileRepository.findProfileData(profileUid, protocol)
                .map(profileMapper::toProfileDataExt)
                .orElseThrow(ErrorCode.PROFILE_BY_PROTOCOL_NOT_FOUND::generateException);
    }

    @Transactional
    public ProfileDto addProfile(SaveProfileRequest request) {

        profileRepository.getProfileEntityByProfileName(request.getProfileName())
                .ifPresent(profileNewEntity -> {
                    throw ErrorCode.PROFILE_IS_EXIST.generateException();
                });
        ProfileEntity profileEntity = ProfileEntity.builder()
                .active(true)
                .profileName(request.getProfileName())
                .productName(request.getProductName())
                .productNameRus(request.getProductNameRus())
                .deviceType(request.getDeviceType())
                .build();
        if (request.getIpmiApiProfile() != null) {
            profileEntity.getProfileDataList().add(ProfileDataEntity.builder()
                    .profile(profileEntity)
                    .protocol(Protocol.IPMI)
                    .profileData(request.getIpmiApiProfile().toProfile())
                    .build());
        }
        if (request.getNodeApiProfile() != null) {
            profileEntity.getProfileDataList().add(ProfileDataEntity.builder()
                    .profile(profileEntity)
                    .protocol(Protocol.NODE)
                    .profileData(request.getNodeApiProfile().toProfile())
                    .build());
        }
        if (request.getRedfishApiProfile() != null) {
            profileEntity.getProfileDataList().add(ProfileDataEntity.builder()
                    .profile(profileEntity)
                    .protocol(Protocol.REDFISH)
                    .profileData(request.getRedfishApiProfile().toProfile())
                    .build());
        }
        if (request.getSnmpApiProfile() != null) {
            profileEntity.getProfileDataList().add(ProfileDataEntity.builder()
                    .profile(profileEntity)
                    .protocol(Protocol.SNMP)
                    .profileData(request.getSnmpApiProfile().toProfile())
                    .build());
        }
        if (request.getSshApiProfile() != null) {
            profileEntity.getProfileDataList().add(ProfileDataEntity.builder()
                    .profile(profileEntity)
                    .protocol(Protocol.SSH)
                    .profileData(request.getSshApiProfile().toProfile())
                    .build());
        }
        return profileMapper.toDto(profileRepository.save(profileEntity));
    }

    @Transactional
    public ProfileDto updateProfile(UUID profileUid, String profileName, String productName, String productNameRus,
                                    DeviceType deviceType, String model, Set<Protocol> jobTypes,
                                    IpmiApiProfile ipmiApiProfile, NodeProxyApiProfile nodeProxyApiProfile,
                                    SnmpApiProfile snmpApiProfile, RedfishApiProfile redfishApiProfile,
                                    SshApiProfile sshApiProfile) {
        ProfileEntity profileEntity = profileRepository.findById(profileUid)
                .orElseThrow(ErrorCode.PROFILE_NOT_FOUND::generateException);
        profileEntity.setProfileName(profileName);
        profileEntity.setProductName(productName);
        profileEntity.setProductNameRus(productNameRus);
        profileEntity.setDeviceType(deviceType);
        profileEntity.setModel(model);
        Map<Protocol, AbstractApiProfileData<?>> map = new HashMap<>();
        if (jobTypes != null) {
            jobTypes.forEach(protocol -> {
                AbstractApiProfileData<?> data = null;
                switch (protocol) {
                    case IPMI -> data = ipmiApiProfile;
                    case REDFISH -> data = redfishApiProfile;
                    case NODE -> data = nodeProxyApiProfile;
                    case SNMP -> data = snmpApiProfile;
                    case SSH -> data = sshApiProfile;
                }
                if (data != null) {
                    map.put(protocol, data);
                }
            });
        }
        map.forEach((k, v) -> profileEntity.getProfileDataList()
                .stream()
                .filter(f -> f.getProtocol().equals(k))
                .findAny()
                .ifPresentOrElse(profileDataEntity -> profileDataEntity.setProfileData(v.toProfile()),
                        () -> {
                            ProfileDataEntity profileDataEntity = new ProfileDataEntity();
                            profileEntity.getProfileDataList().add(profileDataEntity);
                            profileDataEntity.setProfile(profileEntity);
                            profileDataEntity.setProtocol(k);
                            profileDataEntity.setProfileData(v.toProfile());
                        }));
        return profileMapper.toDto(profileRepository.save(profileEntity));
    }

    @Transactional
    public void deleteProfile(UUID profileId) {
        ProfileEntity profileEntity = profileRepository.findById(profileId)
                .orElseThrow(ErrorCode.PROFILE_NOT_FOUND::generateException);

        profileRepository.delete(profileEntity);

    }

    @Transactional
    public void softDeleteProfile(UUID profileId) {
        profileRepository.setIsActive(profileId, true);
    }

    @Transactional
    public void softRecoveryProfile(UUID profileId) {
        profileRepository.setIsActive(profileId, false);
    }
}
