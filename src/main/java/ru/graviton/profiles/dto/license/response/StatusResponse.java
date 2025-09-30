package ru.graviton.profiles.dto.license.response;

import lombok.Data;
import lombok.Builder;

@Data
@Builder
public class StatusResponse {
    
    private String status;
    
    private String message;
    
    public static StatusResponse success(String message) {
        return StatusResponse.builder()
                .status("SUCCESS")
                .message(message)
                .build();
    }
    
    public static StatusResponse error(String message) {
        return StatusResponse.builder()
                .status("ERROR")
                .message(message)
                .build();
    }
}