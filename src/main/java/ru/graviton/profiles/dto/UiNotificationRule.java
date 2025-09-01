package ru.graviton.profiles.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class UiNotificationRule extends AbstractNotificationRule {

    @Override
    public boolean isAvailable() {
        return true;
    }

    @Override
    public String getType() {
        return "UI";
    }
}
