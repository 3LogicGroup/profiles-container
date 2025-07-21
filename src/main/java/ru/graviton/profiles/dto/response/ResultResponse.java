package ru.graviton.profiles.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Generic-результатов ответа
 */
@Getter
@NoArgsConstructor
public class ResultResponse<T> {

    private T result;

    protected ResultResponse(T result) {
        this.result = result;
    }

    public static <T> ResultResponse<T> of(T data) {
        return new ResultResponse<>(data);
    }

    public String toString() {
        return "ResultResponse(result=" + this.getResult() ;
    }
}
