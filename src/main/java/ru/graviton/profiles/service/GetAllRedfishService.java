package ru.graviton.profiles.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import ru.graviton.profiles.dto.Path;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class GetAllRedfishService {

    private final RestClient restClient;

    private final static String idField = "@odata.id";
    private final static Set<String> ignoredBlocks = Set.of("Links");

    public Map<String, Object> fetchActions(String url, String login, String password) {
        Map<String, Object> actions = new HashMap<>();
        Set<Path> walkedPaths = new HashSet<>();
        fetchActionsInternal(url, login, password, new Path("/redfish/v1", null, null), actions, walkedPaths);
        return actions;
    }

    private void addAuthorization(HttpHeaders headers, String username, String password) {
        String auth = username + ":" + password;
        String authHeader = Base64.getEncoder().encodeToString(auth.getBytes(StandardCharsets.UTF_8));
        headers.set("Authorization", "Basic " + authHeader);
    }

    private void fetchActionsInternal(String url, String login, String password, Path path, Map<String, Object> parentMap, Set<Path> walkedPaths) {
        if (path.getPath().contains("#")) {
            return;
        }
        Map<String, Object> redfishNodeInfo = null;
        try {
            redfishNodeInfo = restClient.get()
                    .uri(url + path.getPath())
                    .headers(headers -> addAuthorization(headers, login,
                            password))
                    .retrieve()
                    .toEntity(new ParameterizedTypeReference<Map<String, Object>>() {
                    }).getBody();
        } catch (Exception e) {
            log.warn(e.getMessage(), e);
        }
        walkedPaths.add(path);
        if (redfishNodeInfo == null) {
            return;
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
        if (parentMap.isEmpty()) {
            parentMap.putAll(redfishNodeInfo);
        } else {
            Map<String, Object> pathInMap = findPathInMap(parentMap, path.getPath());
            if (pathInMap != null) {
                pathInMap.put("data", redfishNodeInfo);
            }
        }

        extractLinkPaths(list, path);
        path.getChildren().removeIf(s -> s.getPath().endsWith("Logs"));
        path.getChildren().removeIf(s -> s.getPath().endsWith(".json"));
        path.getChildren().remove(path);
        Map<String, List<Path>> pathMap = path.getChildren().stream()
                .collect(Collectors.groupingBy(Path::getDirectory));
        path.getChildren().addAll(pathMap.values()
                .stream()
                .flatMap(Collection::stream)
                .toList());

        for (Path f : path.getChildren()) {
            fetchActionsInternal(url, login, password, f, redfishNodeInfo, walkedPaths);
        }

    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> findPathInMap(Object map, String path) {
        if (map instanceof Map<?, ?> m) {
            Object o1 = m.get(idField);
            if (path.equals(o1)) {
                return (Map<String, Object>) m;
            }
            for (Map.Entry<?, ?> entry : m.entrySet()) {
                Object v = entry.getValue();
                if (v instanceof Map<?, ?>) {
                    Map<String, Object> pathInMap = findPathInMap(v, path);
                    if (pathInMap != null) {
                        return pathInMap;
                    }
                } else if (v instanceof Collection<?> collection) {
                    for (Object o : collection) {
                        Map<String, Object> pathInMap = findPathInMap(o, path);
                        if (pathInMap != null) {
                            return pathInMap;
                        }
                    }
                }
            }
        } else if (map instanceof Collection<?> col) {
            for (Object c : col) {
                Map<String, Object> pathInMap = findPathInMap(c, path);
                if (pathInMap != null) {
                    return pathInMap;
                }
            }
        }

        return null;
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
