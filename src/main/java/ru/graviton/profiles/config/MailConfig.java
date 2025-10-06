package ru.graviton.profiles.config;

import jakarta.mail.Session;
import jakarta.mail.Store;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

@Configuration
public class MailConfig {

    @Value("${spring.mail.properties.mail.imap.host}")
    private String mailHost;

    @Value("${spring.mail.properties.mail.imap.username}")
    private String username;

    @Value("${spring.mail.properties.mail.imap.password}")
    private String password;

    @Value("${spring.mail.properties.mail.imap.port}")
    private Integer port;

    @Bean
    public Store mailStore() throws Exception {
        Properties props = new Properties();
        props.put("mail.store.protocol", "imaps");
        props.put("mail.host", mailHost);
        props.put("mail.port", port);
        props.put("mail.imap.ssl.enable", "true");

        Session session = Session.getDefaultInstance(props);
        Store store = session.getStore("imaps");
        store.connect(username, password);
        return store;
    }
}