package ru.graviton.profiles.exceptions;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * Коды ошибок системы
 */
@AllArgsConstructor
@Getter
public enum ErrorCode {
    PROM_NOT_AVAILABLE("Prometheus не доступен", HttpStatus.NOT_FOUND),
    PROM_RELOAD_CONFIG_ERROR("Ошибка сохранения конфигурации Prometheus", HttpStatus.INTERNAL_SERVER_ERROR),
    PROM_ADD_JOB("Ошибка добавления задания в Prometheus", HttpStatus.BAD_REQUEST),
    PROM_JOB_NOT_FOUND("Задание не найдено", HttpStatus.NOT_FOUND),
    PROM_JOB_TYPE_NOT_DEFINED("Не определен тип задания Prometheus", HttpStatus.BAD_REQUEST),
    PROM_ADD_TARGET("Ошибка добавления цели в Prometheus", HttpStatus.BAD_REQUEST),
    PROM_UPDATE_TARGET("Ошибка обновления цели в Prometheus", HttpStatus.BAD_REQUEST),
    PROM_REMOVE_JOB("Невозможно удалить задание", HttpStatus.BAD_REQUEST),
    PROM_TARGET_NOT_FOUND("Устройство не найдено", HttpStatus.NOT_FOUND),
    PROM_TARGET_NOT_AVAILABLE("Устройство не доступно", HttpStatus.NOT_FOUND),
    PROM_REMOVE_TARGET("Невозможно удалить цель", HttpStatus.BAD_REQUEST),
    PROM_TARGET_LINK_JOB("Ошибка привязки устройства к заданию. Устройство не имеет профиля авторизации для задания", HttpStatus.BAD_REQUEST),
    PROM_ADD_GROUPS_FILES("Ошибка добавления файла группы оповещений", HttpStatus.BAD_REQUEST),
    PROM_DELETE_GROUPS_FILES("Ошибка удаления файла группы оповещений", HttpStatus.BAD_REQUEST),
    PROM_GROUPS_FILES_NOT_FOUND("Файл группы оповещений не найден", HttpStatus.BAD_REQUEST),

    PROM_ADD_GROUP_RULE("Ошибка добавления группы оповещений", HttpStatus.BAD_REQUEST),
    PROM_DELETE_GROUP_RULE("Ошибка удаления группы оповещений", HttpStatus.BAD_REQUEST),
    PROM_GROUP_RULE_NOT_FOUND("Группа оповещений не найдена", HttpStatus.BAD_REQUEST),

    PROM_ADD_RULE("Ошибка добавления правила оповещений", HttpStatus.BAD_REQUEST),
    PROM_RULE_NOT_FOUND("Правило оповещений не найдено", HttpStatus.NOT_FOUND),

    REQUEST_NOT_VALID("Запрос не корректен", HttpStatus.BAD_REQUEST),

    PROFILE_NOT_FOUND("Профиль не найден", HttpStatus.NOT_FOUND),
    PROFILE_BY_PROTOCOL_NOT_FOUND("Данные профиля с указанным протоколом не найдены", HttpStatus.NOT_FOUND),


    PROFILE_IS_EXIST("Профиль с заданным именем существует", HttpStatus.BAD_REQUEST),

    GET_STATISTICS_ERROR("Ошибка получения статистики сбора данных", HttpStatus.INTERNAL_SERVER_ERROR),
    DISCOVERY_RANGE_ERROR("Формат строки запроса не корректен", HttpStatus.BAD_REQUEST),

    COMMAND_NOT_FOUND("Команда не найдена", HttpStatus.NOT_FOUND),
    COMMAND_NOT_EXECUTABLE("Команду невозможно выполнить", HttpStatus.BAD_REQUEST),
    TARGET_WORKING("Устройство в данный момент работает", HttpStatus.FAILED_DEPENDENCY),
    SETTING_NOT_FOUND("Запрошенная настройка не найдена", HttpStatus.NOT_FOUND),
    SETTING_NOT_APPLICABLE("Значение настройки не применимо", HttpStatus.BAD_REQUEST),
    UNABLE_TO_REGISTER_ASYNC_REQUEST("Невозможно зарегистрировать запрос", HttpStatus.BAD_REQUEST),
    ASYNC_REQUEST_NOT_FOUND("Запрос не найден", HttpStatus.BAD_REQUEST),
    ASYNC_REQUEST_NOT_CANCELLED("Невозможно отменить запрос", HttpStatus.BAD_REQUEST),
    REPORT_REQUEST_NOT_VALID("Переданы некорректные параметры", HttpStatus.BAD_REQUEST),
    UNEXPECTED_REPORT_ERROR("Неожиданная ошибка формирования отчета", HttpStatus.INTERNAL_SERVER_ERROR),
    ;
    private final String text;
    private final HttpStatus status;

    /**
     * Создание PrometheusAdminException
     *
     * @return {@link ProfilesContainerException}
     */
    public ProfilesContainerException generateException() {
        return ProfilesContainerException.builder()
                .errorCode(this)
                .responseStatus(this.getStatus())
                .build();
    }

    /**
     * Создание PrometheusAdminException
     *
     * @param description Описание исключения
     * @return {@link ProfilesContainerException}
     */
    public ProfilesContainerException generateException(String description) {
        ProfilesContainerException prometheusAdminException = generateException();
        prometheusAdminException.setDescription(description);
        return prometheusAdminException;
    }

}
