package ru.graviton.profiles.handler;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.lang.Nullable;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import ru.graviton.profiles.exceptions.ErrorCode;
import ru.graviton.profiles.exceptions.ErrorDto;
import ru.graviton.profiles.exceptions.ProfilesContainerException;

import java.util.ArrayList;
import java.util.List;

/**
 * Обработчик исключений для web
 */
@Slf4j
@ControllerAdvice
public class CustomExceptionHandler extends ResponseEntityExceptionHandler {

    /**
     * Обработчик исключений для web
     *
     * @param e       Внутреннее исключение системы
     * @param request Запрос с фронта
     * @return Response Entity
     */
    @ExceptionHandler(ProfilesContainerException.class)
    @ResponseBody
    public ResponseEntity<Object> handlePrometheusAdminException(ProfilesContainerException e, WebRequest request) {
        log.error(e.getMessage(), e);
        return handleExceptionInternal(e,
                ErrorDto.create(e, e.getErrorCode(), e.getResponseStatus(), request),
                new HttpHeaders(), e.getResponseStatus(), request);
    }

    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status,
            WebRequest request) {
        List<String> errors = new ArrayList<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errors.add(error.getField() + ": " + error.getDefaultMessage());
        }
        for (ObjectError error : ex.getBindingResult().getGlobalErrors()) {
            errors.add(error.getObjectName() + ": " + error.getDefaultMessage());
        }
        ErrorDto errorDto = ErrorDto.create(String.join("; ", errors),
                ErrorCode.REQUEST_NOT_VALID, ErrorCode.REQUEST_NOT_VALID.getStatus(), request);
        return handleExceptionInternal(
                ex, errorDto, headers, ErrorCode.REQUEST_NOT_VALID.getStatus(), request);
    }

    @Override
    protected ResponseEntity<Object> handleMissingServletRequestParameter(
            MissingServletRequestParameterException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        String error = ex.getParameterName() + " parameter is missing";
        ErrorDto errorDto = ErrorDto.create(error,
                ErrorCode.REQUEST_NOT_VALID, ErrorCode.REQUEST_NOT_VALID.getStatus(), request);
        return new ResponseEntity<>(
                errorDto, new HttpHeaders(), errorDto.getStatus());
    }

    @Nullable
    protected ResponseEntity<Object> handleHttpMessageNotReadable(
            HttpMessageNotReadableException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        String error = ex.getLocalizedMessage() + " parameter is missing";
        ErrorDto errorDto = ErrorDto.create(error,
                ErrorCode.REQUEST_NOT_VALID, ErrorCode.REQUEST_NOT_VALID.getStatus(), request);
        return new ResponseEntity<>(
                errorDto, new HttpHeaders(), errorDto.getStatus());
    }

    @Override
    protected ResponseEntity<Object> handleHandlerMethodValidationException(HandlerMethodValidationException ex,
                                                                            HttpHeaders headers,
                                                                            HttpStatusCode status,
                                                                            WebRequest request) {
        ErrorDto errorDto = ErrorDto.create(ex.getReason(),
                ErrorCode.REQUEST_NOT_VALID, ErrorCode.REQUEST_NOT_VALID.getStatus(), request);
        return new ResponseEntity<>(
                errorDto, new HttpHeaders(), errorDto.getStatus());
    }

    /**
     * Обработчик исключений для web
     *
     * @param ex      ConstraintViolationException
     * @param request Запрос с фронта
     * @return Response Entity
     */
    @ExceptionHandler({ConstraintViolationException.class})
    public ResponseEntity<Object> handleConstraintViolation(
            ConstraintViolationException ex, WebRequest request) {
        List<String> errors = new ArrayList<>();
        for (ConstraintViolation<?> violation : ex.getConstraintViolations()) {
            errors.add(violation.getRootBeanClass().getName() + " "
                    + violation.getPropertyPath() + ": " + violation.getMessage());
        }

        ErrorDto errorDto = ErrorDto.create(String.join("; ", errors),
                ErrorCode.REQUEST_NOT_VALID, ErrorCode.REQUEST_NOT_VALID.getStatus(), request);
        return handleExceptionInternal(
                ex, errorDto, new HttpHeaders(), ErrorCode.REQUEST_NOT_VALID.getStatus(), request);
    }

    /**
     * Обработчик исключений для web
     *
     * @param ex      MethodArgumentTypeMismatchException
     * @param request Запрос с фронта
     * @return Response Entity
     */
    @ExceptionHandler({MethodArgumentTypeMismatchException.class})
    public ResponseEntity<Object> handleMethodArgumentTypeMismatch(
            MethodArgumentTypeMismatchException ex, WebRequest request) {
        String error =
                ex.getName() + " should be of type " + ex.getRequiredType().getName();

        ErrorDto errorDto = ErrorDto.create(error,
                ErrorCode.REQUEST_NOT_VALID, ErrorCode.REQUEST_NOT_VALID.getStatus(), request);
        return new ResponseEntity<>(
                errorDto, new HttpHeaders(), errorDto.getStatus());
    }
}
