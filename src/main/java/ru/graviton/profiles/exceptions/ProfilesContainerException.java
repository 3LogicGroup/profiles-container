package ru.graviton.profiles.exceptions;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

/**
 * Описание ошибки сервиса админки
 */
@Getter
@Builder
@Setter
public class ProfilesContainerException extends RuntimeException {

    private final ErrorCode errorCode;

    private String description;

    private HttpStatus responseStatus;

    @Override
    public String getMessage() {
        if (getDescription() != null) {
            return String.format("%s, %s", errorCode.getText(), getDescription());
        } else {
            return errorCode.getText();
        }
    }
}
