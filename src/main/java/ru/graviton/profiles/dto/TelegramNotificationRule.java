package ru.graviton.profiles.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class TelegramNotificationRule extends AbstractNotificationRule {
    @Builder.Default
    @Schema(description = "Идентификаторы чатов")
    private Set<Long> chatIds = new HashSet<>();

    @Override
    public boolean isAvailable() {
        return chatIds != null && !chatIds.isEmpty();
    }

    @Override
    public String getType() {
        return "TELEGRAM";
    }
}
