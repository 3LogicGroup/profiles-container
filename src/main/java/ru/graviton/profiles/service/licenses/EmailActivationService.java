package ru.graviton.profiles.service.licenses;

import com.fasterxml.jackson.core.type.TypeReference;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.graviton.profiles.dto.HybridEncrypted;
import ru.graviton.profiles.dto.MailMessage;
import ru.graviton.profiles.dto.license.ActivationMethod;
import ru.graviton.profiles.dto.license.response.OnlineActivationResponse;
import ru.graviton.profiles.service.EmailService;
import ru.graviton.profiles.service.ImapIdleUidService;
import ru.graviton.profiles.utils.SecureJsonUtils;

import java.nio.charset.StandardCharsets;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailActivationService {

    private final ImapIdleUidService imapIdleUidService;
    private final ActivationService activationService;
    private final EmailService emailService;

    @PostConstruct
    private void init() {
        imapIdleUidService.getListeners().add(new ImapIdleUidService.OnMessageReceivedListener() {
            @Override
            public void onMessageReceived(MailMessage msg) {
                processRequest(msg);
            }

            @Override
            public boolean isAccepted(MailMessage mailMessage) {
                return mailMessage.getAttachments().containsKey("request_activation.lic");
            }
        });
    }

    private void processRequest(MailMessage mailMessage) {
        byte[] request = mailMessage.getAttachments().get("request_activation.lic");
        if (request == null) {
            log.warn("No request attachment found");
            return;
        }
        HybridEncrypted hybridEncrypted = SecureJsonUtils.fromJson(new String(request, StandardCharsets.UTF_8), new TypeReference<>() {
        });
        OnlineActivationResponse onlineActivationResponse = activationService.processActivation(hybridEncrypted, ActivationMethod.EMAIL,
                null, null);

        if (onlineActivationResponse.isSuccess()) {
            emailService.sendMail(mailMessage.getSender(), "3Logic activation", "Во вложении файл активации. Загрузите его в приложении",
                    Map.of("response_activation.lic", SecureJsonUtils.toJson(onlineActivationResponse.getEncrypted()).getBytes(StandardCharsets.UTF_8)));
        }else{
            emailService.sendMail(mailMessage.getSender(), "3Logic activation", "Ошибка активации лицензии. Обратитесь в службу технической поддержки",
                    Map.of());
        }

    }
}