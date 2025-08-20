package ru.graviton.profiles.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public class ActionDto {
    private String service;
    private String name;
    private String target;
    private String description;
    private Map<Integer, Pair<String, String>> pathParameters = new HashMap<>();

    private List<ActionParameter> actionParameters = new ArrayList<>();
}
