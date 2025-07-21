package ru.graviton.profiles.dto.metrics;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class UiApiData {

    private String login;
    private String password;

    private boolean requireAuthentication;
    private boolean requireEncodeToBase64 = false;
    private AuthenticationType authenticationType;


    private String productNameFieldName;
    private String serialNumberFieldName;

    private List<UiApiFieldGroup> groups;

    public enum AuthenticationType {
        SESSION, BASIC
    }

}
