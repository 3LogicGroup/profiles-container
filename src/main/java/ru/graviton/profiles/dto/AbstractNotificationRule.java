package ru.graviton.profiles.dto;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type", visible = true)
@JsonSubTypes({
        @JsonSubTypes.Type(value = UiNotificationRule.class, name = "UI"),
        @JsonSubTypes.Type(value = EmailNotificationRule.class, name = "EMAIL"),
        @JsonSubTypes.Type(value = TelegramNotificationRule.class, name = "TELEGRAM")
})
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
public abstract class AbstractNotificationRule {

    @JsonIgnore
    public abstract boolean isAvailable();

    public abstract String getType();
}
