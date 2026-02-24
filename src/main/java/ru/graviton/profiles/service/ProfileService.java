package ru.graviton.profiles.service;

import com.fasterxml.jackson.databind.ObjectMapper;
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
    private final ObjectMapper objectMapper;


    public boolean existTargetProfileLink(UUID profileUid, UUID targetUid, Protocol protocol) {
        return profileRepository.existTargetProfileLink(profileUid, targetUid, protocol);
    }

    @Transactional
    public String getProfilesSQL() {
        return getProfilesSql(profileRepository.findAll().stream()
                .filter(ProfileEntity::isActive)
                .toList());
    }

    private String getProfilesSql(List<ProfileEntity> profiles) {
        StringBuilder sb = new StringBuilder();
        sb.append("""
                DROP TABLE IF EXISTS public.profiles_temp;
                CREATE TABLE public.profiles_temp (
                uid uuid NOT NULL,
                profile_name varchar(50) NOT NULL,
                date_create timestamp NOT NULL,
                date_update timestamp NULL,
                active bool NOT NULL,
                product_name varchar(255) NULL,
                product_name_rus varchar(255) NULL,
                device_type varchar(50) NULL,
                model varchar(255) NULL,
                CONSTRAINT "profileTempPK" PRIMARY KEY (uid)
                );""");
        sb.append("""
                DROP TABLE IF EXISTS public.profiles_data_temp;
                CREATE TABLE public.profiles_data_temp (
                profile_uid uuid NOT NULL,
                protocol varchar(50) NOT NULL,
                profile_data jsonb NOT NULL,
                scrape_interval int4 NULL,
                scrape_timeout int4 NULL,
                CONSTRAINT profiles_data_temp_pkey PRIMARY KEY (profile_uid, protocol)
                );
                """);
        for (ProfileEntity p : profiles) {
            sb.append("INSERT INTO profiles_temp (uid, profile_name, date_create, date_update, active, product_name, product_name_rus, device_type, model) VALUES (")
                    .append("'").append(p.getUid()).append("', ")
                    .append("'").append(escapeSql(p.getProfileName())).append("', ")
                    .append("'").append(p.getDateCreate()).append("', ")
                    .append(p.getDateUpdate() != null ? "'" + p.getDateUpdate() + "'" : "NULL").append(", ")
                    .append(p.isActive()).append(", ")
                    .append(p.getProductName() != null ? "'" + escapeSql(p.getProductName()) + "'" : "NULL").append(", ")
                    .append(p.getProductNameRus() != null ? "'" + escapeSql(p.getProductNameRus()) + "'" : "NULL").append(", ")
                    .append(p.getDeviceType() != null ? "'" + p.getDeviceType().name() + "'" : "NULL").append(", ")
                    .append(p.getModel() != null ? "'" + escapeSql(p.getModel()) + "'" : "NULL")
                    .append(") ON CONFLICT (uid) DO UPDATE SET ")
                    .append("profile_name = EXCLUDED.profile_name, ")
                    .append("date_create = EXCLUDED.date_create, ")
                    .append("date_update = EXCLUDED.date_update, ")
                    .append("active = EXCLUDED.active, ")
                    .append("product_name = EXCLUDED.product_name, ")
                    .append("product_name_rus = EXCLUDED.product_name_rus, ")
                    .append("device_type = EXCLUDED.device_type, ")
                    .append("model = EXCLUDED.model;\n");

            Set<ProfileDataEntity> dataList = p.getProfileDataList();
            for (ProfileDataEntity d : dataList) {
                String profileDataJson;
                try {
                    profileDataJson = objectMapper.writeValueAsString(d.getProfileData());
                } catch (Exception ex) {
                    throw new RuntimeException("Failed to serialize profileData JSON", ex);
                }

                sb.append("INSERT INTO profiles_data_temp (profile_uid, protocol, profile_data, scrape_interval, scrape_timeout) VALUES (")
                        .append("'").append(p.getUid()).append("', ")
                        .append("'").append(d.getProtocol().name()).append("', ")
                        .append("'").append(escapeSql(profileDataJson)).append("', ")
                        .append(d.getScrapeInterval()).append(", ")
                        .append(d.getScrapeTimeout())
                        .append(") ON CONFLICT (profile_uid, protocol) DO UPDATE SET ")
                        .append("profile_data = EXCLUDED.profile_data, ")
                        .append("scrape_interval = EXCLUDED.scrape_interval, ")
                        .append("scrape_timeout = EXCLUDED.scrape_timeout;\n");
            }
        }

        return sb.toString();
    }

    private String escapeSql(String s) {
        if (s == null) return null;
        return s.replace("'", "''");
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
