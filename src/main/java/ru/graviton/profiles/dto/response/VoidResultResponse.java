package ru.graviton.profiles.dto.response;

/**
 * Ответ без возврата результата
 */
public class VoidResultResponse extends ResultResponse<Void> {

    public VoidResultResponse() {
        super(null);
    }

    public static VoidResultResponse response() {
        return new VoidResultResponse();
    }

    public String toString() {
        return "VoidResultResponse(super=" + super.toString() + ")";
    }
}
