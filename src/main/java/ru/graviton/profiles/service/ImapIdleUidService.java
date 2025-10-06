package ru.graviton.profiles.service;

import com.sun.mail.imap.IMAPFolder;
import jakarta.annotation.PostConstruct;
import jakarta.mail.*;
import jakarta.mail.event.MessageCountAdapter;
import jakarta.mail.event.MessageCountEvent;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.graviton.profiles.dto.MailMessage;

import java.io.InputStream;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Slf4j
@Service
public class ImapIdleUidService {

    private final Store store;
    private volatile long lastUid = -1; // UID последнего обработанного письма

    public ImapIdleUidService(Store store) {
        this.store = store;
    }

    @Getter
    private final List<OnMessageReceivedListener> listeners = new CopyOnWriteArrayList<>();

    public interface OnMessageReceivedListener {
        void onMessageReceived(MailMessage msg);

        boolean isAccepted(MailMessage mailMessage);
    }

    @PostConstruct
    public void startIdleListener() {
        new Thread(this::runIdleLoop).start();
    }

    private void runIdleLoop() {
        while (true) {
            try {
                IMAPFolder inbox = (IMAPFolder) store.getFolder("INBOX");
                inbox.open(Folder.READ_WRITE);

                // если первый запуск → зафиксируем последний UID
                if (lastUid == -1 && inbox.getMessageCount() > 0) {
                    lastUid = inbox.getUID(inbox.getMessage(inbox.getMessageCount()));
                    log.info("Инициализация. Последний UID={} ", lastUid);
                }

                inbox.addMessageCountListener(new MessageCountAdapter() {
                    @Override
                    public void messagesAdded(MessageCountEvent event) {
                        try {
                            for (Message msg : event.getMessages()) {
                                long uid = inbox.getUID(msg);
                                if (uid > lastUid) {
                                    log.info("new message received: {}", uid);
                                    MailMessage build = MailMessage.builder()
                                            .body(msg.getContent().toString())
                                            .sender(msg.getFrom()[0].toString())
                                            .subject(msg.getSubject())
                                            .build();
                                    msg.setFlag(Flags.Flag.SEEN, true);

                                    if (msg.isMimeType("multipart/*")) {
                                        Multipart multipart = (Multipart) msg.getContent();
                                        for (int i = 0; i < multipart.getCount(); i++) {
                                            BodyPart bodyPart = multipart.getBodyPart(i);

                                            String disposition = bodyPart.getDisposition();
                                            if (Part.ATTACHMENT.equalsIgnoreCase(disposition) ||
                                                    (bodyPart.getFileName() != null && !bodyPart.getFileName().isEmpty())) {

                                                String fileName = bodyPart.getFileName();
                                                log.info("attachment found: {}", fileName);

                                                try (InputStream is = bodyPart.getInputStream()) {
                                                    build.getAttachments().put(fileName, is.readAllBytes());
                                                }
                                            }
                                        }

                                    }
                                    listeners.forEach(l -> {
                                        if (l.isAccepted(build)) {
                                            l.onMessageReceived(build);
                                        }
                                    });
                                    lastUid = uid;
                                }
                            }
                        } catch (Exception e) {
                            log.error("Error processing message", e);
                        }
                    }
                });

                // бесконечный цикл IDLE
                while (true) {
                    try {
                        inbox.idle();
                    } catch (FolderClosedException ex) {
                        // если idle-сессия упала → пересоздаём цикл
                        log.info("⚠️ IDLE сессия разорвана. Переподключение...");
                        break;
                    }
                }

            } catch (Exception e) {
                log.error("Error processing message", e);
                try {
                    Thread.sleep(5000); // пауза перед переподключением
                } catch (InterruptedException ignored) {
                }
            }
        }
    }
}
