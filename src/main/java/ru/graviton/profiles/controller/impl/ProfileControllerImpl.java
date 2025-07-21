package ru.graviton.profiles.controller.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.graviton.profiles.controller.ProfileController;
import ru.graviton.profiles.dto.AbstractApiProfileData;
import ru.graviton.profiles.dto.ProfileDataExt;
import ru.graviton.profiles.dto.ProfileDto;
import ru.graviton.profiles.dto.Protocol;
import ru.graviton.profiles.dto.request.SaveProfileRequest;
import ru.graviton.profiles.dto.request.UpdateProfileRequest;
import ru.graviton.profiles.dto.response.ResultResponse;
import ru.graviton.profiles.service.ProfileService;

import java.util.List;
import java.util.UUID;

/**
 * Контроллер экспортера для админки
 */
@RestController
@RequestMapping("profiles")
@RequiredArgsConstructor
public class ProfileControllerImpl implements ProfileController {

    private final ProfileService profileService;

    @Override
    public ResultResponse<List<ProfileDto>> getProfiles() {
        return ResultResponse.of(profileService.getProfiles());
    }

    @Override
    public ResultResponse<List<ProfileDataExt<AbstractApiProfileData<?>>>> getProfilesByProtocol(Protocol protocol) {
        return ResultResponse.of(profileService.getProfiles(protocol));
    }

    @Override
    public ResultResponse<ProfileDto> getProfile(String profileName) {
        return ResultResponse.of(profileService.getProfile(profileName));
    }

    @Override
    public ResultResponse<ProfileDataExt<AbstractApiProfileData<?>>> getBaseProfile(Protocol protocol, UUID uuid) {
        return ResultResponse.of(profileService.getProfileByProtocol(protocol, uuid));
    }

    @Override
    public ResultResponse<ProfileDto> getProfile(UUID uuid) {
        return ResultResponse.of(profileService.getProfile(uuid));
    }

    @Override
    public ResultResponse<ProfileDto> updateProfile(UUID profileUid, UpdateProfileRequest updateProfileRequest) {
        return ResultResponse.of(profileService.updateProfile(
                profileUid, updateProfileRequest.getProfileName(), updateProfileRequest.getProductName(),
                updateProfileRequest.getProductNameRus(), updateProfileRequest.getDeviceType(), updateProfileRequest.getModel(), updateProfileRequest.getUpdatingTypes(),
                updateProfileRequest.getIpmiApiProfile(), updateProfileRequest.getNodeProfile(),
                updateProfileRequest.getSnmpApiProfile(), updateProfileRequest.getRedfishApiProfile(), updateProfileRequest.getSshApiProfile()));
    }

    @Override
    public void softDeleteProfile(UUID profileUid) {
        profileService.softDeleteProfile(profileUid);
    }

    @Override
    public void softRecoveryProfile(UUID profileUid) {
        profileService.softRecoveryProfile(profileUid);
    }

    @Override
    public ResultResponse<ProfileDto> addProfile(SaveProfileRequest saveProfileRequest) {
        return ResultResponse.of(profileService.addProfile(saveProfileRequest));
    }

    @Override
    public void deleteProfile(UUID uuid) {
        profileService.deleteProfile(uuid);
    }

    @Override
    public ResultResponse<Boolean> checkOwnerProfile(Protocol protocol, UUID profileUid, UUID targetUid) {
        return ResultResponse.of(profileService.existTargetProfileLink(profileUid, targetUid, protocol));
    }
}
