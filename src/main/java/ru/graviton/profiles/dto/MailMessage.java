package ru.graviton.profiles.dto;

import lombok.*;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MailMessage {

    private String subject;
    private String body;
    private String sender;
    @Builder.Default
    private Map<String, byte[]> attachments = new HashMap<>();
}
