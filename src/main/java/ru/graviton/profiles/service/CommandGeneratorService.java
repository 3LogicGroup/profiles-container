package ru.graviton.profiles.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import ru.graviton.profiles.dto.*;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommandGeneratorService {

    private final RestClient restClient;

    private final ObjectMapper objectMapper;

    private final static String idField = "@odata.id";
    private final static String actionField = "Actions";
    private final static String targetField = "target";
    private final static String actionInfoField = "@Redfish.ActionInfo";
    private final static String descriptionField = "Description";
    private final static String parametersField = "Parameters";
    private final static Set<String> ignoredBlocks = Set.of("Links");

    private void addAuthorization(HttpHeaders headers, String username, String password) {
        String auth = username + ":" + password;
        String authHeader = Base64.getEncoder().encodeToString(auth.getBytes(StandardCharsets.UTF_8));
        headers.set("Authorization", "Basic " + authHeader);
    }

    public List<ActionDto> fetchActions(String url, String username, String password) {
        List<ActionHolder> actions = new ArrayList<>();
        Set<Path> walkedPaths = new HashSet<>();
        fetchActionsInternal(url, username, password, new Path("/redfish/v1", null, null), actions, walkedPaths);
        return extractActions(url, username, password, actions);
    }

    private List<ActionDto> extractActions(String url, String username, String password, List<ActionHolder> data) {
        List<ActionDto> result = new ArrayList<>();
        extractActionsInternal(url, username, password, data, result);
        return result;
    }

    private void extractActionsInternal(String url, String username, String password, List<ActionHolder> data, List<ActionDto> result) {
        data.forEach(d -> d.getAction().forEach((k, v) -> {
            if (k.startsWith("#")) {
                ActionDto actionDto = new ActionDto();
                result.add(actionDto);
                actionDto.setName(k.substring(1));
                actionDto.setService(d.getId());
                Path path = d.getPath();
                ConstructedPath constructedPath = constructParams(path);
                actionDto.setPathParameters(constructedPath.getParams());
                if (v instanceof Map<?, ?> m) {
                    actionDto.setTarget(constructTarget(constructedPath, (String) m.get(targetField)));
                    String actionInfoPath = (String) m.get(actionInfoField);
                    if (actionInfoPath != null) {
                        Map<String, ?> actionInfoMap = restClient.get()
                                .uri(url + actionInfoPath)
                                .headers(headers -> addAuthorization(headers, username,
                                        password))
                                .retrieve()
                                .toEntity(new ParameterizedTypeReference<Map<String, ?>>() {
                                }).getBody();
                        actionDto.setDescription((String) actionInfoMap.get(descriptionField));
                        List<?> parametersList = (List<?>) actionInfoMap.get(parametersField);
                        if (parametersList != null) {
                            List<ActionParameter> list = parametersList.stream()
                                    .filter(f -> f instanceof Map<?, ?>)
                                    .map(x -> (Map<?, ?>) x)
                                    .map(x -> objectMapper.convertValue(x, ActionParameter.class))
                                    .toList();
                            actionDto.setActionParameters(list);
                        }
                    }
                }
            }
        }));
    }

    private String constructTarget(ConstructedPath constructedPath, String targetUrl) {
        if (!constructedPath.getParams().isEmpty()) {
            String[] targetUrlArray = targetUrl.split("/");
            constructedPath.getParams().keySet().stream().sorted(Integer::compareTo)
                    .forEach(p -> {
                        Pair<String, String> stringStringPair = constructedPath.getParams().get(p);
                        String[] split = stringStringPair.getRight().split("/");
                        targetUrlArray[split.length] = stringStringPair.getLeft();
                    });
            return String.join("/", targetUrlArray);
        }
        return targetUrl;
    }

    private ConstructedPath constructParams(Path path) {
        List<PathPart> parts = new ArrayList<>();
        Map<Integer, Pair<String, String>> params = new HashMap<>();
        while (path.getParentPath() != null) {
            if (path.getDirectory().equals("Members")) {
                String pathSource;
                if (path.getParentPath().getRelativePath() != null) {
                    pathSource = path.getParentPath().getRelativePath();
                } else {
                    pathSource = path.getParentPath().getPath();
                }
                String paramName = pathSource.substring(pathSource.lastIndexOf("/") + 1) + "Id";
                parts.addFirst(new PathPart(String.format("/{%s}", paramName), true));
            } else {
                parts.addFirst(new PathPart(path.getRelativePath(), false));
            }
            path = path.getParentPath();
        }
        parts.addFirst(new PathPart(path.getPath(), false));

        for (int i = 0; i < parts.size(); i++) {
            PathPart pathPart = parts.get(i);
            if (pathPart.isVariable()) {
                params.put(i, Pair.of(pathPart.getPath().substring(1), parts.subList(0, i).stream()
                        .map(PathPart::getPath)
                        .collect(Collectors.joining())));
            }
        }

        String finalPath = parts.stream()
                .map(PathPart::getPath)
                .collect(Collectors.joining());
        return new ConstructedPath(finalPath, params);
    }

    @Getter
    @Setter
    @AllArgsConstructor
    private static class ConstructedPath {
        private String path;
        private Map<Integer, Pair<String, String>> params;
    }

    @AllArgsConstructor
    @Getter
    @Setter
    private static class PathPart {
        private String path;
        private boolean isVariable;
    }

    private void fetchActionsInternal(String url, String username, String password, Path path, List<ActionHolder> actions, Set<Path> walkedPaths) {
        if (walkedPaths.contains(path)) {
            return;
        }
        Map<String, ?> redfishNodeInfo = null;
        try {
            redfishNodeInfo = restClient.get()
                    .uri(url + path.getPath())
                    .headers(headers -> addAuthorization(headers, username,
                            password))
                    .retrieve()
                    .toEntity(new ParameterizedTypeReference<Map<String, ?>>() {
                    }).getBody();
        } catch (Exception e) {
            log.warn(e.getMessage(), e);
        }
        walkedPaths.add(path);
        if (redfishNodeInfo == null) {
            return;
        }
        if (redfishNodeInfo.get(actionField) instanceof Map<?, ?> m) {
            actions.add(new ActionHolder((String) redfishNodeInfo.get("Id"), (Map<String, ?>) m, path));
            redfishNodeInfo.remove(actionField);
        }

        ignoredBlocks.forEach(redfishNodeInfo::remove);
        Set<String> collect = redfishNodeInfo.keySet().stream()
                .filter(f -> f.startsWith("@"))
                .collect(Collectors.toSet());
        for (String string : collect) {
            redfishNodeInfo.remove(string);
        }
        Map<String, ?> list = redfishNodeInfo.entrySet()
                .stream()
                .filter(f -> f.getValue() instanceof Map<?, ?> || f.getValue() instanceof Collection<?>)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        extractLinkPaths(list, path);
        path.getChildren().removeIf(s -> s.getPath().endsWith("Logs"));
        path.getChildren().removeIf(s -> s.getPath().endsWith(".json"));
        path.getChildren().remove(path);
        Map<String, List<Path>> pathMap = path.getChildren().stream()
                .collect(Collectors.groupingBy(Path::getDirectory));
        if (pathMap.containsKey("Members")) {
            pathMap.get("Members").forEach(path.getChildren()::remove);
            path.getChildren().add(pathMap.get("Members").getFirst());
            pathMap.remove("Members");
        }
        path.getChildren().addAll(pathMap.values()
                .stream()
                .flatMap(Collection::stream)
                .toList());
        path.getChildren().forEach(f -> fetchActionsInternal(url, username, password, f, actions, walkedPaths));

    }

    private void extractLinkPaths(Map<String, ?> nodeData, Path parentPath) {
        nodeData.forEach((k, v) -> extractLinkPathsInternal(v, k, parentPath));
    }

    private void extractLinkPathsInternal(Object nodeData, String directory, Path parentPath) {
        if (nodeData instanceof Map<?, ?> map) {
            String path = (String) Optional.ofNullable(map.get(idField))
                    .filter(f -> f instanceof String).orElse(null);
            if (path != null) {
                Path p = new Path(path, parentPath, directory);
                parentPath.getChildren().add(p);
            } else {
                extractLinkPathsInternal(map.values(), directory, parentPath);
            }
        } else if (nodeData instanceof Collection<?> collection) {
            collection.forEach(c -> extractLinkPathsInternal(c, directory, parentPath));

        }
    }
}
