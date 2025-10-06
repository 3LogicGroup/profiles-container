package ru.graviton.profiles.service;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class EmailService {
    private final JavaMailSender emailSender;

    @Value("${spring.mail.from}")
    private String from;

    public boolean sendMail(String recipient, String subject, String html, Map<String, byte[]> attachments) {
        try {
            MimeMessage mimeMessage = emailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8"); // <-- multipart = true

            helper.setFrom(from);
            helper.setTo(recipient);
            helper.setSubject(subject);
            helper.setText(html, true); // true = HTML

            // 🔹 Добавляем вложения, если есть
            if (attachments != null) {
                for (Map.Entry<String, byte[]> entry : attachments.entrySet()) {
                    helper.addAttachment(entry.getKey(), new ByteArrayResource(entry.getValue()));
                }
            }

            emailSender.send(mimeMessage);
            return true;

        } catch (Exception e) {
            throw new RuntimeException("Ошибка при отправке письма", e);
        }
    }
}