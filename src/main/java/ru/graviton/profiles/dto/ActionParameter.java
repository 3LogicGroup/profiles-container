package ru.graviton.profiles.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.Set;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ActionParameter {
    @JsonProperty("Name")
    private String name;

    @JsonProperty("DataType")
    private String dataType;


    @JsonProperty("ObjectDataType")
    private String objectDataType;

    @JsonProperty("Required")
    private Boolean required;

    @JsonProperty("AllowableValues")
    private Set<String> allowableValues;

    @JsonProperty("MaximumValue")
    private Integer maximumValue;

    @JsonProperty("MinimumValue")
    private Integer minimumValue;
}
