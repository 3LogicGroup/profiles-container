package ru.graviton.profiles.mapper;

import jakarta.annotation.Nullable;
import org.mapstruct.Mapper;
import org.mapstruct.Named;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Optional;

/**
 * Конвертер для времени и даты
 */
@Mapper(componentModel = "spring")
public interface TimeMapper {
    String LOCAL_DATE_TIME_TO_OFFSET_DATE_TIME = "localDateTimeToOffsetDateTime";
    String OFFSET_DATE_TIME_LOCAL_DATE_TIME = "offsetDateTimeToLocalDateTime";
    /**
     * DURATION_TO_SECONDS
     */
    String DURATION_TO_SECONDS = "durationToSeconds";

    /**
     * SECONDS_TO_DURATION
     */
    String SECONDS_TO_DURATION = "secondsToDuration";

    /**
     * DURATION_TO_MINUTES
     */
    String DURATION_TO_MINUTES = "durationToMinutes";

    /**
     * MINUTES_TO_DURATION
     */
    String MINUTES_TO_DURATION = "minutesToDuration";

    /**
     * Duration to long seconds
     *
     * @param duration исходная длительность
     * @return секунды
     */
    @Nullable
    @Named(DURATION_TO_SECONDS)
    default Integer toSeconds(@Nullable Duration duration) {
        if (duration == null) {
            return null;
        }
        return (int) duration.toSeconds();
    }

    /**
     * Seconds to duration
     *
     * @param seconds секунды
     * @return длительность
     */
    @Nullable
    @Named(SECONDS_TO_DURATION)
    default Duration toDuration(@Nullable Integer seconds) {
        if (seconds == null) {
            return null;
        }
        return Duration.ofSeconds(seconds);
    }

    /**
     * Seconds to minutes
     *
     * @param duration интервал
     * @return минуты
     */
    @Nullable
    @Named(DURATION_TO_MINUTES)
    default Integer secondsToMinutes(@Nullable Duration duration) {
        if (duration == null) {
            return null;
        }
        return (int) duration.toMinutes();
    }

    /**
     * minutesToDuration
     *
     * @param seconds секунды
     * @return минуты
     */
    @Nullable
    @Named(MINUTES_TO_DURATION)
    default Duration minutesToDuration(@Nullable Integer seconds) {
        if (seconds == null) {
            return null;
        }
        return Duration.ofMinutes(seconds);
    }

    @Named(LOCAL_DATE_TIME_TO_OFFSET_DATE_TIME)
    default OffsetDateTime localDateTimeToOffsetDateTime(LocalDateTime localDateTime) {
        return Optional.ofNullable(localDateTime)
                .map(x -> x.atOffset(ZoneOffset.UTC))
                .orElse(null);

    }

    @Named(OFFSET_DATE_TIME_LOCAL_DATE_TIME)
    default LocalDateTime offsetDateTimeToLocalDateTime(OffsetDateTime offsetDateTime) {
        return Optional.ofNullable(offsetDateTime)
                .map(OffsetDateTime::toLocalDateTime)
                .orElse(null);
    }
}
