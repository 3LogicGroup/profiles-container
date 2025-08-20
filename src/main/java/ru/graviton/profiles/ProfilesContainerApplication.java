package ru.graviton.profiles;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(exclude = {UserDetailsServiceAutoConfiguration.class})
@EnableFeignClients
@EnableScheduling
public class ProfilesContainerApplication {

    public static void main(String[] args) {
        System.setProperty("jdk.internal.httpclient.disableHostnameVerification","true");
        SpringApplication.run(ProfilesContainerApplication.class, args);

    }

}
