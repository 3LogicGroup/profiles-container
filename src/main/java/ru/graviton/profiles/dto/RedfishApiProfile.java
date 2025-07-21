package ru.graviton.profiles.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import ru.graviton.profiles.dao.entity.RedfishProfile;
import ru.graviton.profiles.dto.inventory.RedfishInventoryGroup;
import ru.graviton.profiles.dto.metrics.RedfishMetricGroup;
import ru.graviton.profiles.dto.metrics.UiApiData;

import java.util.List;

/**
 * Описание профиля
 */
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class RedfishApiProfile extends AbstractApiProfileData<RedfishProfile> {

    private String login;
    private String password;

    @NotBlank
    private String path;

    private String systemsSelf;

    private List<RedfishMetricGroup> metricsGroups;

    private List<RedfishInventoryGroup> inventoryGroups;

    private UiApiData uiApiData;

    private String selfPath;

    @Override
    public RedfishProfile toProfile() {
        return RedfishProfile.builder()
                .login(getLogin())
                .password(getPassword())
                .path(getPath())
                .systemsSelf(getSystemsSelf())
                .metricsGroups(getMetricsGroups())
                .inventoryGroups(getInventoryGroups())
                .uiApiData(getUiApiData())
                .build();
    }
}
