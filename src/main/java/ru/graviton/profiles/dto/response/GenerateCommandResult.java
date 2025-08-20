package ru.graviton.profiles.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class GenerateCommandResult {
    private int count;
    private List<String> commandNames = new ArrayList<>();
}
