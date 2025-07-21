package ru.graviton.profiles.exceptions;

import lombok.Builder;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

import java.time.OffsetDateTime;

/**
 * DTO для ошибок системы
 */
@Builder
@Getter
public class ErrorDto {

    @Builder.Default
    private int status = 500;


    @Builder.Default
    private OffsetDateTime timestamp = OffsetDateTime.now();

    private String method;

    private String message;

    private String path;

    private ErrorCode errorCode;

    /**
     * Создание Dto
     *
     * @param message    Сообщение об ошибке
     * @param errorCode  Код ошибки
     * @param status     Статус ошибки
     * @param webRequest Запрос с клиента
     * @return Dto с информацией об ошибке
     */
    public static ErrorDto create(String message, ErrorCode errorCode, HttpStatus status, WebRequest webRequest) {
        var builder = ErrorDto.builder()
                .status(status.value())
                .errorCode(errorCode)
                .message(message);
        addRequestInfo(builder, webRequest);
        return builder.build();
    }

    /**
     * Создание Dto
     *
     * @param exception  Исключение
     * @param errorCode  Код ошибки
     * @param status     Статус ошибки
     * @param webRequest Запрос с клиента
     * @return Dto с информацией об ошибке
     */
    public static ErrorDto create(Exception exception, ErrorCode errorCode, HttpStatus status, WebRequest webRequest) {
        var builder = ErrorDto.builder()
                .status(status.value())
                .errorCode(errorCode)
                .message(exception.getMessage());
        addRequestInfo(builder, webRequest);
        return builder.build();
    }

    /**
     * Парсинг запроса с клиента
     *
     * @param builder    builder
     * @param webRequest Запрос с клиента
     */
    private static void addRequestInfo(ErrorDtoBuilder builder, WebRequest webRequest) {
        if (webRequest instanceof ServletWebRequest servletWebRequest) {
            var httpServletRequest = servletWebRequest.getRequest();
            builder
                    .path(httpServletRequest.getRequestURI())
                    .method(httpServletRequest.getMethod());
        }
    }
}
