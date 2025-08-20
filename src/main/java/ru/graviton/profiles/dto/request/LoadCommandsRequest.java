package ru.graviton.profiles.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoadCommandsRequest {

    private String url;
    private String login;
    private String password;
}
