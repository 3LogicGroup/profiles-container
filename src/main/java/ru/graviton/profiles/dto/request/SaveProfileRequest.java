package ru.graviton.profiles.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import ru.graviton.profiles.dto.*;


@Getter
@Setter
public class SaveProfileRequest {
    @NotBlank
    private String profileName;
    @NotBlank
    private String productName;
    @NotBlank
    private String productNameRus;
    @NotBlank
    private DeviceType deviceType;

    @Schema(description = "ipmi_profile")
    private IpmiApiProfile ipmiApiProfile;

    @Schema(description = "snmp_profile")
    private SnmpApiProfile snmpApiProfile;

    @Schema(description = "redfish_profile")
    private RedfishApiProfile redfishApiProfile;

    @Schema(description = "node_profile")
    private NodeProxyApiProfile nodeApiProfile;

    @Schema(description = "ssh_profile")
    private SshApiProfile sshApiProfile;
}
